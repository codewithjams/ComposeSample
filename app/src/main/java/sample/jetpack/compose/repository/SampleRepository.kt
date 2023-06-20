package sample.jetpack.compose.repository

import kotlinx.coroutines.flow.Flow
import sample.jetpack.compose.repository.rest.HTTPFileTransferResult

interface SampleRepository {

	suspend fun performLogin(userName : String, password : String) : Flow<Boolean>

	suspend fun startDownload(fileName: String, url: String) : Flow<HTTPFileTransferResult>

	suspend fun pauseDownload()

	suspend fun resumeDownload(fileName : String, url : String) : Flow<HTTPFileTransferResult>

}
