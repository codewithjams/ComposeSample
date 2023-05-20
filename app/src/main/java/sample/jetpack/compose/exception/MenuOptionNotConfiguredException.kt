package sample.jetpack.compose.exception

import java.lang.RuntimeException

private const val MESSAGE = "The Menu Option does not exist : "
private const val FIX_RESOLUTION = """
	. Please check these files:
	1. di -> activity -> MainModule.kt
	2. utility -> constants -> MenuOptionsConstants.kt
"""

class MenuOptionNotConfiguredException(optionName : String) : RuntimeException(MESSAGE + optionName + FIX_RESOLUTION)
