package sample.jetpack.compose.repository.impl

import kotlinx.coroutines.channels.awaitClose

import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

import sample.jetpack.compose.repository.SampleRepository

import sample.jetpack.compose.utility.helper.ResourceUtils

import javax.inject.Inject

import kotlin.random.Random

class SampleRepositoryImpl @Inject constructor(
	@Suppress("unused") private val resourceUtils : ResourceUtils
) : SampleRepository {

	override suspend fun performLogin(userName : String, password : String) : Flow<Boolean> =
		callbackFlow {
			delay(5000)
			trySend(Random.nextBoolean())
			awaitClose()
		}

}
