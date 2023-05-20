package sample.jetpack.compose.mvi.reducer

import sample.jetpack.compose.mvi.action.MainAction

import sample.jetpack.compose.mvi.model.MenuOption

import sample.jetpack.compose.mvi.state.MainState

class MainReducer : Reducer<MainState, MainAction> {

	override fun reduce(currentState : MainState, action : MainAction) : MainState =
		when (action) {

			is MainAction.MenuOptionClicked -> onMenuOptionClicked(currentState, action.option)

		}

	private fun onMenuOptionClicked(currentState : MainState, option : MenuOption) =
		currentState.copy(
			navigationCode = option.id,
			navigate = true,
			navigated = false
		)

}
