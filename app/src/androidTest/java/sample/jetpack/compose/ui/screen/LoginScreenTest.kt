@file:Suppress("SpellCheckingInspection")

package sample.jetpack.compose.ui.screen

import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.stringResource

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.isNotEnabled
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput

import org.junit.Rule
import org.junit.Test

import sample.jetpack.compose.R

import sample.jetpack.compose.mvi.middleware.LoggingMiddleWare
import sample.jetpack.compose.mvi.middleware.LoginMiddleWare

import sample.jetpack.compose.mvi.viewModel.LoginViewModel

import sample.jetpack.compose.repository.impl.SampleRepositoryImpl

import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_LOGIN
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_PASSWORD_VISIBILITY_TOGGLE
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_PROGRESS_INDICATOR
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_PASSWORD
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_USER_NAME
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_PASSWORD_VALIDATION_ERROR
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_USER_NAME_VALIDATION_ERROR

import sample.jetpack.compose.utility.helper.ContextResourceUtils

class LoginScreenTest {

	@get: Rule
	val rule : ComposeContentTestRule = createComposeRule()

	/**
	 * Test Case 1
	 *
	 * **Scenario**:
	 * - Screen has started.
	 *
	 * **Expectation**:
	 * - No text on User Name and Password.
	 * - No validation text for User Name and Password field.
	 * - Login button is disabled.
	 * - Progress indicator is not shown.
	 */
	@Test
	fun testCase00() {

		rule.apply {

			setContent {
				TestScreen {}
			}

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Make sure the User Name and Password field is empty.
			textInputUserName().assert(hasText(""))
			textInputPassword().assert(hasText(""))

			// Make sure the validation text for User Name and Password field is not shown.
			textUserNameValidationError().assertDoesNotExist()
			textPasswordValidationError().assertDoesNotExist()

			// Make sure that the Login button is disabled.
			buttonLogin().assert(isNotEnabled())

			// Make sure that Progress indicator is not being shown.
			progressIndicator().assertDoesNotExist()

		}

	}

	/**
	 * Test Case 2.
	 *
	 * **Scenario**:
	 * - User has already entered some user name and password. User's password is shown as ***.
	 * - Now, user clicks on password visibility toggle.
	 *
	 * **Expectation**:
	 * - User's password must be revealed.
	 */
	@Test
	fun testCase01() {

		rule.apply {

			setContent {
				TestScreen {}
			}

			// Type out the user name and password.
			textInputUserName().performTextInput("some_user_name")
			textInputPassword().performTextInput("some_password")

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since there's nothing wrong with User Name and Password,
			// any validation error for them must not be shown.
			textUserNameValidationError().assertDoesNotExist()
			textPasswordValidationError().assertDoesNotExist()

			// By default, the password is hidden, so the password field must show •••.
			textInputPassword().assert(hasText("•••••••••••••"))

			// Since the username and password is proper, Login button should be enabled.
			buttonLogin().assert(isEnabled())

			// Now, perform click on Password Visibility toggle button
			// to trigger revealing the password.
			buttonPasswordVisibilityToggle().performClick()

			// At this point, since the state of Password Visibility is now show,
			// the actual password must be shown.

			// Check if the password shown shows actual password.
			textInputPassword().assert(hasText("some_password"))

		}

	}

	/**
	 * Test Case 3
	 *
	 * **Scenario**:
	 * - User enters both user name and password.
	 * - Now, user clears the User Name.
	 *
	 * **Expectation**:
	 * - User sees the validation text for User Name field.
	 */
	@Test
	fun testCase02() {

		rule.apply {

			lateinit var userNameValidation : String

			setContent {
				TestScreen {
					userNameValidation = stringResource(
						id = R.string.login_screen_validation_empty_user_name
					)
				}
			}

			// Now Enter Some User Name and Password
			textInputUserName().performTextInput("some_user_name")
			textInputPassword().performTextInput("12345")

			// Printing the Node tree for debugging purposes.
			// onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since both User Name and Password is valid, Login button should be enabled.
			buttonLogin().assert(isEnabled())

			// Now, clear the whole text from User Name field.
			textInputUserName().performTextClearance()

			// Printing the Node tree for debugging purposes.
			// onRoot().printToLog(LoginScreenTest::class.java.name)

			// User Validation denoting empty User Name should be shown.
			textUserNameValidationError().apply {
				assertExists()
				assert(hasText(userNameValidation))
			}

			// At this point, since User Name is no longer valid, Login button should be disabled.
			buttonLogin().assert(isNotEnabled())

		}

	}

	/**
	 * Test Case 4
	 *
	 * **Scenario**:
	 * - User enters both user name and password.
	 * - Now, user clears the password.
	 *
	 * **Expectation**:
	 * - User sees the validation text for Password field.
	 */
	@Test
	fun testCase03() {

		rule.apply {

			lateinit var passwordValidation : String

			setContent {
				TestScreen {
					passwordValidation = stringResource(
						id = R.string.login_screen_validation_empty_password
					)
				}
			}

			// Now Enter Some User Name and Password
			textInputUserName().performTextInput("some_user_name")
			textInputPassword().performTextInput("12345")

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since both User Name and Password is valid, Login button should be enabled.
			buttonLogin().assert(isEnabled())

			// Now, clear the whole text from Password field.
			textInputPassword().performTextClearance()

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// User Validation denoting empty User Name should be shown.
			textPasswordValidationError().apply {
				assertExists()
				assert(hasText(passwordValidation))
			}

			// At this point, since User Name is no longer valid, Login button should be disabled.
			buttonLogin().assert(isNotEnabled())

		}

	}

	/**
	 * Test Case 5
	 *
	 * **Scenario**:
	 * - User has entered both User Name and Password.
	 * - User Name reaches the character limit.
	 *
	 * **Expectation**:
	 * - Show the Character Limit reached validation.
	 * - Login button is enabled.
	 */
	@Test
	fun testCase04() {

		rule.apply {

			lateinit var userNameValidation : String

			setContent {
				TestScreen {
					userNameValidation = stringResource(
						id = R.string.login_screen_validation_user_name_length_limit_reached
					)
				}
			}

			// Input both User Name and Password with User Name reaching the character limit.
			textInputUserName().performTextInput("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvb")
			textInputPassword().performTextInput("123")

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since the user name has it's character limit exeeded,
			// the validation around reaching the limit should be shown.
			textUserNameValidationError().apply {
				assertExists()
				assert(hasText(userNameValidation))
			}

			// Even though user name has reached the character limit,
			// it is still allowed to perform Login
			buttonLogin().assert(isEnabled())

		}

	}

	/**
	 * Test Case 6
	 *
	 * **Scenario**:
	 * - User has entered both User Name and Password.
	 * - User Name exceeds the character limit.
	 *
	 * **Expectation**:
	 * - Show the Character Limit exceeded validation.
	 * - Login button is disabled.
	 */
	@Test
	fun testCase05() {

		rule.apply {

			lateinit var userNameValidation : String

			setContent {
				TestScreen {
					userNameValidation = stringResource(
						R.string.login_screen_validation_user_name_length_exceeded,
						2
					)
				}
			}

			// Input both User Name and Password with User Name reaching the character limit.
			textInputUserName().performTextInput("qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm")
			textInputPassword().performTextInput("123")

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since the user name has it's character limit exeeded,
			// the validation around exceeding the limit should be shown.
			textUserNameValidationError().apply {
				assertExists()
				assert(hasText(userNameValidation))
			}

			// Since, the user name is exceeded, Login should be disabled.
			buttonLogin().assert(isNotEnabled())

		}

	}

	/**
	 * Test Case 7
	 *
	 * **Scenario**:
	 * - User has entered both User Name and Password.
	 * - Password contains illegal character.
	 *
	 * **Expectation**:
	 * - Show the Illegal Character validation on Password field.
	 * - Login button is disabled.
	 */
	@Test
	fun testCase06() {

		rule.apply {

			lateinit var passwordValidation : String

			setContent {
				TestScreen {
					passwordValidation = stringResource(
						id = R.string.login_screen_validation_password_containing_illegal_characters
					)
				}
			}

			// Input both User Name and Password with User Name reaching the character limit.
			textInputUserName().performTextInput("abcd")
			textInputPassword().performTextInput("123?")

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since the user name has it's character limit exeeded,
			// the validation around exceeding the limit should be shown.
			textPasswordValidationError().apply {
				assertExists()
				assert(hasText(passwordValidation))
			}

			// Since, the user name is exceeded, Login should be disabled.
			buttonLogin().assert(isNotEnabled())

		}

	}

	/**
	 * Test Case 8
	 *
	 * **Scenario**:
	 * - User has entered both User Name and Password.
	 * - User clears both User Name and Password.
	 *
	 * **Expectation**:
	 * - Show empty validation on both User Name and Password field.
	 * - Login button is disabled.
	 */
	@Test
	fun testCase07() {

		rule.apply {

			lateinit var userNameValidation: String
			lateinit var passwordValidation : String

			setContent {
				TestScreen {
					userNameValidation = stringResource(
						id = R.string.login_screen_validation_empty_user_name
					)
					passwordValidation = stringResource(
						id = R.string.login_screen_validation_empty_password
					)
				}
			}

			// Input both User Name and Password with User Name reaching the character limit.
			textInputUserName().performTextInput("abcd")
			textInputPassword().performTextInput("123")

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since, the both user name and password is valid, Login button should be enabled.
			buttonLogin().assert(isEnabled())

			// Clear both User Name and Password field.
			textInputUserName().performTextClearance()
			textInputPassword().performTextClearance()

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since, both User Name and Password is empty,
			// we should see the validation text for these fields.

			textUserNameValidationError().apply {
				assertExists()
				assert(hasText(userNameValidation))
			}

			textPasswordValidationError().apply {
				assertExists()
				assert(hasText(passwordValidation))
			}

			// Since, both User Name and Password is empty, Login button should be disabled.
			buttonLogin().assert(isNotEnabled())

		}

	}

	/**
	 * Test Case 9
	 *
	 *
	 * **Scenario**:
	 * - User has entered both valid User Name and Password.
	 * - User clicks on Login.
	 *
	 * **Expectation**:.
	 * - Login button is enabled.
	 * - Progress is shown when Login button is clicked.
	 */
	@Test
	fun testCase08() {

		rule.apply {

			setContent {
				TestScreen {}
			}

			// Enter the User Name and Password.
			textInputUserName().performTextInput("abcd")
			textInputPassword().performTextInput("123")

			// Printing the Node tree for debugging purposes.
			//onRoot().printToLog(LoginScreenTest::class.java.name)

			// Since the user name and password are both valid,
			// hence Login button should be enabled.
			// And since the button is enabled, we click the Login button.
			buttonLogin().apply {
				assert(isEnabled())
				performClick()
			}

			// At this point, REST API Call is in progress, as such,
			// the Progress denoting the same should be shown.
			progressIndicator().assertExists()

		}

	}

	@Suppress("TestFunctionName")
	@Composable
	private fun TestScreen(onScreen : @Composable () -> Unit) {

		// Perform anything outside this Composable so that the execution from this Lambda
		// still happens in a Composable scope. This is useful when accessing any specific
		// composable features, like accessing stringResource.
		onScreen.invoke()

		// Creating the ViewModel here because one of the dependency needed Context.
		val viewModel = LoginViewModel(
			middleWare = LoginMiddleWare(
				repository = SampleRepositoryImpl(
					resourceUtils = ContextResourceUtils(
						context = LocalContext.current
					)
				)
			),
			loggingMiddleWare = LoggingMiddleWare()
		)

		// Wrapping the screen under test in a Surface.
		// This is because when the state changes from ViewModel, whole Surface will recompose,
		// while retaining the same instance of viewModel.
		Surface {

			val state = viewModel.viewState.collectAsState()

			LoginScreen(
				state = state.value,
				onUserNameChanged = viewModel::onUserNameChanged,
				onPasswordChanged = viewModel::onPasswordChanged,
				onPasswordVisibilityToggle = viewModel::onPasswordVisibilityToggleClicked,
				onLoginClick = viewModel::onLoginClicked
			)

		}

	}

	private fun ComposeContentTestRule.textInputUserName() : SemanticsNodeInteraction =
		onNodeWithTag(testTag = TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_USER_NAME)

	private fun ComposeContentTestRule.textUserNameValidationError() : SemanticsNodeInteraction =
		onNodeWithTag(testTag = TEST_TAG_LOGIN_INPUT_FIELD_TEXT_USER_NAME_VALIDATION_ERROR)

	private fun ComposeContentTestRule.textInputPassword() : SemanticsNodeInteraction =
		onNodeWithTag(testTag = TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_PASSWORD)

	private fun ComposeContentTestRule.textPasswordValidationError() : SemanticsNodeInteraction =
		onNodeWithTag(testTag = TEST_TAG_LOGIN_INPUT_FIELD_TEXT_PASSWORD_VALIDATION_ERROR)

	private fun ComposeContentTestRule.buttonPasswordVisibilityToggle() : SemanticsNodeInteraction =
		onNodeWithTag(testTag = TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_PASSWORD_VISIBILITY_TOGGLE)

	private fun ComposeContentTestRule.progressIndicator() : SemanticsNodeInteraction =
		onNodeWithTag(testTag = TEST_TAG_LOGIN_INPUT_FIELD_PROGRESS_INDICATOR)

	private fun ComposeContentTestRule.buttonLogin() : SemanticsNodeInteraction =
		onNodeWithTag(testTag = TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_LOGIN)

}
