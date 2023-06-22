package sample.jetpack.compose.repository

import kotlinx.coroutines.flow.Flow

import sample.jetpack.compose.repository.rest.HTTPFileTransferResult

import java.io.File

interface SampleRepository {

	val httpFileTransferResultFlow: Flow<HTTPFileTransferResult>

	suspend fun performLogin(userName : String, password : String) : Flow<Boolean>

	suspend fun startDownload(
		fileName: String,
		url: String,
		headers: Map<String, String> = emptyMap()
	)

	suspend fun pauseDownload()

	suspend fun resumeDownload()

	suspend fun startUpload(
		file: File,
		mimeType: String,
		url : String,
		headers: Map<String, String> = emptyMap()
	)

	suspend fun pauseUpload()

	suspend fun resumeUpload()

}
