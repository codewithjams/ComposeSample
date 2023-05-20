package sample.jetpack.compose.di.application.component

import dagger.BindsInstance
import dagger.Component

import dagger.android.AndroidInjectionModule

import sample.jetpack.compose.application.SampleApplication

import sample.jetpack.compose.di.application.module.ComponentBinder
import sample.jetpack.compose.di.application.module.SampleApplicationModule

import sample.jetpack.compose.di.application.scope.SampleApplicationScope

@SampleApplicationScope
@Component(
	modules = [
		SampleApplicationModule::class,
		ComponentBinder::class,
		AndroidInjectionModule::class
	]
)
interface SampleApplicationComponent {

	fun inject(application : SampleApplication)

	@Component.Builder
	interface Builder {

		fun build() : SampleApplicationComponent

		@BindsInstance
		fun bindApplication(application : SampleApplication) : Builder

	}

}
