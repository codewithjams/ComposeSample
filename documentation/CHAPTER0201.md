# Unit Testing

Let's create our example components like below:

```kotlin
sealed class ExampleAction() : Action {
	object Action1: ExampleAction()
	object Action2: ExampleAction()
	object Action3: ExampleAction(),
	object Action4: ExampleAction(), // For MiddleWare
	object Action5: ExampleAction()
}

data class ExampleState(
	val action1: Boolean = false,
	val action2: Boolean = false,
	val action3: Boolean = false,
	val middleWareExecuted: Boolean = false
) : State

class ExampleReducer() : Reducer<ExampleState, ExampleAction> {

	override fun reduce(currentState: ExampleState, action: ExampleAction): ExampleState =
		when(action) {
			ExampleAction.Action1 -> currentState.copy(action1 = true)
			ExampleAction.Action2 -> currentState.copy(action2 = true)
			ExampleAction.Action3 -> currentState.copy(action3 = true)
			ExampleAction.Action5 -> currentState.copy(middleWareExecuted = true)
			else -> currentState
		}

}

class ExampleMiddleWare() : MiddleWare<ExampleState, ExampleAction> {

	override suspend fun process(action: ExampleAction, currentState: ExampleState) {
		when(action) {
			ExampleAction.Action4 -> propagateAction(store, ExampleAction.Action5)
			else -> Unit
		}
	}

}
```

With **MVI + Redux** Design Pattern, we can Unit Test below components:
- Reducer
- MiddleWare

**NOTE**:
For assertions, I'm using [Google's Truth](https://truth.dev/) library.

```groovy
dependencies {
	testImplementation 'com.google.truth:truth:1.1.3'
}
```

## Reducer

Testing `Reducer` is straightforward: All one needs to assert is for a given `initialState` and an `action`, when reduced using `Reducer`, what new `State` it churns out.

For example:
```kotlin
class ExampleReducerTest {

	private val reducer: ExampleReducer by lazy {
		ExampleReducer()
	}

	@Test
	fun testCase00() {
		assert(
			action = ExampleAction.Action1,
			initialState = ExampleState(action1 = false),
			expectedState = ExampleState(action1 = true)
		)
	}

	@Test
	fun testCase01() {
		assert(
			action = ExampleAction.Action2,
			initialState = ExampleState(action2 = false),
			expectedState = ExampleState(action2 = true)
		)
	}

	@Test
	fun testCase02() {
		assert(
			action = ExampleAction.Action3,
			initialState = ExampleState(action3 = false),
			expectedState = ExampleState(action3 = true)
		)
	}

	... // More test cases

	private fun assert(action: ExampleAction, initialState: ExampleState, expectedState: ExampleState) {
		reducer.reduce(initialState, action).assert(expectedState)
	}

	private fun ExampleState.assert(expected: ExampleState) {
		Truth.assertThat(this).isEqualTo(expected)
	}

}
```

With that, [this](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/test/java/sample/jetpack/compose/mvi/reducer/LoginReducerTest.kt) is how unit test cases look like for [LoginReducer](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/reducer/LoginReducer.kt).

## MiddleWare

Testing `MiddleWare` is tricky, because the method `process` does not return anything.

However, we can test what `Action`s our `MiddleWare` propagates back to `Store`. This testing for actions itself can be thought of as a side-effect. So, we can create a general purpose `MiddleWare` that captures `Action` dispatched from `Store`.

```kotlin
class ActionCaptureMiddleWare<S: State, A: Action> : MiddleWare<S, A> {

	private val capturedActions: MutableList<A> = mutableListOf()

	override suspend fun process(action: A, currentState: S, store: Store<S, A>) {
		capturedActions.add(action)
	}

	fun assertContains(action: A) {
		Truth.assertThat(capturedActions).contains(action)
	}

	fun assertNotContains(action: A) {
		Truth.assertThat(capturedActions).doesNotContain(action)
	}

}
```

The idea is, we call the method `process` of `MiddleWare` under test, `MiddleWare` in turn propagates another `Action` back to `Store`, and our `ActionCaptureMiddleWare` will catch that `Action`.

Now, to test our `MiddleWare`, we do something like this: 

```kotlin
class ExampleMiddleWareTest {

	private val scope: TestScope =
		TestScope()

	private val middleWareUnderTest: ExampleMiddleWare =
		ExampleMiddleWare()

	private val actionCaptureMiddleWare: ActionCaptureMiddleWare<ExampleState, ExampleAction> =
		ActionCaptureMiddleWare()

	private val store: Store<ExampleState, ExampleAction> =
		Store(
			initialState = ExampleState(),
			reducer = ExampleReducer(),
			middleWares = listOf(
				actionCaptureMiddleWare,
				middleWareUnderTest
			)
		)

	@Test
	fun testCase00() {
		scope.runTest {
			middleWareUnderTest.process(
				action = ExampleAction.Action4,
				currentState = ExampleState(),
				store = store
			)
		}
		actionCaptureMiddleWare.apply {

			// Since Action5 is something propagated from ExampleMiddleWare, it should be captured.
			assertContains(ExampleAction.Action5)

			// ExampleMiddleWare is not propagating any other actions, so double checking them.
			assertNotContains(ExampleAction.Action1)
			assertNotContains(ExampleAction.Action2)
			assertNotContains(ExampleAction.Action3)
			assertNotContains(ExampleAction.Action4)

		}
	}

}
```

Here, I'm using Kotlin Coroutine Test dependency that provides us [`TestScope`](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/kotlinx.coroutines.test/-test-scope.html).

```groovy
dependencies {
	testImplementation 'org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1'
}
```
`TestScope` is a `CoroutineScope` designed for testing. This is being used here because the method `process` of `MiddleWare` is a `suspend`ing function that can only be called from a `CoroutineScope`.

With that, [this](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/test/java/sample/jetpack/compose/mvi/middleWare/LoginMiddleWareTest.kt) is how unit test cases look like for [LoginMiddleWare](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/middleware/LoggingMiddleWare.kt).
