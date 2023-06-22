package sample.jetpack.compose.utility.helper

fun Long.toByteNotation(): String {

	var power = 0
	var value: Float = this.toFloat()

	while (value > 1024) {
		power++
		value /= 1024
	}

	val unit = when(power) {
		0 -> "Bytes"
		1 -> "KB"
		2 -> "MB"
		3 -> "GB"
		4 -> "TB"
		5 -> "PB"
		6 -> "EB"
		7 -> "ZB"
		8 -> "YB"
		else -> "*B"
	}

	val formattedValue = String.format("%.2f", value)

	return "$formattedValue $unit"

}

class MIMETypesCollection {

	companion object {

		val IMAGES : Array<String> = arrayOf(
			"image/gif",
			"image/ief",
			"image/jp2",
			"image/jpeg",
			"image/jpm",
			"image/jpx",
			"image/naplps",
			"image/pcx",
			"image/png",
			"image/prs.btif",
			"image/prs.pti",
			"image/svg+xml"
		)

		val VIDEOS : Array<String> = arrayOf(
			"video/3gpp",
			"video/annodex",
			"video/dl",
			"video/dv",
			"video/fli",
			"video/gl",
			"video/mpeg",
			"video/MP2T",
			"video/mp4",
			"video/quicktime",
			"video/mp4v-es",
			"video/parityfec",
			"video/pointer",
			"video/webm"
		)

		val DOCUMENTS : Array<String> = arrayOf(
			"application/gzip",
			"application/msword",
			"application/onenote",
			"application/pdf",
			"application/rar",
			"application/rtf",
			"application/zip"
		)

	}

}
