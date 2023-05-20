package sample.jetpack.compose.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import sample.jetpack.compose.R

val openSansFontFamily = FontFamily(
	Font(
		resId = R.font.open_sans_semi_condensed_light,
		weight = FontWeight.Light,
		style = FontStyle.Normal
	),
	Font(
		resId = R.font.open_sans_semi_condensed_light_italic,
		weight = FontWeight.Light,
		style = FontStyle.Italic
	),
	Font(
		resId = R.font.open_sans_semi_condensed_regular,
		weight = FontWeight.Normal,
		style = FontStyle.Normal
	),
	Font(
		resId = R.font.open_sans_semi_condensed_italic,
		weight = FontWeight.Normal,
		style = FontStyle.Italic
	),
	Font(
		resId = R.font.open_sans_semi_condensed_semi_bold,
		weight = FontWeight.Bold,
		style = FontStyle.Normal
	),
	Font(
		resId = R.font.open_sans_semi_condensed_semi_bold_italic,
		weight = FontWeight.Bold,
		style = FontStyle.Italic
	)
)
