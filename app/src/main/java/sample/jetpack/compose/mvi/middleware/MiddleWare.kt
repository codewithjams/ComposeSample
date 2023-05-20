package sample.jetpack.compose.mvi.middleware

import sample.jetpack.compose.mvi.action.Action

import sample.jetpack.compose.mvi.state.State

import sample.jetpack.compose.mvi.store.Store

interface MiddleWare<S : State, A : Action> {

	suspend fun process(action : A, currentState : S, store : Store<S, A>)

	suspend fun propagateAction(store : Store<S, A>, action : A) {
		store.dispatch(action)
	}

}
