package sample.jetpack.compose.mvi.middleware

import kotlinx.coroutines.flow.transformWhile

import sample.jetpack.compose.mvi.action.FileTransferAction

import sample.jetpack.compose.mvi.state.FileTransferState

import sample.jetpack.compose.mvi.store.FileTransferStore

import sample.jetpack.compose.repository.SampleRepository

import sample.jetpack.compose.repository.rest.HTTPFileTransferResult

import sample.jetpack.compose.utility.helper.toByteNotation

import java.io.File

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

			FileTransferAction.Download.Pause    -> onPauseDownload()

			FileTransferAction.Download.Perform  -> currentState.let {
				onPerformDownload(store, it.fileName, it.url)
			}

			FileTransferAction.Download.Resume   -> currentState.let {
				onResumeDownload(store, it.fileName, it.url)
			}

			is FileTransferAction.Upload.Perform ->
				onPerformUpload(store, action.file, action.mimeType)

			FileTransferAction.Upload.Pause      ->
				onPauseUpload()

			FileTransferAction.Upload.Resume     ->
				onResumeUpload(store)

			else                                 -> Unit

		}
	}

	private suspend fun onPerformDownload(
		store : FileTransferStore,
		fileName : String,
		url : String
	) {
		repository.startDownload(fileName, url).transformWhile { value ->
			emit(value)
			value != HTTPFileTransferResult.Success &&
					value !is HTTPFileTransferResult.Failed &&
					value != HTTPFileTransferResult.Progress.Paused
		}.collect { result ->
			when (result) {
				HTTPFileTransferResult.None        -> Unit
				is HTTPFileTransferResult.Progress -> onFileDownloadProgress(store, result)
				is HTTPFileTransferResult.Failed   -> onFileDownloadFailed(store, result)
				HTTPFileTransferResult.Success     -> onFileDownloadSuccess(store)
			}
		}
	}

	private suspend fun onPauseDownload() {
		repository.pauseDownload()
	}

	private suspend fun onResumeDownload(
		store : FileTransferStore,
		fileName : String,
		url : String
	) {
		repository.resumeDownload(fileName, url).transformWhile { value ->
			emit(value)
			value != HTTPFileTransferResult.Success &&
					value !is HTTPFileTransferResult.Failed &&
					value != HTTPFileTransferResult.Progress.Paused
		}.collect { result ->
			when (result) {
				HTTPFileTransferResult.None        -> Unit
				is HTTPFileTransferResult.Progress -> onFileDownloadProgress(store, result)
				is HTTPFileTransferResult.Failed   -> onFileDownloadFailed(store, result)
				HTTPFileTransferResult.Success     -> onFileDownloadSuccess(store)
			}
		}
	}

	private suspend fun onFileDownloadProgress(
		store : FileTransferStore,
		result : HTTPFileTransferResult.Progress
	) {
		when (result) {

			HTTPFileTransferResult.Progress.Started        -> {
				store.dispatchIndeterminateProgress(0L)
			}

			HTTPFileTransferResult.Progress.Paused         -> {
				store.dispatchDownloadPaused()
			}

			is HTTPFileTransferResult.Progress.Transferred -> {

				if (result.totalBytes < 0) {
					store.dispatchIndeterminateProgress(result.transferredBytes)
					return
				}

				if (result.transferredBytes in 0 .. result.totalBytes) {
					store.dispatchDeterminateProgress(result.transferredBytes, result.totalBytes)
					return
				}

			}

		}
	}

	private suspend fun onFileDownloadFailed(
		store : FileTransferStore,
		result : HTTPFileTransferResult.Failed
	) {
		store.dispatchDownloadError(
			when (result) {
				is HTTPFileTransferResult.Failed.MissingPermission -> result.message
				is HTTPFileTransferResult.Failed.NoConnection      -> result.message
				is HTTPFileTransferResult.Failed.TimeOut           -> result.message
				is HTTPFileTransferResult.Failed.Unknown           -> {
					result.cause.printStackTrace()
					result.message
				}
			}
		)
	}

	private suspend fun onFileDownloadSuccess(store : FileTransferStore) {
		store.dispatchDownloadCompleted()
	}

	private suspend fun onPerformUpload(
		store : FileTransferStore,
		file : File,
		mimeType : String
	) {
		repository.startUpload(
			file = file,
			mimeType = mimeType,
			url = "http://10.0.2.2:8080/file"
		).transformWhile { value ->
			emit(value)
			value != HTTPFileTransferResult.Success &&
					value !is HTTPFileTransferResult.Failed &&
					value != HTTPFileTransferResult.Progress.Paused
		}.collect { result ->
			when (result) {
				HTTPFileTransferResult.None        -> Unit
				is HTTPFileTransferResult.Success  -> onFileUploadSuccess(store)
				is HTTPFileTransferResult.Progress -> onFileUploadProgress(store, result)
				is HTTPFileTransferResult.Failed   -> onFileUploadFailed(store, result)
			}
		}
	}

	private suspend fun onPauseUpload() {
		repository.pauseUpload()
	}

	private suspend fun onResumeUpload(store : FileTransferStore) {
		repository.resumeUpload().transformWhile { value ->
			emit(value)
			value != HTTPFileTransferResult.Success &&
					value !is HTTPFileTransferResult.Failed &&
					value != HTTPFileTransferResult.Progress.Paused
		}.collect { result ->
			when (result) {
				HTTPFileTransferResult.None        -> Unit
				is HTTPFileTransferResult.Success  -> onFileUploadSuccess(store)
				is HTTPFileTransferResult.Progress -> onFileUploadProgress(store, result)
				is HTTPFileTransferResult.Failed   -> onFileUploadFailed(store, result)
			}
		}
	}

	private suspend fun onFileUploadProgress(
		store : FileTransferStore,
		result : HTTPFileTransferResult.Progress
	) {
		when (result) {

			HTTPFileTransferResult.Progress.Started        -> {
				store.dispatchUploadIndeterminateProgress(0L)
			}

			HTTPFileTransferResult.Progress.Paused         -> {
				store.dispatchUploadPaused()
			}

			is HTTPFileTransferResult.Progress.Transferred -> {

				if (result.totalBytes < 0) {
					store.dispatchUploadIndeterminateProgress(result.transferredBytes)
					return
				}

				if (result.transferredBytes in 0 .. result.totalBytes) {
					store.dispatchUploadDeterminateProgress(
						result.transferredBytes,
						result.totalBytes
					)
					return
				}

			}

		}
	}

	private suspend fun onFileUploadFailed(
		store : FileTransferStore,
		result : HTTPFileTransferResult.Failed
	) {
		store.dispatchUploadError(
			when (result) {
				is HTTPFileTransferResult.Failed.MissingPermission -> result.message
				is HTTPFileTransferResult.Failed.NoConnection      -> result.message
				is HTTPFileTransferResult.Failed.TimeOut           -> result.message
				is HTTPFileTransferResult.Failed.Unknown           -> {
					result.cause.printStackTrace()
					result.message
				}
			}
		)
	}

	private suspend fun onFileUploadSuccess(store : FileTransferStore) {
		store.dispatchUploadCompleted()
	}

	private suspend fun FileTransferStore.dispatchIndeterminateProgress(transferredBytes : Long) {
		dispatch(
			FileTransferAction.UI.IndeterminateProgressUpdate(
				transferred = transferredBytes.toByteNotation()
			)
		)
	}

	private suspend fun FileTransferStore.dispatchDeterminateProgress(
		transferredBytes : Long,
		totalBytes : Long
	) {
		val percentage : Float = transferredBytes.toFloat() / totalBytes
		dispatch(
			FileTransferAction.UI.DeterminateProgressUpdate(
				percentage = percentage,
				transferred = "${transferredBytes.toByteNotation()} / ${totalBytes.toByteNotation()}"
			)
		)
	}

	private suspend fun FileTransferStore.dispatchDownloadError(message : String) {
		dispatch(FileTransferAction.UI.DownloadError(message))
	}

	private suspend fun FileTransferStore.dispatchDownloadPaused() {
		dispatch(FileTransferAction.UI.DownloadPaused)
	}

	private suspend fun FileTransferStore.dispatchDownloadCompleted() {
		dispatch(FileTransferAction.UI.DownloadCompleted)
	}

	private suspend fun FileTransferStore.dispatchUploadIndeterminateProgress(
		transferredBytes : Long
	) {
		dispatch(
			FileTransferAction.UI.UploadIndeterminateProgressUpdate(
				transferred = transferredBytes.toByteNotation()
			)
		)
	}

	private suspend fun FileTransferStore.dispatchUploadDeterminateProgress(
		transferredBytes : Long,
		totalBytes : Long
	) {
		val percentage : Float = transferredBytes.toFloat() / totalBytes
		dispatch(
			FileTransferAction.UI.UploadDeterminateProgressUpdate(
				percentage = percentage,
				transferred = "${transferredBytes.toByteNotation()} / ${totalBytes.toByteNotation()}"
			)
		)
	}

	private suspend fun FileTransferStore.dispatchUploadError(message : String) {
		dispatch(FileTransferAction.UI.UploadError(message))
	}

	private suspend fun FileTransferStore.dispatchUploadPaused() {
		dispatch(FileTransferAction.UI.UploadPaused)
	}

	private suspend fun FileTransferStore.dispatchUploadCompleted() {
		dispatch(FileTransferAction.UI.UploadCompleted)
	}

}
