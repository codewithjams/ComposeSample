package sample.jetpack.compose.application

import android.app.Application

import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector

import sample.jetpack.compose.di.application.component.DaggerSampleApplicationComponent

import javax.inject.Inject

class SampleApplication : Application(), HasAndroidInjector {

	@Inject
	lateinit var androidInjector : DispatchingAndroidInjector<Any>

	override fun onCreate() {
		super.onCreate()
		inject()
	}

	override fun androidInjector() : AndroidInjector<Any> =
		androidInjector

	private fun inject() {
		DaggerSampleApplicationComponent.builder()
			.bindApplication(this)
			.build()
			.inject(this)
	}

}
