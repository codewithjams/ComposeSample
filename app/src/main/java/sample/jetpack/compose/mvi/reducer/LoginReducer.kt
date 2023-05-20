package sample.jetpack.compose.mvi.reducer

import sample.jetpack.compose.mvi.action.LoginAction

import sample.jetpack.compose.mvi.state.LoginState

class LoginReducer : Reducer<LoginState, LoginAction> {

	override fun reduce(currentState : LoginState, action : LoginAction) : LoginState =
		when (action) {

			is LoginAction.PasswordChanged          ->
				onPasswordChanged(currentState, action.newPassword)

			LoginAction.PasswordVisibilityToggle    ->
				onPasswordVisibilityToggled(currentState)

			is LoginAction.UserNameChanged          ->
				onUserNameChanged(currentState, action.newUserName)

			LoginAction.PerformRESTLogin.Loading    ->
				onPerformRESTLoginLoading(currentState)

			is LoginAction.PerformRESTLogin.Success ->
				onPerformRESTLoginSuccess(currentState)

			is LoginAction.PerformRESTLogin.Error   ->
				onPerformRESTLoginError(currentState)

			else                                    -> currentState

		}

	private fun onPasswordChanged(currentState : LoginState, password : String) : LoginState =
		currentState.copy(
			password = password,
			passwordEnteredFirstTime = true
		)

	private fun onUserNameChanged(currentState : LoginState, userName : String) : LoginState =
		currentState.copy(
			userName = userName,
			userNameEnteredFirstTime = true
		)

	private fun onPasswordVisibilityToggled(currentState : LoginState) : LoginState =
		currentState.copy(
			passwordVisible = currentState.passwordVisible.not()
		)

	private fun onPerformRESTLoginLoading(currentState : LoginState) : LoginState =
		currentState.copy(
			loading = true
		)

	private fun onPerformRESTLoginSuccess(currentState : LoginState) : LoginState =
		currentState.copy(
			loading = false
		)

	private fun onPerformRESTLoginError(currentState : LoginState) : LoginState =
		currentState.copy(
			loading = false
		)

}
