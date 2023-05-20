package sample.jetpack.compose.exception

import java.lang.RuntimeException

private const val MESSAGE = """
	There are no menu options to populate from.
	Please add some menu options under strings.xml
	as string-array resource named 'main_screen_menu_options'
"""

class NoMenuOptionsException : RuntimeException(MESSAGE)
