package sample.jetpack.compose.ui.screen

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width

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
import androidx.compose.ui.graphics.ColorFilter

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp

import sample.jetpack.compose.R

import sample.jetpack.compose.mvi.state.FileTransferState

@Composable
fun FileTransferScreen(
	state : FileTransferState,
	onOptionDownloadClicked : () -> Unit,
	onOptionUploadClicked : () -> Unit,
	onDownloadClicked : () -> Unit,
	onUploadClicked : () -> Unit,
	onFileNameChanged : (String) -> Unit,
	onURLChanged : (String) -> Unit,
	onPauseClicked : () -> Unit,
	onResumeClicked : () -> Unit,
) {

	when (state.transferType) {

		FileTransferState.TransferType.NONE ->
			TransferTypeSelector(
				onOptionDownloadClicked = onOptionDownloadClicked,
				onOptionUploadClicked = onOptionUploadClicked
			)

		FileTransferState.TransferType.DOWNLOAD ->
			DownloaderScreen(
				state = state,
				onFileNameChanged = onFileNameChanged,
				onURLChanged = onURLChanged,
				onDownloadClicked = onDownloadClicked,
				onPauseClicked = onPauseClicked,
				onResumeClicked = onResumeClicked
			)

		FileTransferState.TransferType.UPLOAD ->
			UploaderScreen(
				state = state,
				onUploadClicked = onUploadClicked,
				onPauseClicked = onPauseClicked,
				onResumeClicked = onResumeClicked
			)

	}

}

@Composable
private fun TransferTypeSelector(
	onOptionDownloadClicked : () -> Unit,
	onOptionUploadClicked : () -> Unit
) {

	val context = LocalContext.current

	Row(
		modifier = Modifier
			.fillMaxWidth()
			.padding(all = 20.dp)
	) {

		Button(
			onClick = onOptionDownloadClicked
		) {
			Text(
				text = context.getString(R.string.downloader_screen_button_download)
			)
		}

		Spacer(modifier = Modifier.width(10.dp))

		Button(
			onClick = onOptionUploadClicked
		) {
			Text(
				text = context.getString(R.string.file_transfer_screen_button_upload)
			)
		}

	}

}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun DownloaderScreen(
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

		OutlinedTextField(
			value = state.fileName,
			onValueChange = onFileNameChanged,
			label = {
				Text(
					text = context.getString(R.string.downloader_screen_field_file_name)
				)
			},
			modifier = Modifier.fillMaxWidth(),
			enabled = !state.transferring
		)

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		OutlinedTextField(
			value = state.url,
			onValueChange = onURLChanged,
			label = {
				Text(
					text = context.getString(R.string.downloader_screen_field_url)
				)
			},
			modifier = Modifier.fillMaxWidth(),
			enabled = !state.transferring
		)

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		Button(
			onClick = {
				keyboardController?.hide()
				onDownloadClicked.invoke()
			},
			enabled = state.transferEnabled,
			modifier = Modifier.fillMaxWidth()
		) {
			Text(
				text = context.getString(R.string.downloader_screen_button_download)
			)
		}

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		val progress by remember { mutableStateOf(state.transferProgress) }

		val animatedProgress by animateFloatAsState(
			targetValue = progress,
			animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
		)

		if (!state.transferring)
			return

		AnimatedVisibility(visible = true) {

			Column(
				modifier = Modifier.fillMaxWidth()
			) {

				if (state.transferProgress !in 0f .. 100f) {
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
					text = state.transferText,
					modifier = Modifier.align(alignment = Alignment.End)
				)

				Spacer(
					modifier = Modifier.height(20.dp)
				)

				Row(
					modifier = Modifier.align(alignment = Alignment.End)
				) {

					Button(
						onClick = onPauseClicked,
						enabled = state.transferring
					) {
						Image(
							painter = painterResource(id = R.drawable.ic_pause),
							contentDescription = context.getString(R.string.downloader_screen_button_pause),
							colorFilter = ColorFilter.tint(Color.White)
						)
					}

					Spacer(
						modifier = Modifier.width(10.dp)
					)

					Button(
						onClick = onResumeClicked,
						enabled = !state.transferring
					) {
						Image(
							painter = painterResource(id = R.drawable.ic_play),
							contentDescription = context.getString(R.string.downloader_screen_button_resume),
							colorFilter = ColorFilter.tint(Color.White)
						)
					}

				}

			}

		}

		AnimatedVisibility(
			visible = state.errorVisible
		) {
			Spacer(
				modifier = Modifier.height(10.dp)
			)
			Text(
				text = state.transferError,
				color = Color.Red
			)
		}

		AnimatedVisibility(
			visible = state.transferCompleted
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
private fun UploaderScreen(
	state : FileTransferState,
	onUploadClicked : () -> Unit,
	onPauseClicked : () -> Unit,
	onResumeClicked : () -> Unit
) {

	val context = LocalContext.current

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(
				all = 20.dp
			)
	) {

		Button(
			onClick = onUploadClicked,
			modifier = Modifier.fillMaxWidth(),
			enabled = state.transferEnabled
		) {
			Text(
				text = context.getString(R.string.file_transfer_screen_button_upload)
			)
		}

		AnimatedVisibility(
			visible = state.transferring,
			modifier = Modifier.fillMaxWidth()
		) {

			val progress by remember { mutableStateOf(state.transferProgress) }

			val animatedProgress by animateFloatAsState(
				targetValue = progress,
				animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
			)

			Column(modifier = Modifier.fillMaxWidth()) {

				Spacer(
					modifier = Modifier.height(10.dp)
				)

				if (state.transferProgress !in 0f .. 100f) {
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
					text = state.transferText,
					modifier = Modifier
						.align(alignment = Alignment.End)
				)

				Row {

					Button(
						onClick = onPauseClicked,
						enabled = state.transferring
					) {
						Image(
							painter = painterResource(id = R.drawable.ic_pause),
							contentDescription = context.getString(R.string.downloader_screen_button_pause),
							colorFilter = ColorFilter.tint(Color.White)
						)
					}

					Spacer(
						modifier = Modifier.width(20.dp)
					)

					Button(
						onClick = onResumeClicked,
						enabled = !state.transferring
					) {
						Image(
							painter = painterResource(id = R.drawable.ic_play),
							contentDescription = context.getString(R.string.downloader_screen_button_resume),
							colorFilter = ColorFilter.tint(Color.White)
						)
					}

				}

			}

		}

		AnimatedVisibility(
			visible = state.errorVisible
		) {
			Spacer(
				modifier = Modifier.height(10.dp)
			)
			Text(
				text = state.transferError,
				color = Color.Red
			)
		}

		AnimatedVisibility(
			visible = state.transferCompleted
		) {
			Spacer(
				modifier = Modifier.height(10.dp)
			)
			Text(
				text = context.getString(R.string.file_transfer_screen_message_uploaded),
				color = Color.Green
			)
		}

	}

}

@Preview
@Composable
private fun FileTransferScreenPreview() {
	FileTransferScreen(
		state = FileTransferState(),
		onOptionDownloadClicked = {},
		onOptionUploadClicked = {},
		onDownloadClicked = {},
		onUploadClicked = {},
		onFileNameChanged = {},
		onURLChanged = {},
		onPauseClicked = {},
		onResumeClicked = {}
	)
}

@Preview
@Composable
private fun DownloaderScreenPreview() {
	DownloaderScreen(
		state = FileTransferState(
			transferType = FileTransferState.TransferType.DOWNLOAD
		),
		onFileNameChanged = {},
		onURLChanged = {},
		onDownloadClicked = {},
		onPauseClicked = {},
		onResumeClicked = {}
	)
}

@Preview
@Composable
private fun UploaderScreenPreview() {
	UploaderScreen(
		state = FileTransferState(
			transferType = FileTransferState.TransferType.UPLOAD
		),
		onUploadClicked = {},
		onPauseClicked = {},
		onResumeClicked = {}
	)
}
