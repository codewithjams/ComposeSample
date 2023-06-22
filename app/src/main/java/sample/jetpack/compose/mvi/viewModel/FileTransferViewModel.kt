package sample.jetpack.compose.mvi.viewModel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers

import sample.jetpack.compose.mvi.action.FileTransferAction

import sample.jetpack.compose.mvi.middleware.FileTransferMiddleWare

import sample.jetpack.compose.mvi.reducer.FileTransferReducer

import sample.jetpack.compose.mvi.state.FileTransferState

import sample.jetpack.compose.mvi.store.FileTransferStore
import sample.jetpack.compose.mvi.store.Store
import java.io.File

import javax.inject.Inject

class FileTransferViewModel @Inject constructor(
	middleWare: FileTransferMiddleWare
) : MVIViewModel<FileTransferState, FileTransferAction>() {

	override val store : FileTransferStore = Store(
		initialState = FileTransferState(),
		reducer = FileTransferReducer(),
		middleWares = listOf(middleWare),
		coroutineScope = viewModelScope
	)

	fun onOptionDownloadClicked() {
		dispatchActionToStore(FileTransferAction.UI.OptionDownloadClicked)
	}

	fun onOptionUploadClicked() {
		dispatchActionToStore(FileTransferAction.UI.OptionUploadClicked)
	}

	fun onFileNameChanged(newFileName: String) {
		dispatchActionToStore(FileTransferAction.UI.FileNameChanged(newFileName))
	}

	fun onURLChanged(newURL: String) {
		dispatchActionToStore(FileTransferAction.UI.URLChanged(newURL))
	}

	fun onDownloadClicked() {
		dispatchActionToStore(FileTransferAction.Download.Perform, dispatcher = Dispatchers.IO)
	}

	fun onDownloadPauseClicked() {
		dispatchActionToStore(FileTransferAction.Download.Pause, dispatcher = Dispatchers.IO)
	}

	fun onDownloadResumeClicked() {
		dispatchActionToStore(FileTransferAction.Download.Resume, dispatcher = Dispatchers.IO)
	}

	fun onFileSelectedByUser(file: File, mimeType: String) {
		dispatchActionToStore(
			FileTransferAction.Upload.Perform(file, mimeType),
			dispatcher = Dispatchers.IO
		)
	}

}
