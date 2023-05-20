package sample.jetpack.compose.mvi.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import java.lang.Exception
import java.lang.RuntimeException

import javax.inject.Inject
import javax.inject.Provider

class VMFactory @Inject constructor(
	private val creators : Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

	override fun <T : ViewModel> create(modelClass : Class<T>) : T {

		val provider = creators[modelClass] ?: creators.entries.firstOrNull() { entry ->
			modelClass.isAssignableFrom(entry.key)
		}?.value ?: throw IllegalArgumentException("Unknown Model Class : $modelClass")

		return try {
			@Suppress("UNCHECKED_CAST")
			provider.get() as T
		} catch (e : Exception) {
			throw RuntimeException(e)
		}

	}

}
