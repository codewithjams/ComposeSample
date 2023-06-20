package sample.jetpack.compose.ui.activity

import android.os.Bundle

import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Surface

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier

import androidx.lifecycle.ViewModelProvider

import dagger.android.AndroidInjection

import sample.jetpack.compose.mvi.factory.VMFactory

import sample.jetpack.compose.mvi.state.FileTransferState

import sample.jetpack.compose.mvi.viewModel.FileTransferViewModel

import sample.jetpack.compose.ui.screen.FileTransferScreen

import sample.jetpack.compose.ui.theme.ComposeSampleTheme

import javax.inject.Inject

class FileTransferActivity : ComponentActivity() {

	@Inject
	lateinit var viewModelFactory : VMFactory

	private var _viewModel : FileTransferViewModel? = null

	private val viewModel : FileTransferViewModel
		get() = _viewModel!!

	override fun onCreate(savedInstanceState : Bundle?) {

		AndroidInjection.inject(this)

		_viewModel = ViewModelProvider(this, viewModelFactory)[FileTransferViewModel::class.java]

		super.onCreate(savedInstanceState)

		setContent {
			ComposeSampleTheme {

				val state : State<FileTransferState> = viewModel.viewState.collectAsState()

				Surface(
					modifier = Modifier.fillMaxSize()
				) {
					FileTransferScreen(
						state = state.value,
						onFileNameChanged = viewModel::onFileNameChanged,
						onURLChanged = viewModel::onURLChanged,
						onDownloadClicked = viewModel::onDownloadClicked,
						onPauseClicked = viewModel::onDownloadPauseClicked,
						onResumeClicked = viewModel::onDownloadResumeClicked
					)
				}

			}
		}

	}

}
