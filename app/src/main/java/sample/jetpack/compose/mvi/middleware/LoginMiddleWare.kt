package sample.jetpack.compose.mvi.middleware

import sample.jetpack.compose.mvi.action.LoginAction

import sample.jetpack.compose.mvi.state.LoginState

import sample.jetpack.compose.mvi.store.Store

import sample.jetpack.compose.repository.SampleRepository

import java.lang.RuntimeException

import javax.inject.Inject

class LoginMiddleWare @Inject constructor(private val repository : SampleRepository) :
	MiddleWare<LoginState, LoginAction> {

	override suspend fun process(
		action : LoginAction, currentState : LoginState, store : Store<LoginState, LoginAction>
	) {
		when (action) {
			is LoginAction.LoginButtonClicked ->
				performRESTLogin(store, action.userName, action.password)
			else                              -> Unit
		}
	}

	private suspend fun performRESTLogin(
		store : Store<LoginState, LoginAction>, userName : String, password : String
	) {

		propagateAction(store, LoginAction.PerformRESTLogin.Loading)

		repository.performLogin(userName, password).collect { success ->
			if (success) {
				propagateAction(store, LoginAction.PerformRESTLogin.Success(true))
			} else {
				propagateAction(
					store,
					LoginAction.PerformRESTLogin.Error(RuntimeException("Something went wrong"))
				)
			}
		}

	}

}
