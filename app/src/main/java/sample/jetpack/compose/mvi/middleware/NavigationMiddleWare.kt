package sample.jetpack.compose.mvi.middleware

import sample.jetpack.compose.mvi.action.Action

import sample.jetpack.compose.mvi.state.State
import sample.jetpack.compose.mvi.store.Store

class NavigationMiddleWare<S: State, A: Action>(
	private val onNavigate: (S, A) -> Unit
) : MiddleWare<S, A> {

	override suspend fun process(action : A, currentState : S, store : Store<S, A>) {
		onNavigate.invoke(currentState, action)
	}

}
