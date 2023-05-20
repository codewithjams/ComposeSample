@file:Suppress("SpellCheckingInspection")

package sample.jetpack.compose.mvi.reducer

import com.google.common.truth.Truth

import org.junit.Test

import sample.jetpack.compose.mvi.action.LoginAction

import sample.jetpack.compose.mvi.model.LoginValidation

import sample.jetpack.compose.mvi.state.LoginState
import sample.jetpack.compose.mvi.utility.assert

class LoginReducerTest {

	private val reducer : LoginReducer by lazy {
		LoginReducer()
	}

	@Test
	fun `test case 00`() {
		assert(
			action = LoginAction.UserNameChanged(
				newUserName = "a"
			),
			initialState = LoginState(),
			expectedState = LoginState(
				userName = "a",
				userNameEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.NONE,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 01`() {
		assert(
			action = LoginAction.UserNameChanged(
				newUserName = ""
			),
			initialState = LoginState(
				userName = "a",
				userNameEnteredFirstTime = true
			),
			expectedState = LoginState(userNameEnteredFirstTime = true),
			userNameValidation = LoginValidation.UserNameValidation.EMPTY_USER_NAME,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 02`() {
		assert(
			action = LoginAction.UserNameChanged(
				newUserName = "abcdef"
			),
			initialState = LoginState(
				userName = "abc",
				userNameEnteredFirstTime = false
			),
			expectedState = LoginState(
				userName = "abcdef",
				userNameEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.NONE,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 03`() {
		assert(
			action = LoginAction.UserNameChanged(
				newUserName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvb"
			),
			initialState = LoginState(
				userName = "q",
				userNameEnteredFirstTime = true
			),
			expectedState = LoginState(
				userName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvb",
				userNameEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.LENGTH_LIMIT_REACHED,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 04`() {
		assert(
			action = LoginAction.UserNameChanged(
				newUserName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm"
			),
			initialState = LoginState(
				userName = "q",
				userNameEnteredFirstTime = true
			),
			expectedState = LoginState(
				userName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm",
				userNameEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.LENGTH_LIMIT_EXCEEDED,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 05`() {
		assert(
			action = LoginAction.PasswordChanged(
				newPassword = "1"
			),
			initialState = LoginState(),
			expectedState = LoginState(
				password = "1",
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.NONE,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 06`() {
		assert(
			action = LoginAction.PasswordChanged(
				newPassword = ""
			),
			initialState = LoginState(
				password = "1",
				passwordEnteredFirstTime = true
			),
			expectedState = LoginState(
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.NONE,
			passwordValidation = LoginValidation.PasswordValidation.EMPTY_PASSWORD,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 07`() {
		assert(
			action = LoginAction.PasswordChanged(
				newPassword = "1%3"
			),
			initialState = LoginState(
				password = "1",
				passwordEnteredFirstTime = false
			),
			expectedState = LoginState(
				password = "1%3",
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.NONE,
			passwordValidation = LoginValidation.PasswordValidation.ILLEGAL_CHARACTERS,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 08`() {
		assert(
			action = LoginAction.UserNameChanged(
				newUserName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvb"
			),
			initialState = LoginState(
				userName = "q",
				userNameEnteredFirstTime = true,
				password = "1",
				passwordEnteredFirstTime = true
			),
			expectedState = LoginState(
				userName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvb",
				userNameEnteredFirstTime = true,
				password = "1",
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.LENGTH_LIMIT_REACHED,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = true
		)
	}

	@Test
	fun `test case 09`() {
		assert(
			action = LoginAction.UserNameChanged(
				newUserName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm"
			),
			initialState = LoginState(
				userName = "q",
				userNameEnteredFirstTime = true,
				password = "1",
				passwordEnteredFirstTime = true
			),
			expectedState = LoginState(
				userName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm",
				userNameEnteredFirstTime = true,
				password = "1",
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.LENGTH_LIMIT_EXCEEDED,
			passwordValidation = LoginValidation.PasswordValidation.NONE,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 10`() {
		assert(
			action = LoginAction.PasswordChanged(
				newPassword = "1x?"
			),
			initialState = LoginState(
				userName = "abc",
				userNameEnteredFirstTime = true,
				password = "1x",
				passwordEnteredFirstTime = true
			),
			expectedState = LoginState(
				userName = "abc",
				userNameEnteredFirstTime = true,
				password = "1x?",
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.NONE,
			passwordValidation = LoginValidation.PasswordValidation.ILLEGAL_CHARACTERS,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 11`() {
		assert(
			action = LoginAction.PasswordChanged(
				newPassword = "1x?"
			),
			initialState = LoginState(
				userName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm",
				userNameEnteredFirstTime = true,
				password = "1x",
				passwordEnteredFirstTime = true
			),
			expectedState = LoginState(
				userName = "qwertyuiopasdfghjklzxcvbnmqwertyuiopasdfghjklzxcvbnm",
				userNameEnteredFirstTime = true,
				password = "1x?",
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.LENGTH_LIMIT_EXCEEDED,
			passwordValidation = LoginValidation.PasswordValidation.ILLEGAL_CHARACTERS,
			loginEnabled = false
		)
	}

	@Test
	fun `test case 12`() {
		assert(
			action = LoginAction.PasswordChanged(
				newPassword = "1x?"
			),
			initialState = LoginState(
				userName = "",
				userNameEnteredFirstTime = true,
				password = "1x",
				passwordEnteredFirstTime = true
			),
			expectedState = LoginState(
				userName = "",
				userNameEnteredFirstTime = true,
				password = "1x?",
				passwordEnteredFirstTime = true
			),
			userNameValidation = LoginValidation.UserNameValidation.EMPTY_USER_NAME,
			passwordValidation = LoginValidation.PasswordValidation.ILLEGAL_CHARACTERS,
			loginEnabled = false
		)
	}

	private fun assert(
		action : LoginAction,
		initialState : LoginState,
		expectedState : LoginState,
		userNameValidation : LoginValidation.UserNameValidation,
		passwordValidation : LoginValidation.PasswordValidation,
		loginEnabled : Boolean
	) {
		reducer.reduce(currentState = initialState, action = action).apply {
			assert(expected = expectedState)
			this.userNameValidation.assert(userNameValidation)
			this.passwordValidation.assert(passwordValidation)
			this.loginEnabled.assert(loginEnabled)
		}
	}

	private fun LoginState.assert(expected : LoginState) {
		Truth.assertThat(this).isEqualTo(expected)
	}

	private fun LoginValidation.assert(expected : LoginValidation) {
		Truth.assertThat(this).isEqualTo(expected)
	}

}
