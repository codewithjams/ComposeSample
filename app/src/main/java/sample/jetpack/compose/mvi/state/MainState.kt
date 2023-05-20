package sample.jetpack.compose.mvi.state

import sample.jetpack.compose.mvi.model.MenuOption

import sample.jetpack.compose.utility.constants.MENU_OPTION_NONE
import sample.jetpack.compose.utility.constants.MenuOptionsValidator

data class MainState(
	val menuOptions: List<MenuOption>,
	@MenuOptionsValidator val navigationCode: Int = MENU_OPTION_NONE,
	val navigate: Boolean = false,
	var navigated: Boolean = false
) : State
