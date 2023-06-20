package sample.jetpack.compose.mvi.state

data class FileTransferState(
	val fileName: String = "",
	val url: String = "",
	val downloadedText: String = "",
	val progressPercentage: Float = 0.0f,
	val downloading: Boolean = false,
	val downloadError: String = "",
	val downloadPaused: Boolean = false,
	val downloadCompleted: Boolean = false
) : State {

	val downloadEnabled: Boolean
		get() = isDownloadingEnabled()

	val errorVisible: Boolean
		get() = downloadError.isNotBlank()

	val downloadControlsEnabled: Boolean
		get() = isDownloadControlsEnabled()

	private fun isDownloadingEnabled(): Boolean {

		if (fileName.isBlank() || url.isBlank())
			return false

		if (downloadPaused)
			return false

		if (downloading)
			return false

		return true

	}

	private fun isDownloadControlsEnabled(): Boolean {

		if (downloading)
			return true

		if (downloadCompleted)
			return false

		if (errorVisible)
			return false

		return true

	}

}
