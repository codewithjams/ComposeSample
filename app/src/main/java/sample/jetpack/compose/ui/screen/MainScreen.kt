package sample.jetpack.compose.ui.screen

import android.annotation.SuppressLint

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.material3.Button
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import sample.jetpack.compose.R

import sample.jetpack.compose.mvi.model.MenuOption
import sample.jetpack.compose.mvi.state.MainState

import sample.jetpack.compose.ui.theme.openSansFontFamily
import sample.jetpack.compose.utility.constants.TEST_TAG_MAIN_MENU_OPTION_BUTTON
import sample.jetpack.compose.utility.constants.TEST_TAG_MAIN_TEXT_GREETINGS

@Composable
fun MainScreen(
	state: MainState,
	onMenuOptionClicked : (MenuOption) -> Unit
) {

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.padding(10.dp),
		verticalArrangement = Arrangement.Center,
		horizontalAlignment = Alignment.CenterHorizontally
	) {

		GreetingsText(
			name = stringResource(id = R.string.main_screen_user_name),
			modifier = Modifier.testTag(tag = TEST_TAG_MAIN_TEXT_GREETINGS)
		)

		Spacer(
			modifier = Modifier.height(20.dp)
		)

		Text(
			text = stringResource(id = R.string.main_screen_menu_options_description),
			fontFamily = openSansFontFamily,
			fontWeight = FontWeight.Light,
			fontStyle = FontStyle.Normal
		)

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		LazyColumn {
			itemsIndexed(
				items = state.menuOptions
			) { index, menuOption ->
				MenuOptionItem(
					option = menuOption,
					onClick = onMenuOptionClicked,
					buttonModifier = Modifier.testTag(tag = TEST_TAG_MAIN_MENU_OPTION_BUTTON + index)
				)
			}
		}

	}

}

@Composable
private fun GreetingsText(
	name : String,
	modifier : Modifier = Modifier
) {
	Text(
		text = stringResource(R.string.main_screen_greetings_name, name),
		modifier = modifier,
		fontFamily = openSansFontFamily,
		fontWeight = FontWeight.Normal,
		fontStyle = FontStyle.Normal
	)
}

@Composable
private fun MenuOptionItem(
	option : MenuOption,
	onClick : (MenuOption) -> Unit,
	@SuppressLint("ModifierParameter") buttonModifier : Modifier = Modifier,
	textModifier : Modifier = Modifier
) {
	Button(
		onClick = {
			onClick.invoke(option)
		},
		modifier = buttonModifier
	) {
		Text(
			text = option.name,
			modifier = textModifier
		)
	}
}
