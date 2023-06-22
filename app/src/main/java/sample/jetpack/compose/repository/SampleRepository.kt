package sample.jetpack.compose.repository

import kotlinx.coroutines.flow.Flow

import sample.jetpack.compose.repository.rest.HTTPFileTransferResult

import java.io.File

interface SampleRepository {

	suspend fun performLogin(userName : String, password : String) : Flow<Boolean>

	suspend fun startDownload(fileName: String, url: String) : Flow<HTTPFileTransferResult>

	suspend fun pauseDownload()

	suspend fun resumeDownload() : Flow<HTTPFileTransferResult>

	suspend fun startUpload(file: File, mimeType: String, url : String) : Flow<HTTPFileTransferResult>

	suspend fun pauseUpload()

	suspend fun resumeUpload() : Flow<HTTPFileTransferResult>

}
