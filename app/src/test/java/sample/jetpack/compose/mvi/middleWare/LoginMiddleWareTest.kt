package sample.jetpack.compose.mvi.middleWare

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest

import org.junit.Test

import sample.jetpack.compose.mvi.action.LoginAction

import sample.jetpack.compose.mvi.middleware.LoginMiddleWare

import sample.jetpack.compose.mvi.reducer.LoginReducer

import sample.jetpack.compose.mvi.repository.FakeSampleRepository

import sample.jetpack.compose.mvi.state.LoginState

import sample.jetpack.compose.mvi.store.Store

class LoginMiddleWareTest {

	private val scope : TestScope =
		TestScope()

	private val middleWareUnderTest : LoginMiddleWare =
		LoginMiddleWare(FakeSampleRepository())

	private val actionCaptureMiddleWare : ActionCaptureMiddleWare<LoginState, LoginAction> =
		ActionCaptureMiddleWare()

	private val store : Store<LoginState, LoginAction> =
		Store(
			initialState = LoginState(),
			reducer = LoginReducer(),
			middleWares = listOf(actionCaptureMiddleWare),
			coroutineScope = scope
		)

	@Test
	fun `test case 00`() {
		scope.runTest {
			middleWareUnderTest.process(
				action = LoginAction.LoginButtonClicked(userName = "abc", password = "123"),
				currentState = LoginState(
					userName = "abc",
					userNameEnteredFirstTime = true,
					password = "123",
					passwordEnteredFirstTime = true
				),
				store = store
			)
		}
		actionCaptureMiddleWare.apply {
			assertContains(LoginAction.PerformRESTLogin.Loading)
			assertContains(LoginAction.PerformRESTLogin.Success(success = true))
		}
	}

}
