# Instrumentation Testing

Consider below `@Composable` screen as an example:

```kotlin
@Composable
fun ExampleScreen(
	state: ExampleState,
	onTextChanged: (String) -> Unit,
	onButtonClicked: () -> Unit
) {
	Column(
		modifier = Modifier.padding(
			horizontal = 10.dp,
			vertical = 10.dp
		)
	) {
		Text(
			text = "Some Title",
			fontSize = 14.sp
		)
		TextField(
			value = state.text,
			onValueChange = onTextChanged,
			modifier = Modifier.fillMaxWidth()
		)
		Spacer(
			modifier = Modifier.height(10.dp)
		)
		Button(
			onClick = onButtonClicked,
			modifier = Modifier.align(alignment = Alignment.End),
			enabled = state.valid
		) {
			Text(text = "Click Me")
		}
	}
}
```

This renders like below:

![Example Screen Demonstration](https://github.com/ritwikjamuar/ComposeSample/blob/main/documentation/assets/example_screen_demo.gif)

Below are the **MVI + Redux** components:

```kotlin

data class ExampleState(
	val text: String
) : State {
	val valid: Boolean
		get() = text.isNotBlank()
}

sealed class ExampleAction : Action {
	data class TextChanged(val text: String): ExampleAction()
	object ButtonClicked : ExampleAction()
}

class ExampleReducer : Reducer<ExampleState, ExampleAction> {

	override fun reduce(currentState: ExampleState, action: ExampleAction): ExampleState =
		when(action) -> {
			is ExampleAction.TextChanged -> currentState.copy(text = action.text)
			ExampleAction.ButtonClicked -> currentState
		}

}

class ExampleViewModel : MVIViewModel<ExampleState, ExampleAction> {

	override val store: Store<ExampleState, ExampleAction> =
		Store(
			initialState = ExampleState(),
			reducer = ExampleReducer(),
			middleWares = listOf()
		)

	fun onTextChanged(text: String) {
		dispatchActionToStore(ExampleAction.TextChanged(text = text))
	}

	fun onButtonClicked() {
		dispatchActionToStore(ExampleAction.ButtonClicked)
	}

}

class ExampleActivity : ComponentActivity() {

	private val viewModel: ExampleViewModel by viewModels()

	override fun onCreate(savedInstanceState: Bundle?) {
		setContent {

			val state = viewModel.viewState.collectAsState()

			ExampleApplicationTheme {
				Surface(
					modifier = Modifier.fillMaxWidth().fillMaxHeight()
				) {
					ExampleScreen(
						state = state.value,
						onTextChanged = viewModel::onTextChanged(),
						onButtonClicked = viewModel::onButtonClicked()
					)
				}
			}

		}
	}

}
```

## Theory on Instrumentation Testing with Jetpack Compose

Before starting instrumentation (translates to UI) testing, we must ask ourselves this question:

> What UI components are going to be tested?

In the above case, following components:
- Text Input
- Button

> How do we get the said components for testing?

All views in Jetpack Compose are represented as their View Tree. Beside view, there exists a [Semantic Tree](https://developer.android.com/jetpack/compose/semantics) in parallel with View Tree. This Semantic Tree is useful in Android Framework as it is used by Accessibility Services and Testing frameworks.

Here, each node in this Semantic Tree is a semantic image of same view from View Tree and is represented by [`SemanticsNodeInteraction`](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsNodeInteraction).

<br/>

> Now how do we access the individual node from Semantic Tree?

A `SemanticsNodeInteraction` is accessible from [`ComposeContentTestRule`](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/junit4/ComposeContentTestRule).

If you see from it's documentation, ComposeContentTestRule implements [`SemanticsNodeInteractionsProvider`](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsNodeInteractionsProvider) which contains method that provides the `SemanticsNodeInteraction`.

To use `ComposeContentTestRule`, the application must have [Jetpack Compose UI Test](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/package-summary) library:

```groovy
dependencies {
	androidTestImplementation platform('androidx.compose:compose-bom:2023.05.01')  
	androidTestImplementation 'androidx.compose.ui:ui-test-junit4'
}
```

Now, to get hold of a particular `SemanticsNodeInteraction`, we must provide a [`SemanticMatcher`](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsMatcher) to the method [`onNode()`](https://developer.android.com/reference/kotlin/androidx/compose/ui/test/SemanticsNodeInteractionsProvider#onNode(androidx.compose.ui.test.SemanticsMatcher,kotlin.Boolean)) of `ComposeContentTestRule`.

Upon getting hold of a `SemanticsNodeInteraction`, then we can perform assertions on it. One example of assertion in `SemanticsNodeInteraction` is whether the view is actually shown in the UI or not, or if view is clickable, and such.

Here's the Cheat-Sheet for combining `SemanticMatcher`s and Finder methods:

![compose-testing-cheatsheet.png (3393×4800) (android.com)](https://developer.android.com/images/jetpack/compose/compose-testing-cheatsheet.png)

Finally, before start performing testing, we have to populate the `@Composable` under `setContent()` method of `ComposeContentTestRule`.

Example:

```kotlin
@get: Rule
val rule : ComposeContentTestRule = createComposeRule()

@Test
fun test() {
	rule.apply {

		setContent {
			SomeScreen(
				...
			)
		}

		// hasClickAction() provides a SemanticMatcher that when supplied to onNode means find me a Node from Semantic Tree which has click action.
		onNode(hasClickAction()).apply {
			performClick()
		}

		// isSelectable() provides a SemanticMatcher that when supplied to onNode means find me a Node from Semantic Tree which is selectable.
		onNode(isSelectable()).apply {
			// There can be cases where we get hold of SemanticsNodeInteraction, but it does not mean it exists.
			// Now assert that it exists in the UI.
			assertExists()
		}

	}
}
```

## Example Implementation

We can use `hasText("DISPLAYED_TEXT_IN_UI")` as the `SemanticMatcher` like this:

```kotlin
fun test() {
	rule.apply {

		setContent {
			...
		}

		onNode(hasText("DISPLAYED_TEXT_IN_UI")).apply {
			assertExists()
		}

	}
}
```

Above code will work for sure for whatever view renders `DISPLAYED_TEXT_IN_UI`. This is easy, but not reliable. What happens when UI is complex and `DISPLAYED_TEXT_IN_UI` is shown two or more times?

A Better and reliable way to get the specific view in UI is to use `testTag`.

[`testTag()`](https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier#%28androidx.compose.ui.Modifier%29.testTag%28kotlin.String%29) is one of the extension method of [`Modifier`](https://developer.android.com/reference/kotlin/androidx/compose/ui/Modifier#%28androidx.compose.ui.Modifier) one can use in their `@Composable`.

To demonstrate it, we change our `@Composable ExampleScreen()` by adding `testTag()` to `@Composable` that renders Text Input and Button:

```kotlin
@Composable
fun ExampleScreen(
	state: ExampleState,
	onTextChanged: (String) -> Unit,
	onButtonClicked: () -> Unit
) {
	Column(
		modifier = Modifier.padding(
			horizontal = 10.dp,
			vertical = 10.dp
		)
	) {
		Text(
			text = "Some Title",
			fontSize = 14.sp
		)
		TextField(
			value = state.text,
			onValueChange = onTextChanged,
			modifier = Modifier.fillMaxWidth()
				.testTag(tag = "TEST_TAG_EXAMPLE_TEXT_FIELD") // Test Tag
		)
		Spacer(
			modifier = Modifier.height(10.dp)
		)
		Button(
			onClick = onButtonClicked,
			modifier = Modifier.align(alignment = Alignment.End)
				.testTag(tag = "TEST_TAG_EXAMPLE_BUTTON"), // Test Tag
			enabled = state.valid
		) {
			Text(text = "Click Me")
		}
	}
}
```

Finally, we test our screen as below:

```kotlin
class ExampleScreenTest {

	@get: Rule
	val rule: ComposeContentTestRule = createComposeRule()

	/**
	 * TEST CASE 1 : Empty screen
	 * EXPECTATION :
	 * - Input Text is empty.
	 * - Button is disabled.
	 */
	@Test
	fun testCase00() {
		rule.apply {

			setContent {
				TestScreen()
			}

			textInput().apply {
				assert(hasText(""))
			}

			button().apply {
				assert(isNotEnabled())
			}

		}
	}

	/**
	 * TEST CASE 2 : User types something
	 * EXPECTATION :
	 * - Button is enabled.
	 */
	@Test
	fun testCase01() {
		rule.apply {

			setContent {
				TestScreen()
			}

			textInput().apply {
				performTextInput("something")
			}

			button().apply {
				assert(isEnabled())
			}

		}
	}

	@Composable
	private fun TestScreen() {

		val viewModel = ExampleViewModel()

		Surface {

			val state = viewModel.viewState.collectAsState()

			ExampleScreen(
				state = state.value,
				onTextChanged = viewModel::onTextChanged(),
				onButtonClicked = viewModel::onButtonClicked()
			)

		}

	}

	private fun ComposeContentTestRule.textInput(): SemanticsNodeInteraction =
		onNodeWithTag(testTag = "TEST_TAG_EXAMPLE_TEXT_INPUT")

	private fun ComposeContentTestRule.button(): SemanticsNodeInteraction =
		onNodeWithTag(testTag = "TEST_TAG_EXAMPLE_BUTTON")

}
```

With all of the above, [this](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/androidTest/java/sample/jetpack/compose/ui/screen/LoginScreenTest.kt) is how I tested [LoginScreen](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/ui/screen/LoginScreen.kt).
