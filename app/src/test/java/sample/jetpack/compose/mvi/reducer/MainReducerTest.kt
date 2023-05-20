package sample.jetpack.compose.mvi.reducer

import com.google.common.truth.Truth

import org.junit.Test

import sample.jetpack.compose.mvi.action.MainAction

import sample.jetpack.compose.mvi.model.MenuOption

import sample.jetpack.compose.mvi.state.MainState

import sample.jetpack.compose.utility.constants.MENU_OPTION_LOGIN

class MainReducerTest {

	private val reducer: MainReducer by lazy {
		MainReducer()
	}

	@Test
	fun `test case 00`() {
		val option = MenuOption(MENU_OPTION_LOGIN, "Login Screen")
		assert(
			action = MainAction.MenuOptionClicked(option = option),
			initialState = MainState(
				menuOptions = listOf(
					option
				)
			),
			expectedState = MainState(
				menuOptions = listOf(
					option
				),
				navigate = true,
				navigated = false,
				navigationCode = MENU_OPTION_LOGIN
			)
		)
	}

	private fun assert(
		action: MainAction,
		initialState: MainState,
		expectedState: MainState
	) {
		reducer.reduce(currentState = initialState, action = action).apply {
			assert(expectedState)
		}
	}

	private fun MainState.assert(expect: MainState) {
		Truth.assertThat(this).isEqualTo(expect)
	}

}
