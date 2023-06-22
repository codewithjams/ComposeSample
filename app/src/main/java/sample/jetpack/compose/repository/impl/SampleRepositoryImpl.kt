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

import java.io.File

import javax.inject.Inject

import kotlin.random.Random

class SampleRepositoryImpl @Inject constructor(
	@Suppress("unused") private val resourceUtils : ResourceUtils,
	@ApplicationContext private val context : Context
) : SampleRepository {

	private val httpFileTransfer : HTTPFileTransfer by lazy {
		HTTPFileTransfer()
	}
	override val httpFileTransferResultFlow : Flow<HTTPFileTransferResult>
		get() = httpFileTransfer.resultFlow

	override suspend fun performLogin(userName : String, password : String) : Flow<Boolean> =
		callbackFlow {
			delay(5000)
			trySend(Random.nextBoolean())
			awaitClose()
		}

	override suspend fun startDownload(
		fileName : String,
		url : String,
		headers : Map<String, String>
	) {
		httpFileTransfer.startDownload(fileName, context.filesDir, url, headers)
	}

	override suspend fun pauseDownload() {
		httpFileTransfer.pauseDownload()
	}

	override suspend fun resumeDownload() {
		httpFileTransfer.resumeDownload()
	}

	override suspend fun startUpload(
		file : File,
		mimeType : String,
		url : String,
		headers : Map<String, String>
	) {
		httpFileTransfer.startUpload(file, mimeType, url, headers)
	}

	override suspend fun pauseUpload() {
		httpFileTransfer.pauseUpload()
	}

	override suspend fun resumeUpload() {
		httpFileTransfer.resumeUpload()
	}

}
