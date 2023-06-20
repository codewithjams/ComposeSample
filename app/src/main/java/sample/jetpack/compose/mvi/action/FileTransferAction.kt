package sample.jetpack.compose.mvi.action

sealed class FileTransferAction : Action {

	sealed class UI : FileTransferAction() {

		data class FileNameChanged(val newFileName : String) : UI()

		data class URLChanged(val newURL : String) : UI()

		data class IndeterminateProgressUpdate(val transferred : String) : UI()

		data class DeterminateProgressUpdate(val percentage : Float, val transferred : String) :
			UI()

		object DownloadPaused : UI()

		object DownloadCompleted : UI()

		data class DownloadError(val message : String) : UI()

	}

	sealed class Download : FileTransferAction() {

		object Perform : Download()

		object Pause : Download()

		object Resume : Download()

	}

}
