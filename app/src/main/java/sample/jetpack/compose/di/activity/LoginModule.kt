package sample.jetpack.compose.di.activity

import dagger.Module
import dagger.Provides

import sample.jetpack.compose.mvi.action.LoginAction

import sample.jetpack.compose.mvi.middleware.LoggingMiddleWare
import sample.jetpack.compose.mvi.middleware.LoginMiddleWare

import sample.jetpack.compose.mvi.state.LoginState

import sample.jetpack.compose.repository.SampleRepository

@Module
class LoginModule {

	@Provides
	fun providesLoginMiddleWare(repository : SampleRepository) : LoginMiddleWare =
		LoginMiddleWare(repository)

	@Provides
	fun providesLoggingMiddleWare() : LoggingMiddleWare<LoginState, LoginAction> =
		LoggingMiddleWare()

}
