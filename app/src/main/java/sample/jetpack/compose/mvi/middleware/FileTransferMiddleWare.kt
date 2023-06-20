package sample.jetpack.compose.mvi.middleware

import sample.jetpack.compose.mvi.action.FileTransferAction

import sample.jetpack.compose.mvi.state.FileTransferState
import sample.jetpack.compose.mvi.store.FileTransferStore

import sample.jetpack.compose.repository.SampleRepository

import javax.inject.Inject

class FileTransferMiddleWare @Inject constructor(
	private val repository : SampleRepository
) : MiddleWare<FileTransferState, FileTransferAction> {

	override suspend fun process(
		action : FileTransferAction,
		currentState : FileTransferState,
		store : FileTransferStore
	) {
		when (action) {

			FileTransferAction.Download.Pause -> onPauseDownload()

			FileTransferAction.Download.Perform -> currentState.let {
				onPerformDownload(store, it.fileName, it.url)
			}

			FileTransferAction.Download.Resume -> currentState.let {
				onResumeDownload(store, it.fileName, it.url)
			}

			else -> Unit

		}
	}

	private suspend fun onPerformDownload(
		store : FileTransferStore,
		fileName : String,
		url : String
	) {
		// TODO : Implement
	}

	private suspend fun onPauseDownload() {
		// TODO : Implement
	}

	private suspend fun onResumeDownload(
		store : FileTransferStore,
		fileName : String,
		url : String
	) {
		// TODO : Implement
	}

}
