package sample.jetpack.compose.mvi.reducer

import sample.jetpack.compose.mvi.action.FileTransferAction

import sample.jetpack.compose.mvi.state.FileTransferState

class FileTransferReducer : Reducer<FileTransferState, FileTransferAction> {

	override fun reduce(
		currentState : FileTransferState,
		action : FileTransferAction
	) : FileTransferState = when (action) {

		FileTransferAction.UI.OptionDownloadClicked -> currentState.onOptionDownloadClicked()

		FileTransferAction.UI.OptionUploadClicked -> currentState.onOptionUploadClicked()

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

		is FileTransferAction.UI.UploadIndeterminateProgressUpdate ->
			currentState.onUploadIndeterminateProgress(action.transferred)

		is FileTransferAction.UI.UploadDeterminateProgressUpdate ->
			currentState.onUploadDeterminateProgress(action.percentage, action.transferred)

		FileTransferAction.UI.UploadPaused -> currentState.onUploadPaused()

		FileTransferAction.UI.UploadCompleted -> currentState.onUploadCompleted()

		is FileTransferAction.UI.UploadError -> currentState.onUploadError(action.message)

		else -> currentState

	}

	private fun FileTransferState.onOptionDownloadClicked(): FileTransferState =
		this.copy(
			transferType = FileTransferState.TransferType.DOWNLOAD
		)

	private fun FileTransferState.onOptionUploadClicked(): FileTransferState =
		this.copy(
			transferType = FileTransferState.TransferType.UPLOAD
		)

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
			transferring = true,
			transferProgress = -1f,
			transferText = transferred,
			transferError = "",
			transferPaused = false,
			transferCompleted = false
		)

	private fun FileTransferState.onDeterminateProgress(
		transferred : String,
		percentage : Float
	) : FileTransferState =
		this.copy(
			transferring = true,
			transferProgress = percentage,
			transferText = transferred,
			transferError = "",
			transferPaused = false,
			transferCompleted = false
		)

	private fun FileTransferState.onDownloadPaused() : FileTransferState =
		this.copy(
			transferring = false,
			transferPaused = true,
			transferCompleted = false
		)

	private fun FileTransferState.onDownloadCompleted() : FileTransferState =
		this.copy(
			transferring = false,
			transferProgress = 100f,
			transferText = "",
			transferError = "",
			transferPaused = false,
			transferCompleted = true
		)

	private fun FileTransferState.onDownloadError(message : String) : FileTransferState =
		this.copy(
			transferring = false,
			transferProgress = -1f,
			transferText = "",
			transferError = message,
			transferPaused = false,
			transferCompleted = false
		)

	private fun FileTransferState.onUploadIndeterminateProgress(transferred : String): FileTransferState =
		this.copy(
			transferring = true,
			transferProgress = -1f,
			transferText = transferred,
			transferError = "",
			transferPaused = false,
			transferCompleted = false
		)

	private fun FileTransferState.onUploadDeterminateProgress(percentage : Float, transferred : String): FileTransferState =
		this.copy(
			transferring = true,
			transferProgress = percentage,
			transferText = transferred,
			transferError = "",
			transferPaused = false,
			transferCompleted = false
		)

	private fun FileTransferState.onUploadPaused(): FileTransferState =
		this.copy(
			transferring = true,
			transferPaused = true,
			transferCompleted = false
		)

	private fun FileTransferState.onUploadCompleted(): FileTransferState =
		this.copy(
			transferring = false,
			transferProgress = 1.0f,
			transferText = "",
			transferError = "",
			transferPaused = false,
			transferCompleted = true
		)

	private fun FileTransferState.onUploadError(message : String): FileTransferState =
		this.copy(
			transferring = false,
			transferProgress = -1f,
			transferText = "",
			transferError = message,
			transferPaused = false,
			transferCompleted = false
		)

}
