package sample.jetpack.compose.mvi.action

import java.io.File

sealed class FileTransferAction : Action {

	sealed class UI : FileTransferAction() {

		object OptionDownloadClicked : UI()

		object OptionUploadClicked : UI()

		data class FileNameChanged(val newFileName : String) : UI()

		data class URLChanged(val newURL : String) : UI()

		data class IndeterminateProgressUpdate(val transferred : String) : UI()

		data class DeterminateProgressUpdate(val percentage : Float, val transferred : String) :
			UI()

		object DownloadPaused : UI()

		object DownloadCompleted : UI()

		data class DownloadError(val message : String) : UI()

		data class UploadIndeterminateProgressUpdate(val transferred : String) : UI()

		data class UploadDeterminateProgressUpdate(
			val percentage : Float,
			val transferred : String
		) : UI()

		object UploadPaused : UI()

		object UploadCompleted : UI()

		data class UploadError(val message : String) : UI()

	}

	sealed class Download : FileTransferAction() {

		object Perform : Download()

		object Pause : Download()

		object Resume : Download()

	}

	sealed class Upload : FileTransferAction() {

		data class Perform(val file : File, val mimeType : String) : Upload()

		object Pause : Upload()

		object Resume : Upload()

	}

}
