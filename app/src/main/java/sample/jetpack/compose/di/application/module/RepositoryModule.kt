package sample.jetpack.compose.di.application.module

import dagger.Module
import dagger.Provides

import sample.jetpack.compose.repository.SampleRepository

import sample.jetpack.compose.repository.impl.SampleRepositoryImpl

import sample.jetpack.compose.utility.helper.ResourceUtils

@Module
class RepositoryModule {

	@Provides
	fun providesSampleRepository(
		resourceUtils : ResourceUtils
	) : SampleRepository =
		SampleRepositoryImpl(resourceUtils)

}
