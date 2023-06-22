package sample.jetpack.compose.repository.rest

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor

import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.Sink
import okio.buffer

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Streaming
import retrofit2.http.Url

import java.io.BufferedOutputStream
import java.io.File
import java.io.InputStream

import java.net.ConnectException
import java.net.SocketTimeoutException

import java.util.concurrent.TimeUnit

private const val DEFAULT_READ_BUFFER_SIZE = 2_048

class HTTPFileTransfer {

	private val service: HTTPFileTransferService by lazy {

		val loggingInterceptor = HttpLoggingInterceptor().also { interceptor ->
			interceptor.level = HttpLoggingInterceptor.Level.HEADERS
		}

		val client = OkHttpClient.Builder().apply {

			readTimeout(60, TimeUnit.SECONDS)
			connectTimeout(3, TimeUnit.MINUTES)

			addInterceptor(loggingInterceptor)

		}.build()

		val retrofit = Retrofit.Builder().apply {
			baseUrl("http://www.example.com")
			client(client)
		}.build()

		retrofit.create(HTTPFileTransferService::class.java)

	}

	private val downloadCallback: Callback<ResponseBody> = object : Callback<ResponseBody> {

		override fun onResponse(call : Call<ResponseBody>, response : Response<ResponseBody>) {
			onDownloadFileResponse(call, response)
		}

		override fun onFailure(call : Call<ResponseBody>, cause : Throwable) {

			downloadBuilder?.getFile()?.also { file ->
				if (file.exists()) {
					file.delete()
				}
			}

			onTransferFailure(cause)

			downloadBuilder = null
			downloadCall = null

			resetTransferTracker()

		}

	}

	private val uploadCallback: Callback<ResponseBody> = object : Callback<ResponseBody> {

		override fun onResponse(call : Call<ResponseBody>, response : Response<ResponseBody>) {
			onUploadFileResponse(call, response)
		}

		override fun onFailure(call : Call<ResponseBody>, cause : Throwable) {

			onTransferFailure(cause)

			uploadBuilder = null
			uploadCall = null

			resetTransferTracker()

		}

	}

	private val transferTracker: TransferTracker = TransferTracker()

	private var _resultFlow : MutableStateFlow<HTTPFileTransferResult> =
		MutableStateFlow(HTTPFileTransferResult.None)

	private var uploadBuilder : UploadBuilder? = null
	private var downloadBuilder : DownloadBuilder? = null

	private var downloadCall: Call<ResponseBody>? = null
	private var uploadCall: Call<ResponseBody>? = null

	val resultFlow : StateFlow<HTTPFileTransferResult>
		get() = _resultFlow

	fun startDownload(
		fileName : String,
		filePath : File,
		url : String,
		headers : Map<String, String>
	) {

		downloadBuilder = DownloadBuilder(fileName, filePath, url, headers).also { builder ->
			transferTracker.status = TransferTracker.Status.START
			prepareDownload(builder)
		}

		downloadCall?.enqueue(downloadCallback)

	}

	fun pauseDownload() {
		transferTracker.status = TransferTracker.Status.PAUSE
	}

	fun resumeDownload() {

		downloadBuilder?.let { builder ->
			transferTracker.status = TransferTracker.Status.RESUME
			prepareDownload(builder)
		}

		downloadCall?.enqueue(downloadCallback)

	}

	fun startUpload(
		file : File,
		mimeType : String,
		url : String,
		headers : Map<String, String>
	) {

		uploadBuilder = UploadBuilder(file, mimeType, url, headers).also { builder ->
			transferTracker.status = TransferTracker.Status.START
			prepareUpload(builder)
		}

		uploadCall?.enqueue(uploadCallback)

	}

	fun pauseUpload() {
		transferTracker.status = TransferTracker.Status.PAUSE
	}

	fun resumeUpload() {

		uploadBuilder?.let { builder ->
			transferTracker.status = TransferTracker.Status.RESUME
			prepareUpload(builder)
		}

		uploadCall?.enqueue(uploadCallback)

	}

	private fun prepareDownload(builder : DownloadBuilder) {

		val file = builder.getFile()

		val headers : Map<String, String> = mutableMapOf<String, String>().apply {
			put("Connection", "keep-alive")
			if (file.exists() && transferTracker.status == TransferTracker.Status.RESUME) {
				put("Range", "bytes=${transferTracker.transferredBytes}-")
			}
			putAll(builder.headers)
		}

		propagateResult(HTTPFileTransferResult.Progress.Started)

		downloadCall = service.downloadFile(builder.url, headers)

	}

	private fun onDownloadFileResponse(call: Call<ResponseBody>, response : Response<ResponseBody>) {

		val file = downloadBuilder?.getFile() ?: return

		if (!response.isSuccessful) {
			propagateResult(
				HTTPFileTransferResult.Failed.Unknown(
					IllegalStateException("Request is unsuccessful"),
					"Request is unsuccessful"
				)
			)
			return
		}

		if (response.body() == null) {
			propagateResult(
				HTTPFileTransferResult.Failed.Unknown(
					IllegalStateException("Request is successful but no Response"),
					"Request is successful but no Response"
				)
			)
			return
		}

		response.body()?.let { responseBody ->
			responseBody.byteStream().use inputStream@{ inputStream ->
				BufferedOutputStream(file.outputStream()).use outputStream@{ outputStream ->
					writeFileToFileSystem(
						inputStream = inputStream,
						outputStream = outputStream,
						responseBody = responseBody,
						file = file,
						onCallCancelled = {
							call.cancel()
						},
						onDownloadCompleted = {

							propagateResult(HTTPFileTransferResult.Success)

							downloadBuilder = null
							downloadCall = null

							resetTransferTracker()

						}
					)
				}
			}
		}

	}

	private fun writeFileToFileSystem(
		inputStream : InputStream,
		outputStream : BufferedOutputStream,
		responseBody : ResponseBody,
		file: File,
		onCallCancelled: () -> Unit,
		onDownloadCompleted: () -> Unit
	) {

		val totalBytes : Long = responseBody.contentLength().also { bytes ->
			transferTracker.totalBytes = bytes
		}

		var progressBytes =
			if (file.exists() && transferTracker.status == TransferTracker.Status.RESUME) {
				transferTracker.transferredBytes
			} else {
				0L
			}

		val data = ByteArray(DEFAULT_READ_BUFFER_SIZE)

		while (true) {

			if (transferTracker.status == TransferTracker.Status.PAUSE) {
				transferTracker.transferredBytes = progressBytes
				onCallCancelled.invoke()
				outputStream.flush()
				propagateResult(HTTPFileTransferResult.Progress.Paused)
				return
			}

			val bytes = inputStream.read()

			if (bytes == -1)
				break

			outputStream.write(data, 0, bytes)

			progressBytes += bytes

			propagateResult(HTTPFileTransferResult.Progress.Transferred(progressBytes, totalBytes))

		}

		outputStream.flush()

		onDownloadCompleted.invoke()

	}

	private fun prepareUpload(builder : UploadBuilder) {

		val requestBody = ProgressRequestBody(
			delegate = builder.file.asRequestBody(),
			callback = { uploaded, total ->
				propagateResult(HTTPFileTransferResult.Progress.Transferred(uploaded, total))
			}
		)

		val part : MultipartBody.Part = MultipartBody.Part.createFormData(
			name = builder.mimeType,
			filename = builder.file.name,
			requestBody
		)

		val headers : Map<String, String> = mutableMapOf<String, String>().apply {
			put("Connection", "keep-alive")
			putAll(builder.headers)
		}

		propagateResult(HTTPFileTransferResult.Progress.Started)

		uploadCall = service.uploadFile(builder.url, headers, part)

	}

	@Suppress("UNUSED_PARAMETER")
	private fun onUploadFileResponse(call : Call<ResponseBody>, response : Response<ResponseBody>) {

		propagateResult(HTTPFileTransferResult.Success)

		uploadBuilder = null
		uploadCall = null

		resetTransferTracker()

	}

	private fun onTransferFailure(cause : Throwable) {

		val message : String = cause.message ?: "Something went wrong"

		propagateResult(
			when (cause) {
				is ConnectException       -> HTTPFileTransferResult.Failed.NoConnection(message)
				is SecurityException      -> HTTPFileTransferResult.Failed.MissingPermission(message)
				is SocketTimeoutException -> HTTPFileTransferResult.Failed.TimeOut(message)
				else                      -> HTTPFileTransferResult.Failed.Unknown(cause, message)
			}
		)

		resetTransferTracker()

	}

	private fun propagateResult(result : HTTPFileTransferResult) {
		_resultFlow.value = result
	}

	private fun resetTransferTracker() {
		transferTracker.apply {
			transferredBytes = -1
			totalBytes = -1
			status = TransferTracker.Status.IDLE
		}
	}

}

private interface HTTPFileTransferService {

	@Streaming
	@GET
	fun downloadFile(
		@Url url : String,
		@HeaderMap headers : Map<String, String>
	) : Call<ResponseBody>

	@Multipart
	@POST
	fun uploadFile(
		@Url url : String,
		@HeaderMap headers : Map<String, String>,
		@Part file : MultipartBody.Part
	) : Call<ResponseBody>

}

data class DownloadBuilder(
	val fileName : String,
	val filePath : File,
	val url : String,
	val headers : Map<String, String>
) {

	fun getFile(): File = File(filePath, fileName)

}

data class UploadBuilder(
	val file : File,
	val mimeType : String,
	val url : String,
	val headers : Map<String, String> = emptyMap()
)

private data class TransferTracker(
	var transferredBytes : Long = -1L,
	var totalBytes : Long = -1L,
	var status : Status = Status.IDLE
) {

	enum class Status {
		IDLE,
		START,
		PAUSE,
		RESUME
	}

}

private class ProgressRequestBody(
	private val delegate : RequestBody,
	val callback : (Long, Long) -> Unit
) : RequestBody() {

	override fun contentType() : MediaType? = delegate.contentType()

	override fun contentLength() : Long = delegate.contentLength()

	override fun writeTo(sink : BufferedSink) {
		val countingSink = CountingSink(sink).buffer()
		delegate.writeTo(countingSink)
		countingSink.flush()
	}

	private inner class CountingSink(delegate : Sink) : ForwardingSink(delegate) {

		private val total = contentLength()
		private var uploaded = 0L

		override fun write(source : Buffer, byteCount : Long) {
			super.write(source, byteCount)
			uploaded += byteCount
			callback.invoke(uploaded, total)
		}

	}

}

sealed class HTTPFileTransferResult {

	object None : HTTPFileTransferResult()

	object Success : HTTPFileTransferResult()

	sealed class Progress : HTTPFileTransferResult() {

		object Started : Progress()

		object Paused : Progress()

		data class Transferred(val transferredBytes : Long, val totalBytes : Long) : Progress()

	}

	sealed class Failed : HTTPFileTransferResult() {

		abstract val message : String

		data class MissingPermission(override val message : String) : Failed()

		data class NoConnection(override val message : String) : Failed()

		data class TimeOut(override val message : String) : Failed()

		data class Unknown(val cause : Throwable, override val message : String) : Failed()

	}

}
