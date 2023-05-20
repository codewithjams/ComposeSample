package sample.jetpack.compose.ui.activity

import android.os.Bundle

import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Surface

import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier

import androidx.lifecycle.ViewModelProvider

import dagger.android.AndroidInjection

import sample.jetpack.compose.mvi.factory.VMFactory

import sample.jetpack.compose.mvi.viewModel.LoginViewModel

import sample.jetpack.compose.ui.screen.LoginScreen

import sample.jetpack.compose.ui.theme.ComposeSampleTheme

import javax.inject.Inject

class LoginActivity : ComponentActivity() {

	@Inject
	lateinit var viewModelFactory : VMFactory

	private var _viewModel : LoginViewModel? = null

	private val viewModel : LoginViewModel
		get() = _viewModel!!

	override fun onCreate(savedInstanceState : Bundle?) {

		AndroidInjection.inject(this)

		_viewModel = ViewModelProvider(this, viewModelFactory)[LoginViewModel::class.java]

		super.onCreate(savedInstanceState)

		setContent {
			ComposeSampleTheme {

				val state = viewModel.viewState.collectAsState()

				Surface(
					modifier = Modifier.fillMaxSize()
				) {
					LoginScreen(
						state = state.value,
						onUserNameChanged = viewModel::onUserNameChanged,
						onPasswordChanged = viewModel::onPasswordChanged,
						onLoginClick = viewModel::onLoginClicked,
						onPasswordVisibilityToggle = viewModel::onPasswordVisibilityToggleClicked
					)
				}

			}
		}

	}

	override fun onDestroy() {
		super.onDestroy()
		_viewModel = null
	}

}
