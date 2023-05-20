package sample.jetpack.compose.utility.constants

import androidx.annotation.IntDef

const val MENU_OPTION_NONE = 0
const val MENU_OPTION_LOGIN = 1

@IntDef(
	value = [
		MENU_OPTION_NONE,
		MENU_OPTION_LOGIN
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
