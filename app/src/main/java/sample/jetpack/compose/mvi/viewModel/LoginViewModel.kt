package sample.jetpack.compose.mvi.viewModel

import androidx.lifecycle.viewModelScope

import sample.jetpack.compose.mvi.action.LoginAction

import sample.jetpack.compose.mvi.middleware.LoggingMiddleWare
import sample.jetpack.compose.mvi.middleware.LoginMiddleWare

import sample.jetpack.compose.mvi.reducer.LoginReducer

import sample.jetpack.compose.mvi.state.LoginState

import sample.jetpack.compose.mvi.store.Store

import javax.inject.Inject

class LoginViewModel @Inject constructor(
	middleWare : LoginMiddleWare,
	loggingMiddleWare : LoggingMiddleWare<LoginState, LoginAction>
) : MVIViewModel<LoginState, LoginAction>() {

	override val store : Store<LoginState, LoginAction> =
		Store(
			initialState = LoginState(),
			reducer = LoginReducer(),
			middleWares = listOf(middleWare, loggingMiddleWare),
			coroutineScope = viewModelScope
		)

	fun onUserNameChanged(userName : String) {
		dispatchActionToStore(LoginAction.UserNameChanged(userName))
	}

	fun onPasswordChanged(password : String) {
		dispatchActionToStore(LoginAction.PasswordChanged(password))
	}

	fun onLoginClicked(userName : String, password : String) {
		dispatchActionToStore(LoginAction.LoginButtonClicked(userName, password))
	}

	fun onPasswordVisibilityToggleClicked() {
		dispatchActionToStore(LoginAction.PasswordVisibilityToggle)
	}

}
