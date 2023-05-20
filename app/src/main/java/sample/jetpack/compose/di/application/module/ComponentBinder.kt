@file:Suppress("unused")

package sample.jetpack.compose.di.application.module

import androidx.lifecycle.ViewModel

import dagger.Binds
import dagger.Module

import dagger.android.ContributesAndroidInjector

import dagger.multibindings.IntoMap

import sample.jetpack.compose.di.activity.LoginModule
import sample.jetpack.compose.di.activity.MainModule

import sample.jetpack.compose.di.application.mapKey.ViewModelKey

import sample.jetpack.compose.mvi.viewModel.LoginViewModel
import sample.jetpack.compose.mvi.viewModel.MainViewModel

import sample.jetpack.compose.ui.activity.LoginActivity
import sample.jetpack.compose.ui.activity.MainActivity

@Module
abstract class ComponentBinder {

	@ContributesAndroidInjector(modules = [MainModule::class])
	abstract fun contributesMainActivity() : MainActivity

	@Binds
	@IntoMap
	@ViewModelKey(MainViewModel::class)
	abstract fun providesMainViewModel(viewModel : MainViewModel) : ViewModel

	@ContributesAndroidInjector(modules = [LoginModule::class])
	abstract fun contributesLoginActivity() : LoginActivity

	@Binds
	@IntoMap
	@ViewModelKey(LoginViewModel::class)
	abstract fun providesLoginViewModel(viewModel : LoginViewModel) : ViewModel

}
