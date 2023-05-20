package sample.jetpack.compose.di.application.module

import android.app.Application

import android.content.Context

import dagger.Module
import dagger.Provides

import sample.jetpack.compose.application.SampleApplication

import sample.jetpack.compose.di.application.qualifier.ApplicationContext

import sample.jetpack.compose.di.application.scope.SampleApplicationScope

@Module(
	includes = [
		RepositoryModule::class,
		UtilityModule::class,
		ViewModelModule::class
	]
)
class SampleApplicationModule {

	@Provides
	fun providesApplication(application : SampleApplication) : Application =
		application

	@Provides
	@SampleApplicationScope
	@ApplicationContext
	fun providesApplicationContext(application : SampleApplication) : Context =
		application.applicationContext

}
