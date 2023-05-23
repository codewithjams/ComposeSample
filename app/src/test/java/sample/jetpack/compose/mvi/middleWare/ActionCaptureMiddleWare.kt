package sample.jetpack.compose.mvi.middleWare

import com.google.common.truth.Truth

import sample.jetpack.compose.mvi.action.Action

import sample.jetpack.compose.mvi.middleware.MiddleWare

import sample.jetpack.compose.mvi.state.State

import sample.jetpack.compose.mvi.store.Store

class ActionCaptureMiddleWare<S : State, A : Action> : MiddleWare<S, A> {

	private val capturedActions : MutableList<A> = mutableListOf()

	override suspend fun process(action : A, currentState : S, store : Store<S, A>) {
		capturedActions.add(action)
	}

	fun assertContains(action : A) {
		Truth.assertThat(capturedActions).contains(action)
	}

	fun assertNotContains(action : A) {
		Truth.assertThat(capturedActions).doesNotContain(action)
	}

}
