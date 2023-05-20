package sample.jetpack.compose.ui.screen

import androidx.annotation.DrawableRes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight

import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag

import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import sample.jetpack.compose.R

import sample.jetpack.compose.mvi.model.LoginValidation
import sample.jetpack.compose.mvi.state.LoginState

import sample.jetpack.compose.ui.theme.openSansFontFamily

import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_LOGIN
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_PASSWORD_VALIDATION_ERROR
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_PASSWORD_VISIBILITY_TOGGLE
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_PROGRESS_INDICATOR
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_PASSWORD
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_USER_NAME
import sample.jetpack.compose.utility.constants.TEST_TAG_LOGIN_INPUT_FIELD_TEXT_USER_NAME_VALIDATION_ERROR

@Composable
fun LoginScreen(
	state : LoginState,
	onUserNameChanged : (String) -> Unit,
	onPasswordChanged : (String) -> Unit,
	onLoginClick : (String, String) -> Unit,
	onPasswordVisibilityToggle : () -> Unit
) {

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.fillMaxHeight()
			.padding(
				horizontal = 10.dp,
				vertical = 10.dp
			)
	) {

		LoginInputField(
			placeholder = stringResource(id = R.string.login_screen_field_user_name),
			value = state.userName,
			validation = state.userNameValidation,
			enabled = !state.loading,
			onTextChanged = onUserNameChanged
		)

		Spacer(
			modifier = Modifier.height(5.dp)
		)

		LoginInputField(
			placeholder = stringResource(id = R.string.login_screen_field_password),
			value = state.password,
			validation = state.passwordValidation,
			secretiveInput = true,
			secretiveInputVisible = state.passwordVisible,
			enabled = !state.loading,
			onTextChanged = onPasswordChanged,
			onSecretiveVisibilityToggle = onPasswordVisibilityToggle
		)

		Spacer(
			modifier = Modifier.height(10.dp)
		)

		AnimatedVisibility(
			visible = state.loading,
			enter = fadeIn(),
			exit = fadeOut()
		) {
			LinearProgressIndicator(
				modifier = Modifier.fillMaxWidth()
					.semantics {
						testTag = TEST_TAG_LOGIN_INPUT_FIELD_PROGRESS_INDICATOR
					}
			)
			Spacer(
				modifier = Modifier.height(15.dp)
			)
		}

		Button(
			onClick = {
				onLoginClick.invoke(state.userName, state.password)
			},
			enabled = state.loginEnabled && !state.loading,
			modifier = Modifier.semantics {
				testTag = TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_LOGIN
			}
		) {
			Text(
				text = stringResource(id = R.string.login_screen_button_login),
				fontFamily = openSansFontFamily,
				fontWeight = FontWeight.Normal,
				fontStyle = FontStyle.Normal,
				textAlign = TextAlign.Center,
				modifier = Modifier
					.wrapContentHeight(align = Alignment.CenterVertically)
					.height(20.dp)
			)
		}

	}

}


@Composable
@Preview
private fun LoginInputField(
	@PreviewParameter(PlaceholderValueParameter::class) placeholder : String,
	value : String = "",
	validation : LoginValidation = LoginValidation.None,
	secretiveInput : Boolean = false,
	secretiveInputVisible : Boolean = false,
	enabled : Boolean = true,
	onTextChanged : (String) -> Unit = {},
	onSecretiveVisibilityToggle : () -> Unit = {}
) {

	Column(
		modifier = Modifier.fillMaxWidth()
	) {

		Text(
			text = placeholder,
			fontSize = 18.sp,
			fontFamily = openSansFontFamily,
			fontWeight = FontWeight.Normal,
			fontStyle = FontStyle.Normal
		)

		Spacer(
			modifier = Modifier.height(5.dp)
		)

		val keyboardOptions : KeyboardOptions = if (secretiveInput) {
			KeyboardOptions(keyboardType = KeyboardType.Password)
		} else {
			KeyboardOptions(keyboardType = KeyboardType.Text)
		}

		val visualTransformation : VisualTransformation = when {
			secretiveInput && !secretiveInputVisible -> PasswordVisualTransformation()
			else                                     -> VisualTransformation.None
		}

		val trailingIcon : (@Composable () -> Unit)? = if (!secretiveInput) {
			null
		} else {
			{

				IconButton(
					onClick = {
						onSecretiveVisibilityToggle.invoke()
					},
					modifier = Modifier.semantics {
						testTag = TEST_TAG_LOGIN_INPUT_FIELD_BUTTON_PASSWORD_VISIBILITY_TOGGLE
					}
				) {

					@DrawableRes val iconResource : Int = if (secretiveInputVisible) {
						R.drawable.ic_baseline_visibility_off
					} else {
						R.drawable.ic_baseline_visibility
					}

					Icon(
						painter = painterResource(id = iconResource),
						tint = MaterialTheme.colorScheme.primary,
						contentDescription = stringResource(
							id = R.string.content_description_login_password_visibility_toggle
						)
					)

				}

			}
		}

		TextField(
			value = value,
			onValueChange = onTextChanged,
			enabled = enabled,
			textStyle = TextStyle(
				fontSize = 22.sp,
				fontFamily = openSansFontFamily,
				fontWeight = FontWeight.Normal,
				fontStyle = FontStyle.Normal
			),
			singleLine = true,
			keyboardOptions = keyboardOptions,
			visualTransformation = visualTransformation,
			trailingIcon = trailingIcon,
			shape = RoundedCornerShape(5.dp),
			colors = TextFieldDefaults.colors(),
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 5.dp)
				.testTag(
					if (secretiveInput) {
						TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_PASSWORD
					} else {
						TEST_TAG_LOGIN_INPUT_FIELD_TEXT_INPUT_USER_NAME
					}
				)
		)

		val (message : String, color : Color, shouldBeVisible: Boolean) = when (validation) {

			LoginValidation.UserNameValidation.EMPTY_USER_NAME       ->
				Triple(
					stringResource(id = R.string.login_screen_validation_empty_user_name),
					Color.Red,
					true
				)

			LoginValidation.UserNameValidation.LENGTH_LIMIT_REACHED  ->
				Triple(
					stringResource(id = R.string.login_screen_validation_user_name_length_limit_reached),
					Color.Yellow,
					true
				)

			LoginValidation.UserNameValidation.LENGTH_LIMIT_EXCEEDED ->
				Triple(
					stringResource(id = R.string.login_screen_validation_user_name_length_exceeded, value.length - 50),
					Color.Red,
					true
				)

			LoginValidation.PasswordValidation.EMPTY_PASSWORD        ->
				Triple(
					stringResource(id = R.string.login_screen_validation_empty_password),
					Color.Red,
					true
				)

			LoginValidation.PasswordValidation.ILLEGAL_CHARACTERS    ->
				Triple(
					stringResource(id = R.string.login_screen_validation_password_containing_illegal_characters),
					Color.Red,
					true
				)

			else                                                     ->
				Triple(
					"",
					Color.Transparent,
					false
				)

		}

		AnimatedVisibility(
			visible = shouldBeVisible,
			enter = fadeIn(),
			exit = fadeOut()
		) {
			Text(
				text = message,
				fontSize = 16.sp,
				color = color,
				fontFamily = openSansFontFamily,
				fontWeight = FontWeight.Light,
				fontStyle = FontStyle.Normal,
				modifier = Modifier.testTag(
					if (secretiveInput) {
						TEST_TAG_LOGIN_INPUT_FIELD_TEXT_PASSWORD_VALIDATION_ERROR
					} else {
						TEST_TAG_LOGIN_INPUT_FIELD_TEXT_USER_NAME_VALIDATION_ERROR
					}
				)
			)
		}

	}
}

private class PlaceholderValueParameter : PreviewParameterProvider<String> {

	override val values : Sequence<String>
		get() = sequenceOf("name")

}
