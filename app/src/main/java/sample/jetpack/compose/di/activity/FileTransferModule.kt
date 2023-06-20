package sample.jetpack.compose.di.activity

import dagger.Module
import dagger.Provides
import sample.jetpack.compose.mvi.middleware.FileTransferMiddleWare
import sample.jetpack.compose.repository.SampleRepository

@Module
class FileTransferModule {

	@Provides
	fun providesMiddleWare(repository : SampleRepository): FileTransferMiddleWare =
		FileTransferMiddleWare(repository = repository)

}
