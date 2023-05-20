package sample.jetpack.compose.di.application.module

import android.content.Context

import dagger.Module
import dagger.Provides

import sample.jetpack.compose.di.application.qualifier.ApplicationContext

import sample.jetpack.compose.di.application.scope.SampleApplicationScope

import sample.jetpack.compose.utility.helper.ContextResourceUtils
import sample.jetpack.compose.utility.helper.ResourceUtils

@Module
class UtilityModule {

	@Provides
	@SampleApplicationScope
	fun providesResourceUtils(@ApplicationContext context : Context) : ResourceUtils =
		ContextResourceUtils(context)

}
