package sample.jetpack.compose.utility.constants

import androidx.annotation.IntDef

const val MENU_OPTION_NONE = 0
const val MENU_OPTION_LOGIN = 1
const val MENU_OPTION_FILE_TRANSFER = 2

@IntDef(
	value = [
		MENU_OPTION_NONE,
		MENU_OPTION_LOGIN,
		MENU_OPTION_FILE_TRANSFER
	]
)
@Retention(AnnotationRetention.SOURCE)
@Target(
	allowedTargets = [
		AnnotationTarget.FIELD,
		AnnotationTarget.FUNCTION,
		AnnotationTarget.VALUE_PARAMETER,
		AnnotationTarget.LOCAL_VARIABLE
	]
)
annotation class MenuOptionsValidator
