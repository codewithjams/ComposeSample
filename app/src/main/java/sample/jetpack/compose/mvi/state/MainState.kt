package sample.jetpack.compose.mvi.state

import sample.jetpack.compose.mvi.model.MenuOption

data class MainState(
	val menuOptions: List<MenuOption>
) : State
