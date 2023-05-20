package sample.jetpack.compose.mvi.repository

import kotlinx.coroutines.delay

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

import sample.jetpack.compose.repository.SampleRepository

class FakeSampleRepository : SampleRepository {

	override suspend fun performLogin(userName : String, password : String) : Flow<Boolean> =
		flow {
			delay(1000)
			emit(true)
		}

}
