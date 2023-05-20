package sample.jetpack.compose.mvi.state

import sample.jetpack.compose.mvi.model.LoginValidation

data class LoginState(
	val userName : String = "",
	val password : String = "",
	val userNameEnteredFirstTime : Boolean = false,
	val passwordEnteredFirstTime : Boolean = false,
	val loading : Boolean = false,
	val passwordVisible : Boolean = false
) : State {

	val userNameValidation : LoginValidation
		get() = when {
			userName.isEmpty() && userNameEnteredFirstTime ->
				LoginValidation.UserNameValidation.EMPTY_USER_NAME

			userName.length == 50                          ->
				LoginValidation.UserNameValidation.LENGTH_LIMIT_REACHED

			userName.length > 50                           ->
				LoginValidation.UserNameValidation.LENGTH_LIMIT_EXCEEDED

			else                                           ->
				LoginValidation.UserNameValidation.NONE
		}

	val passwordValidation : LoginValidation
		get() = when {
			password.isEmpty() && passwordEnteredFirstTime ->
				LoginValidation.PasswordValidation.EMPTY_PASSWORD

			password.containsIllegalCharacters()           ->
				LoginValidation.PasswordValidation.ILLEGAL_CHARACTERS

			else                                           ->
				LoginValidation.PasswordValidation.NONE
		}

	val loginEnabled : Boolean
		get() = shouldLoginEnabled()

	private fun String.containsIllegalCharacters() : Boolean = when {
		this.contains(".") -> true
		this.contains("?") -> true
		this.contains("~") -> true
		this.contains("%") -> true
		else               -> false
	}

	private fun shouldLoginEnabled() : Boolean {

		if (!userNameEnteredFirstTime)
			return false

		if (!passwordEnteredFirstTime)
			return false

		val userNameEligible : Boolean = when (userNameValidation) {
			LoginValidation.UserNameValidation.LENGTH_LIMIT_REACHED -> true
			LoginValidation.UserNameValidation.NONE                 -> true
			else                                                    -> false
		}

		val passwordEligible : Boolean = when (passwordValidation) {
			LoginValidation.PasswordValidation.NONE -> true
			else                                    -> false
		}

		return userNameEligible && passwordEligible

	}

}
