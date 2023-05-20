package sample.jetpack.compose.mvi.action

sealed class LoginAction : Action {

	data class UserNameChanged(val newUserName : String) : LoginAction()

	data class PasswordChanged(val newPassword : String) : LoginAction()

	object PasswordVisibilityToggle : LoginAction()

	data class LoginButtonClicked(val userName : String, val password : String) : LoginAction()

	sealed class PerformRESTLogin : LoginAction() {
		object Loading : PerformRESTLogin()
		data class Success(val success : Boolean) : PerformRESTLogin()
		data class Error(val throwable : Throwable) : PerformRESTLogin()
	}

}
