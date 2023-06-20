package sample.jetpack.compose.mvi.reducer

import sample.jetpack.compose.mvi.action.FileTransferAction

import sample.jetpack.compose.mvi.state.FileTransferState

class FileTransferReducer : Reducer<FileTransferState, FileTransferAction> {

	override fun reduce(
		currentState : FileTransferState,
		action : FileTransferAction
	) : FileTransferState = when (action) {

		is FileTransferAction.UI.DeterminateProgressUpdate -> currentState.onDeterminateProgress(
			action.transferred,
			action.percentage
		)

		FileTransferAction.UI.DownloadCompleted -> currentState.onDownloadCompleted()

		is FileTransferAction.UI.DownloadError -> currentState.onDownloadError(action.message)

		FileTransferAction.UI.DownloadPaused -> currentState.onDownloadPaused()

		is FileTransferAction.UI.FileNameChanged -> currentState.onFileNameChanged(
			action.newFileName
		)

		is FileTransferAction.UI.IndeterminateProgressUpdate -> currentState.onIndeterminateProgress(
			action.transferred
		)

		is FileTransferAction.UI.URLChanged -> currentState.onURLChanged(action.newURL)

		else -> currentState

	}

	private fun FileTransferState.onFileNameChanged(newFileName : String) : FileTransferState =
		this.copy(
			fileName = newFileName
		)

	private fun FileTransferState.onURLChanged(newURL : String) : FileTransferState =
		this.copy(
			url = newURL
		)

	private fun FileTransferState.onIndeterminateProgress(transferred : String) : FileTransferState =
		this.copy(
			downloading = true,
			progressPercentage = -1f,
			downloadedText = transferred,
			downloadError = "",
			downloadPaused = false,
			downloadCompleted = false
		)

	private fun FileTransferState.onDeterminateProgress(
		transferred : String,
		percentage : Float
	) : FileTransferState =
		this.copy(
			downloading = true,
			progressPercentage = percentage,
			downloadedText = transferred,
			downloadError = "",
			downloadPaused = false,
			downloadCompleted = false
		)

	private fun FileTransferState.onDownloadPaused() : FileTransferState =
		this.copy(
			downloading = false,
			downloadPaused = true,
			downloadCompleted = false
		)

	private fun FileTransferState.onDownloadCompleted() : FileTransferState =
		this.copy(
			downloading = false,
			progressPercentage = 100f,
			downloadedText = "",
			downloadError = "",
			downloadPaused = false,
			downloadCompleted = true
		)

	private fun FileTransferState.onDownloadError(message : String) : FileTransferState =
		this.copy(
			downloading = false,
			progressPercentage = -1f,
			downloadedText = "",
			downloadError = message,
			downloadPaused = false,
			downloadCompleted = false
		)

}
