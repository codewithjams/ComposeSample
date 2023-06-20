package sample.jetpack.compose.repository.impl

import android.content.Context
import kotlinx.coroutines.channels.awaitClose

import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import sample.jetpack.compose.di.application.qualifier.ApplicationContext

import sample.jetpack.compose.repository.SampleRepository
import sample.jetpack.compose.repository.rest.HTTPFileTransfer
import sample.jetpack.compose.repository.rest.HTTPFileTransferResult

import sample.jetpack.compose.utility.helper.ResourceUtils

import javax.inject.Inject

import kotlin.random.Random

class SampleRepositoryImpl @Inject constructor(
	@Suppress("unused") private val resourceUtils : ResourceUtils,
	@ApplicationContext private val context : Context
) : SampleRepository {

	private val httpFileTransfer : HTTPFileTransfer by lazy {
		HTTPFileTransfer()
	}

	override suspend fun performLogin(userName : String, password : String) : Flow<Boolean> =
		callbackFlow {
			delay(5000)
			trySend(Random.nextBoolean())
			awaitClose()
		}

	override suspend fun startDownload(fileName : String, url : String) : Flow<HTTPFileTransferResult> =
		httpFileTransfer.startDownload(
			fileName,
			context.filesDir,
			url
		)

	override suspend fun pauseDownload() {
		httpFileTransfer.pauseDownload()
	}

	override suspend fun resumeDownload(fileName : String, url : String) : Flow<HTTPFileTransferResult> =
		httpFileTransfer.resumeDownload(
			fileName,
			context.filesDir,
			url
		)

}
