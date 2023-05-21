package sample.jetpack.compose.mvi.reducer

import sample.jetpack.compose.mvi.action.MainAction

import sample.jetpack.compose.mvi.state.MainState

class MainReducer : Reducer<MainState, MainAction> {

	override fun reduce(currentState : MainState, action : MainAction) : MainState =
		currentState

}
