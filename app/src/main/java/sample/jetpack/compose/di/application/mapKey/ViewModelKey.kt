package sample.jetpack.compose.di.application.mapKey

import androidx.lifecycle.ViewModel

import dagger.MapKey

import kotlin.reflect.KClass

@MapKey
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class ViewModelKey(val value : KClass<out ViewModel>)
