package sample.jetpack.compose.mvi.model

sealed interface LoginValidation {

	enum class UserNameValidation : LoginValidation {
		EMPTY_USER_NAME,
		LENGTH_LIMIT_REACHED,
		LENGTH_LIMIT_EXCEEDED,
		NONE
	}

	enum class PasswordValidation : LoginValidation {
		EMPTY_PASSWORD,
		ILLEGAL_CHARACTERS,
		NONE
	}

	object None : LoginValidation

}
