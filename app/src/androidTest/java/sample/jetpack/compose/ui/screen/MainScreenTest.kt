package sample.jetpack.compose.ui.screen

import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick

import org.junit.Rule
import org.junit.Test

import sample.jetpack.compose.R

import sample.jetpack.compose.mvi.middleware.LoggingMiddleWare

import sample.jetpack.compose.mvi.viewModel.MainViewModel

import sample.jetpack.compose.utility.constants.MENU_OPTION_LOGIN
import sample.jetpack.compose.utility.constants.MENU_OPTION_NONE
import sample.jetpack.compose.utility.constants.TEST_TAG_MAIN_MENU_OPTION_BUTTON
import sample.jetpack.compose.utility.constants.TEST_TAG_MAIN_TEXT_GREETINGS

import sample.jetpack.compose.utility.helper.ContextResourceUtils

private typealias NavigationListener = (Int) -> Unit

class MainScreenTest {

	@get: Rule
	val rule : ComposeContentTestRule = createComposeRule()

	/**
	 * Test Case 1
	 *
	 * **Scenario**:
	 * - Screen has started.
	 *
	 * **Expectation**:
	 * - Greetings Text is populated with configured User Name in strings.xml.
	 * - All Menu Options defined under strings.xml are being displayed.
	 */
	@Test
	fun testCase00() {

		rule.apply {

			lateinit var greetingsText : String
			lateinit var menuOptions: Array<String>

			setContent {
				TestScreen(
					onScreen = {
						greetingsText = stringResource(
							R.string.main_screen_greetings_name,
							stringResource(id = R.string.main_screen_user_name)
						)
						menuOptions = stringArrayResource(id = R.array.main_screen_menu_options)
					},
					onNavigateListener = {}
				)
			}

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Greetings text show the name of user as expected.
			textGreetings().assert(hasText(greetingsText))

			// All Menu Options defined under strings.xml is being displayed.
			menuOptions.forEachIndexed { index, option ->
				menuOptionButton(index).apply {
					assertExists()
					hasText(option)
				}
			}

		}

	}

	/**
	 * Test Case 2
	 *
	 * **Scenario**:
	 * - Screen has started.
	 * - User clicks on first option.
	 *
	 * **Expectation**:
	 * - User should receive an event that is responsible for navigating to first option.
	 */
	@Test
	fun testCase01() {

		rule.apply {

			var navigationCode: Int = MENU_OPTION_NONE

			setContent {
				TestScreen(
					onScreen = {},
					onNavigateListener = { option ->
						navigationCode = option
					}
				)
			}

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Perform click on one option.
			menuOptionButton(0).apply {
				assertExists()
				performClick()
			}

			// Wait for a second in order to receive the callback.
			waitUntil(
				timeoutMillis = 1000L,
				condition = {
					return@waitUntil navigationCode != MENU_OPTION_NONE
				}
			)

			// We should have received the navigation code for first option.
			assert(navigationCode == MENU_OPTION_LOGIN)

		}

	}

	@Suppress("TestFunctionName")
	@Composable
	private fun TestScreen(
		onScreen : @Composable () -> Unit,
		onNavigateListener : NavigationListener
	) {

		// Perform anything outside this Composable so that the execution from this Lambda
		// still happens in a Composable scope. This is useful when accessing any specific
		// composable features, like accessing stringResource.
		onScreen.invoke()

		// Creating the ViewModel here because one of the dependency needed Context.
		val viewModel = MainViewModel(
			resourceUtils = ContextResourceUtils(
				context = LocalContext.current
			),
			loggingMiddleWare = LoggingMiddleWare()
		)

		// Wrapping the screen under test in a Surface.
		// This is because when the state changes from ViewModel, whole Surface will recompose,
		// while retaining the same instance of viewModel.
		Surface {

			val state = viewModel.viewState.collectAsState()

			MainScreen(
				state = state.value,
				onMenuOptionClicked = viewModel::onMenuOptionClicked,
				onNavigateScreen = onNavigateListener
			)

		}

	}

	private fun ComposeContentTestRule.textGreetings() =
		onNodeWithTag(testTag = TEST_TAG_MAIN_TEXT_GREETINGS)

	private fun ComposeContentTestRule.menuOptionButton(index : Int) =
		onNodeWithTag(testTag = TEST_TAG_MAIN_MENU_OPTION_BUTTON + index)

}
