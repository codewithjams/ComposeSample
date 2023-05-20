package sample.jetpack.compose.mvi.action

import sample.jetpack.compose.mvi.model.MenuOption

sealed class MainAction : Action {

	data class MenuOptionClicked(val option: MenuOption) : MainAction()

}
