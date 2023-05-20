package sample.jetpack.compose.mvi.middleware

import sample.jetpack.compose.mvi.action.Action

import sample.jetpack.compose.mvi.state.State

import sample.jetpack.compose.mvi.store.Store

import javax.inject.Inject

private const val LOGGING_MIDDLE_WARE_CLASS_TAG = "LoggingMiddleWare"

class LoggingMiddleWare<S : State, A : Action> @Inject constructor() : MiddleWare<S, A> {

	override suspend fun process(action : A, currentState : S, store : Store<S, A>) {
		android.util.Log.v(
			LOGGING_MIDDLE_WARE_CLASS_TAG,
			"Processing Action : $action, Current State : $currentState"
		)
	}

}
