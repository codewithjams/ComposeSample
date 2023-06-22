package sample.jetpack.compose.mvi.state

data class FileTransferState(
	val fileName : String = "",
	val url : String = "",
	val transferType: TransferType = TransferType.NONE,
	val transferText : String = "",
	val transferProgress : Float = 0.0f,
	val transferring : Boolean = false,
	val transferError : String = "",
	val transferPaused : Boolean = false,
	val transferCompleted : Boolean = false
) : State {

	val transferEnabled : Boolean
		get() = isTransferEnabled()

	val errorVisible : Boolean
		get() = transferError.isNotBlank()

	private fun isTransferEnabled() : Boolean {

		if (transferType == TransferType.DOWNLOAD && (fileName.isBlank() || url.isBlank()))
			return false

		if (transferPaused)
			return false

		if (transferring)
			return false

		return true

	}

	enum class TransferType {
		NONE,
		DOWNLOAD,
		UPLOAD
	}

}
