package sample.jetpack.compose.mvi.model

import sample.jetpack.compose.utility.constants.MenuOptionsValidator

data class MenuOption(
	@MenuOptionsValidator val id: Int,
	val name: String
)
