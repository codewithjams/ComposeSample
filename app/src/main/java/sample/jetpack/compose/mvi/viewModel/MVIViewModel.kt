package sample.jetpack.compose.mvi.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.launch

import sample.jetpack.compose.mvi.action.Action

import sample.jetpack.compose.mvi.state.State

import sample.jetpack.compose.mvi.store.Store

abstract class MVIViewModel<S : State, A : Action> : ViewModel() {

	protected abstract val store : Store<S, A>

	val viewState : StateFlow<S>
		get() = store.state

	protected open fun dispatchActionToStore(
		action : A,
		dispatcher : CoroutineDispatcher = Dispatchers.Main
	) {
		viewModelScope.launch(dispatcher) {
			store.dispatch(action)
		}
	}

}
