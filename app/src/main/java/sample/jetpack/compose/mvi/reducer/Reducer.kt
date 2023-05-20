package sample.jetpack.compose.mvi.reducer

import sample.jetpack.compose.mvi.action.Action

import sample.jetpack.compose.mvi.state.State

interface Reducer<S : State, A : Action> {

	fun reduce(currentState : S, action : A) : S

}
