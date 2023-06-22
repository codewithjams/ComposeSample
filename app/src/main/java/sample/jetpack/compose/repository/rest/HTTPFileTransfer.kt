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

import java.net.ConnectException
import java.net.SocketTimeoutException

import java.util.concurrent.TimeUnit

private const val DEFAULT_READ_BUFFER_SIZE = 2_048

class HTTPFileTransfer {

	private lateinit var transferControl : FileTransferControl

	private var lastReadBytes : Long = -1L

	private var _resultFlow = MutableStateFlow<HTTPFileTransferResult>(HTTPFileTransferResult.None)

	val resultFlow : StateFlow<HTTPFileTransferResult>
		get() = _resultFlow

	private var downloadBuilder : DownloadBuilder? = null
	private var uploadBuilder : UploadBuilder? = null

	fun startDownload(
		fileName : String,
		filePath : File,
		url : String
	) {
		downloadBuilder = DownloadBuilder(fileName, filePath, url).also { builder ->
			transferControl = FileTransferControl.START
			downloadFile(builder)
		}
	}

	fun pauseDownload() {
		transferControl = FileTransferControl.PAUSE
	}

	fun resumeDownload() {
		downloadBuilder?.let {
			transferControl = FileTransferControl.RESUME
			downloadFile(it)
		}
	}

	suspend fun startUpload(
		file : File,
		mimeType : String,
		url : String,
		headers : Map<String, String> = emptyMap()
	) {
		uploadBuilder = UploadBuilder(file, mimeType, url, headers).also {
			transferControl = FileTransferControl.START
			uploadFile(it)
		}
	}

	fun pauseUpload() {
		transferControl = FileTransferControl.PAUSE
	}

	suspend fun resumeUpload() {
		uploadBuilder?.let { builder ->
			transferControl = FileTransferControl.RESUME
			uploadFile(builder)
		}
	}

	private fun downloadFile(builder : DownloadBuilder) {

		val loggingInterceptor = HttpLoggingInterceptor()
		loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

		val client = OkHttpClient.Builder().apply {
			readTimeout(60, TimeUnit.SECONDS)
			connectTimeout(3, TimeUnit.MINUTES)
			addInterceptor(loggingInterceptor)
		}.build()

		val retrofit = Retrofit.Builder().apply {
			baseUrl("http://www.example.com")
			client(client)
		}.build()

		val service = retrofit.create(HTTPFileTransferService::class.java)

		val file = builder.getFile()

		val headers : Map<String, String> = mutableMapOf<String, String>().apply {
			put("Connection", "keep-alive")
			if (file.exists() && transferControl == FileTransferControl.RESUME) {
				put("Range", "bytes=${lastReadBytes}-")
			}
			putAll(builder.headers)
		}

		_resultFlow.value = HTTPFileTransferResult.Progress.Started

		service.downloadFile(builder.url, headers).enqueue(
			object: Callback<ResponseBody> {

				override fun onResponse(
					call : Call<ResponseBody>,
					response : Response<ResponseBody>
				) {

					if (!response.isSuccessful) {
						_resultFlow.value = HTTPFileTransferResult.Failed.Unknown(
							IllegalStateException("Request is unsuccessful"),
							"Request is unsuccessful"
						)
						return
					}

					if (response.body() == null) {
						_resultFlow.value = HTTPFileTransferResult.Failed.Unknown(
							IllegalStateException("Request is successful but no Response"),
							"Request is successful but no Response"
						)
						return
					}

					response.body()?.let { responseBody ->
						responseBody.byteStream().use inputStream@{ inputStream ->
							BufferedOutputStream(file.outputStream()).use outputStream@{ outputStream ->

								val totalBytes : Long = responseBody.contentLength()

								var progressBytes =
									if (file.exists() && transferControl == FileTransferControl.RESUME) {
										lastReadBytes
									} else {
										0L
									}

								val data = ByteArray(DEFAULT_READ_BUFFER_SIZE)

								while (true) {

									if (transferControl == FileTransferControl.PAUSE) {
										lastReadBytes = progressBytes
										call.cancel()
										outputStream.flush()
										_resultFlow.value = HTTPFileTransferResult.Progress.Paused
										return@inputStream
									}

									val bytes = inputStream.read()

									if (bytes == -1)
										break

									outputStream.write(data, 0, bytes)

									progressBytes += bytes

									_resultFlow.value = HTTPFileTransferResult.Progress.Transferred(progressBytes, totalBytes)

								}

								outputStream.flush()

							}
						}
						_resultFlow.value = HTTPFileTransferResult.Success
					}

				}

				override fun onFailure(call : Call<ResponseBody>, cause : Throwable) {

					builder.getFile().also { file ->
						if (file.exists()) {
							file.delete()
						}
					}

					val message : String = cause.message ?: "Something went wrong"

					_resultFlow.value = when (cause) {
						is ConnectException       -> HTTPFileTransferResult.Failed.NoConnection(message)
						is SecurityException      -> HTTPFileTransferResult.Failed.MissingPermission(message)
						is SocketTimeoutException -> HTTPFileTransferResult.Failed.TimeOut(message)
						else                      -> HTTPFileTransferResult.Failed.Unknown(cause, message)
					}

				}

			}
		)

	}

	private suspend fun uploadFile(builder : UploadBuilder) {

		val loggingInterceptor = HttpLoggingInterceptor()
		loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS

		val client = OkHttpClient.Builder().apply {
			readTimeout(60, TimeUnit.SECONDS)
			connectTimeout(3, TimeUnit.MINUTES)
			addInterceptor(loggingInterceptor)
		}.build()

		val retrofit = Retrofit.Builder().apply {
			baseUrl("http://www.example.com/")
			client(client)
		}.build()

		val service = retrofit.create(HTTPFileTransferService::class.java)

		val requestBody = ProgressRequestBody(
			delegate = builder.file.asRequestBody(),
			callback = { uploaded, total ->
				_resultFlow.value = HTTPFileTransferResult.Progress.Transferred(uploaded, total)
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

		_resultFlow.value = HTTPFileTransferResult.Progress.Started

		try {

			val response : Response<ResponseBody> = service.uploadFile(builder.url, headers, part)

			if (!response.isSuccessful)
				throw IllegalStateException("Request is unsuccessful")

			_resultFlow.value = HTTPFileTransferResult.Success

			uploadBuilder = null

		} catch (cause : Throwable) {

			val message : String = cause.message ?: "Something went wrong"

			_resultFlow.value = when (cause) {
				is ConnectException       -> HTTPFileTransferResult.Failed.NoConnection(message)
				is SecurityException      -> HTTPFileTransferResult.Failed.MissingPermission(message)
				is SocketTimeoutException -> HTTPFileTransferResult.Failed.TimeOut(message)
				else                      -> HTTPFileTransferResult.Failed.Unknown(cause, message)
			}

			uploadBuilder = null

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
	suspend fun uploadFile(
		@Url url : String,
		@HeaderMap headers : Map<String, String>,
		@Part file : MultipartBody.Part
	) : Response<ResponseBody>

}

data class DownloadBuilder(
	val fileName : String,
	val filePath : File,
	val url : String,
	val headers : Map<String, String> = emptyMap()
) {

	fun getFile(): File = File(filePath, fileName)

}

data class UploadBuilder(
	val file : File,
	val mimeType : String,
	val url : String,
	val headers : Map<String, String> = emptyMap()
)

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

private enum class FileTransferControl {
	START,
	PAUSE,
	RESUME
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
