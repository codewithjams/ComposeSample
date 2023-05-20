package sample.jetpack.compose.utility.helper

import android.app.Activity

import android.content.Intent

private typealias IntentModifier = (Intent) -> Unit

fun <T : Activity> Activity.navigateTo(
	destination : Class<T>,
	intentModifier : IntentModifier? = null,
	isFinishing : Boolean = false
) {

	startActivity(
		Intent(this, destination).apply {
			intentModifier?.invoke(this)
		}
	)

	if (!isFinishing)
		return

	finish()

}
