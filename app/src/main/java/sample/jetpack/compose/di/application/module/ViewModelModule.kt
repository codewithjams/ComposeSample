package sample.jetpack.compose.di.application.module

import androidx.lifecycle.ViewModel

import dagger.Module
import dagger.Provides

import sample.jetpack.compose.mvi.factory.VMFactory

import javax.inject.Provider

@Module
class ViewModelModule {

	@Provides
	fun providesVMFactory(
		creators : Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
	) : VMFactory = VMFactory(creators)

}
