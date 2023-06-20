package sample.jetpack.compose.mvi.viewModel

import androidx.lifecycle.viewModelScope

import sample.jetpack.compose.R

import sample.jetpack.compose.exception.MenuOptionNotConfiguredException
import sample.jetpack.compose.exception.NoMenuOptionsException

import sample.jetpack.compose.mvi.action.MainAction

import sample.jetpack.compose.mvi.middleware.LoggingMiddleWare
import sample.jetpack.compose.mvi.middleware.NavigationMiddleWare

import sample.jetpack.compose.mvi.model.MenuOption

import sample.jetpack.compose.mvi.reducer.MainReducer

import sample.jetpack.compose.mvi.state.MainState

import sample.jetpack.compose.mvi.store.Store
import sample.jetpack.compose.utility.constants.MENU_OPTION_FILE_TRANSFER

import sample.jetpack.compose.utility.constants.MENU_OPTION_LOGIN

import sample.jetpack.compose.utility.helper.ResourceUtils

import javax.inject.Inject

class MainViewModel @Inject constructor(
	private val resourceUtils : ResourceUtils,
	loggingMiddleWare: LoggingMiddleWare<MainState, MainAction>
) : MVIViewModel<MainState, MainAction>() {

	var navigationLambda: ((MainState, MainAction) -> Unit)? = null

	override val store : Store<MainState, MainAction> =
		Store(
			initialState = prepareInitialState(),
			reducer = MainReducer(),
			middleWares = listOf(
				loggingMiddleWare,
				NavigationMiddleWare { state, action ->
					navigationLambda?.invoke(state, action)
				}
			),
			coroutineScope = viewModelScope
		)

	fun onMenuOptionClicked(option: MenuOption) {
		dispatchActionToStore(MainAction.MenuOptionClicked(option = option))
	}

	private fun prepareInitialState(): MainState {

		val options: Array<String> = resourceUtils.getStringArray(R.array.main_screen_menu_options)

		if (options.isEmpty())
			throw NoMenuOptionsException()

		val menuOptions: List<MenuOption> = options.mapIndexed { index, option ->
			MenuOption(
				id = when(index) {
					0 -> MENU_OPTION_LOGIN
					1 -> MENU_OPTION_FILE_TRANSFER
					else -> throw MenuOptionNotConfiguredException(option)
				},
				name = option
			)
		}.toList()

		return MainState(
			menuOptions = menuOptions
		)

	}

}
