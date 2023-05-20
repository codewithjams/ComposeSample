package sample.jetpack.compose.di.activity

import dagger.Module
import dagger.Provides

import sample.jetpack.compose.mvi.action.MainAction

import sample.jetpack.compose.mvi.middleware.LoggingMiddleWare

import sample.jetpack.compose.mvi.state.MainState

@Module
class MainModule {

	@Provides
	fun providesLoggingMiddleWare(): LoggingMiddleWare<MainState, MainAction> =
		LoggingMiddleWare()

}
