package sample.jetpack.compose.ui.screen

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.animation.core.animateFloatAsState

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp

import sample.jetpack.compose.R

import sample.jetpack.compose.mvi.state.FileTransferState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FileTransferScreen(
	state : FileTransferState,
	onFileNameChanged : (String) -> Unit,
	onURLChanged : (String) -> Unit,
	onDownloadClicked : () -> Unit,
	onPauseClicked : () -> Unit,
	onResumeClicked : () -> Unit
) {

	val context = LocalContext.current
	val keyboardController = LocalSoftwareKeyboardController.current

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(20.dp)
	) {

		DownloaderInputFields(
			fileName = state.fileName,
			onFileNameChanged = onFileNameChanged,
			url = state.url,
			onURLChanged = onURLChanged,
			downloading = state.downloading
		)

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		Row(
			modifier = Modifier.align(alignment = Alignment.End)
		) {

			AnimatedVisibility(visible = state.downloadControlsEnabled) {
				if (state.downloadPaused) {
					Button(
						onClick = onResumeClicked
					) {
						Text(
							text = context.getString(R.string.downloader_screen_button_resume)
						)
					}
				} else {
					Button(
						onClick = onPauseClicked
					) {
						Text(
							text = context.getString(R.string.downloader_screen_button_pause)
						)
					}
				}
			}

			Button(
				onClick = {
					keyboardController?.hide()
					onDownloadClicked.invoke()
				},
				enabled = state.downloadEnabled
			) {
				Text(
					text = context.getString(R.string.downloader_screen_button_download)
				)
			}

		}

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		DownloaderProgress(
			state = state
		)

		AnimatedVisibility(
			visible = state.errorVisible
		) {
			Spacer(
				modifier = Modifier.height(10.dp)
			)
			Text(
				text = state.downloadError,
				color = Color.Red
			)
		}

		AnimatedVisibility(
			visible = state.downloadCompleted
		) {
			Spacer(
				modifier = Modifier.height(10.dp)
			)
			Text(
				text = context.getString(R.string.downloader_screen_message_downloaded),
				color = Color.Green
			)
		}

	}

}

@Composable
private fun DownloaderInputFields(
	fileName : String,
	onFileNameChanged : (String) -> Unit,
	url : String,
	onURLChanged : (String) -> Unit,
	downloading : Boolean
) {

	val context = LocalContext.current

	OutlinedTextField(
		value = fileName,
		onValueChange = onFileNameChanged,
		label = {
			Text(
				text = context.getString(R.string.downloader_screen_field_file_name)
			)
		},
		modifier = Modifier.fillMaxWidth(),
		enabled = !downloading
	)

	Spacer(
		modifier = Modifier.height(10.dp)
	)

	OutlinedTextField(
		value = url,
		onValueChange = onURLChanged,
		label = {
			Text(
				text = context.getString(R.string.downloader_screen_field_url)
			)
		},
		modifier = Modifier.fillMaxWidth(),
		enabled = !downloading
	)

}

@Composable
private fun DownloaderProgress(
	state : FileTransferState
) {

	val progress by remember { mutableStateOf(state.progressPercentage) }

	val animatedProgress by animateFloatAsState(
		targetValue = progress,
		animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
	)

	if (!state.downloading)
		return

	AnimatedVisibility(visible = true) {

		if (state.progressPercentage !in 0f .. 100f) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth()
			)
		} else {
			LinearProgressIndicator(
				progress = animatedProgress,
				modifier = Modifier.fillMaxWidth()
			)
		}

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		Text(
			text = state.downloadedText
		)

	}

}

@Preview
@Composable
private fun FileTransferScreenPreview() {
	FileTransferScreen(
		state = FileTransferState(),
		onFileNameChanged = {},
		onURLChanged = {},
		onDownloadClicked = {},
		onPauseClicked = {},
		onResumeClicked = {}
	)
}
