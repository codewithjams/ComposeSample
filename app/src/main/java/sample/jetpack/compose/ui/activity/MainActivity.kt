package sample.jetpack.compose.ui.activity

import android.os.Bundle

import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.collectAsState

import androidx.lifecycle.ViewModelProvider

import dagger.android.AndroidInjection
import sample.jetpack.compose.mvi.action.MainAction

import sample.jetpack.compose.mvi.factory.VMFactory
import sample.jetpack.compose.mvi.state.MainState

import sample.jetpack.compose.mvi.viewModel.MainViewModel

import sample.jetpack.compose.ui.screen.MainScreen

import sample.jetpack.compose.ui.theme.ComposeSampleTheme
import sample.jetpack.compose.utility.constants.MENU_OPTION_LOGIN
import sample.jetpack.compose.utility.constants.MENU_OPTION_NONE
import sample.jetpack.compose.utility.constants.MenuOptionsValidator

import sample.jetpack.compose.utility.helper.navigateTo

import javax.inject.Inject

class MainActivity : ComponentActivity() {

	@Inject
	lateinit var viewModelFactory: VMFactory

	private var _viewModel: MainViewModel? = null

	private val viewModel: MainViewModel
		get() = _viewModel!!

	override fun onCreate(savedInstanceState : Bundle?) {

		AndroidInjection.inject(this)

		_viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

		viewModel.navigationLambda = this::onNavigateLambdaInvoked

		super.onCreate(savedInstanceState)

		setContent {
			ComposeSampleTheme {

				val state = viewModel.viewState.collectAsState()

				Surface(
					color = MaterialTheme.colorScheme.background
				) {
					MainScreen(
						state = state.value,
						onMenuOptionClicked = viewModel::onMenuOptionClicked
					)
				}

			}
		}

	}

	override fun onDestroy() {
		super.onDestroy()
		viewModel.navigationLambda = null
		_viewModel = null
	}

	private fun onNavigateLambdaInvoked(@Suppress("UNUSED_PARAMETER") state: MainState, action: MainAction) {
		when(action) {
			is MainAction.MenuOptionClicked -> onNavigateScreen(action.option.id)
		}
	}

	private fun onNavigateScreen(@MenuOptionsValidator code: Int) {
		when(code) {
			MENU_OPTION_LOGIN -> navigateToLoginScreen()
			MENU_OPTION_NONE  -> Unit
		}
	}

	private fun navigateToLoginScreen() {
		navigateTo(LoginActivity::class.java)
	}

}
