package sample.jetpack.compose.repository

import kotlinx.coroutines.flow.Flow

interface SampleRepository {

	suspend fun performLogin(userName : String, password : String) : Flow<Boolean>

}
