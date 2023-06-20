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
