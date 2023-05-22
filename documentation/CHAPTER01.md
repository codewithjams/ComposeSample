# Chapter 1: Tour de Design Pattern

As I get a hang on developing UI components, I have tried out these Design Pattern:

- MVVM
- MVI

With MVVM, I ended up managing a UI Data class that gets updated every time some event happened at UI and ViewModel ends up manipulating that data class. This got me an a squint look at MVI design pattern, since in this, essentially a new state gets propagated at some event. So, went on a ride with MVI, coupled with Redux pattern.

## A ride with MVI + Redux
For MVI + Redux, I consulted [this](https://github.com/adammc331/mviexample) repository (Shout-out to [Adam McNelly](https://www.youtube.com/@AdamMc331) for the simplistic yet powerful implementation). To give a brief look at how the design pattern looks like:

A `State` represents the current view in the UI.
```kotlin
interface State
```

Whenever some event triggers from UI, those events are considered `Action`.
```kotlin
interface Action
```

The purpose of `Reducer` is to take an `Action` and current `State`, and based on `Action`, transform this current `State` to a new `State`.
```kotlin
interface Reducer<S: State, A: Action> {

	fun process(action: A, currentState: S): S

}
```

So far, so good, but above definitions are not enough to handle any side-effects (as `Reducer` is very simple in nature, can't perform anything beyond manipulating the UI `State`). To handle any side-effect, a new abstraction is introduced:

`MiddleWare` handles any kind of side-effect (`Action` as Performing REST API Call ,or Logging any dispatched action and such are some examples of side-effects)  dispatched.
```kotlin
interface MiddleWare<S: State, A: Action> {

	suspend fun process(action: A, currentState: S, store: Store<S, A>)

	suspend fun propagateAction(store: Store<S, A>, action: A) =
		store.dispatch(action)

}
```

In `MiddleWare`. we can see an unknown entity called `Store`. Well, `Store` is what orchestrates the MVI. Under the method `dispatch()`, `Store` does following:

- Process the `Action` in all of the `MiddleWare`s.
- Reduce a given `Action` using `Reducer` and propagate the new `State`.

```kotlin
class Store<S: State, A : Action>(
	initialState: S,
	private val reducer: Reducer<S, A>,
	private val middleWares: List<MiddleWare<S, A>> = emptyList()
) {

	private val mutableState: MutableStateFlow<S> =
		MutableStateFlow(
			value = initialState
		)

	val state: StateFlow<S>
		get = mutableState

	private val currentState: S
		get = mutableState.value

	suspend fun dispatch(action: A) {

		middleWares.forEach { middleWare ->
			middleWare.process(action, currentState, this)
		}

		mutableState.value = reducer.reduce(action, currentState)

	}

}
```

With all of the above, it's still better if the `State` can survive configuration changes, for that Store must be encapsulated in a [`ViewModel`](https://developer.android.com/topic/libraries/architecture/viewmodel). So, created an abstraction to serve such purpose:
```kotlin
abstract class MVIViewModel<S: State, A: Action>: ViewModel() {

	protected abstract val store : Store<S, A> 

	val viewState : StateFlow<S>  
		get() = store.state  

	protected open fun dispatchActionToStore(
		action : A,
		dispatcher : CoroutineDispatcher = Dispatchers.Main
	) {
		viewModelScope.launch(dispatcher) {
			store.dispatch(action)
		}
	}

}
```
Not only this abstract `ViewModel` encapsulates the `Store`, it also executes dispatching the `Action` since dispatching `Action` from `Store`, by design is a `suspend`ing function to allow any blocking executions by `MiddleWare`.

**CAVEAT**:

> Since the `ViewModel` is capable of surviving configuration changes,
> any ongoing execution under `MiddleWare` would continue (since
> `viewModelScope` has not been out of scope) which would lead `Reducer`
> to continue to reduce any `Action` and change the `State`.

## MVI + Redux in Action
Here are the components we'll develop as an Example:
1. `ExampleActivity`: Hypothetical `ComponentActivity` that serves as our UI.
2. `ExampleViewModel`: An `MVIViewModel` that host `Store` of `ExampleState` and `ExampleAction`.
3. `ExampleState`:  `data class` implementing `State` that encapsulating the various information of UI.
4. `ExampleAction`: `sealed class` implementing `Action` encapsulating variety of actions `ExampleUI` and `MiddleWare` can trigger.
5. `ExampleReducer`: An implementation of `Reducer` that reduces any `ExampleAction` to a new `ExampleState`.
6. `ExampleMiddleWare`: An implementation of `MiddleWare` that handles the side-effects of `ExampleAction`.

We have `ExampleActivity` that renders `ExampleScreen`.
```kotlin
class ExampleActivity : ComponentActivity() {

	private var _viewModel: ExampleViewModel? = null

	private val viewModel: ExampleViewModel
		get() = _viewModel!!

	override fun onCreate(savedInstanceState: Bundle) {

		inject()
		super.onCreate(savedInstanceState)

		setContent {
			ExampleTheme {

				val state = viewModel.viewState.collectAsState()

				ExampleScreen(
					state.value,
					...
				)

			}
		}

	}

	override fun onDestroy() {
		cleanUp()
	}

	private fun inject() {
		_viewModel = // Provide the instance of ViewModel
	}

	private fun cleanUp() {
		_viewModel = null // Making it null so that it's instance is eligible for Garbage Collection.
	}

}

@Composable
fun ExampleScreen(
	state: ExampleState,
	...
) {
	// Renders some Composables.
	...
}
```

Hypothetically, `ExampleScreen` shows a number and a button. And the number is incremented every time a button is clicked. For that we can have `ExampleState` and `ExampleAction` implemented as below:
```kotlin
data class ExampleState(
	val number: Int = 0
) : State

sealed class ExampleAction : Action {
	object ButtonClicked : ExampleAction()
}
```

Now, we need a `Reducer` that takes any `ExampleAction` and changes current state to new `ExampleState`:
```kotlin
class ExampleReducer : Reducer<ExampleState, ExampleAction> {

	override fun reduce(currentState: ExampleState, action: ExampleAction): ExampleState =
		when(action) {
			ExampleAction.ButtonClicked -> onButtonClicked(currentState)
		}

	private fun onButtonClicked(currentState: ExampleState): ExampleState =
		currentState.copy(
			number = currentState.number + 1
		)

}
```

For now, there's no side effect to handle, we can skip creating `ExampleMiddleWare`.

With all of the components ready, we can finally create the `ViewModel`:
```kotlin
class ExampleViewModel : MVIViewModel<ExampleState, ExampleAction> {

	override val store: Store<ExampleState, ExampleAction> =
		Store(
			initialState = ExampleState(),
			reducer = ExampleReducer()
		)

	fun onButtonClicked() {
		dispatchActionToStore(ExampleAction.ButtonClicked)
	}

}
```

So far, so good. However, we add another `Button` inside `ExampleScreen` whose purpose on click is to navigate to another hypothetical `AnotherActivity`. This means, adding the functionality to our implementation. Something like this:

```kotlin
sealed class ExampleAction: Action {
	...
	object NavigateToAnotherScreen : ExampleAction()
}

class ExampleViewModel : MVIViewModel<ExampleState, ExampleAction>() {

	...

	fun onNavigateToAnotherActivityClicked() {
		dispatchActionToStore(ExampleAction.NavigateToAnotherScreen)
	}

}
```

Again, so far so good, but then we hit a road-block:

```kotlin
class ExampleReducer: Reducer<ExampleState, ExampleAction> {

	override fun reduce(currentState: ExampleState, action: ExampleAction): ExampleState =
		when(action) {
			... -> ...
			ExampleAction.NavigateToAnotherScreen -> onNavigateToAnotherScreen(currentState)
		}

	private fun onNavigateToAnotherScreen(currentState: ExampleState) = ???

}
```
Since navigation is an aspect that does not change the state of UI itself. **How do we propagate the navigation event to UI**?

### Approach 1: Add navigation event as a State of the UI

To do this, we just add a flag in ExampleState as below that represents whether to navigate or not.

```kotlin
data class ExampleState: State {
	val number: Int = 0,
	val navigate: Boolean = false // This flag tells UI whether to navigate to AnotherActivity or not.
}
```

Next, in `ExampleReducer`, we change the `ExampleState` as below:

```kotlin
class ExampleReducer: Reducer<ExampleState, ExampleAction> {

	...

	private fun onNavigateToAnotherScreen(currentState: ExampleState) =
		currentState.copy(
			navigate = true
		)

}
```

Finally, `ExampleScreen` sees the flag `navigate` and if true, triggers navigation.

#### Advantages
- Navigate without creating extra component.

#### Disadvantages
- Adding extra auxiliary data which is not rendered into the UI.
- Because we are adding auxiliary data that does not render directly to UI, any change in those data will trigger change in State, which subsequently trigger recomposition of UI.

### Approach 2: Treat Navigation as a side-effect.

When we are navigating to another screen (translates to `Activity`), we are not changing the state of current screen. This can be treated as a side-effect. To incorporate side-effect, we include `MiddleWare` to our Store.

So, first we create `ExampleMiddleWare` as below:

```kotlin
class ExampleMiddleWare: MiddleWare<ExampleState, ExampleAction>(
	private val onNavigate: () -> Unit
) {

	override suspend fun(action: ExampleAction, currentState: ExampleState) {
		when(action) {
			ExampleAction.NavigateToAnotherScreen -> onNavigate.invoke()
			else -> Unit
		}
	}

}
```

As you can see, we added a Lambda Expression `navigate` as the constructor argument to this `MiddleWare`. With `navigate` we trigger whether to navigate to AnotherActivity based on comparing various `ExampleAction`.

Next, in our ExampleViewModel, we include `ExampleMiddleWare` to the store along with implementation of `navigate`.

```kotlin
class ExampleViewModel : MVIViewModel<ExampleState, ExampleAction>() {

	var onNavigate: (() -> Unit)? = null

	override val store: Store<ExampleState, ExampleAction> =
		Store(
			initialState = ExampleState(),
			reducer = ExampleReducer(),
			middleWares = listOf(
				ExampleMiddleWare {
					onNavigate?.invoke()
				}
			)
		)

}
```

Finally, on our `ExampleActivity`, we can have something like this:

```kotlin
class ExampleActivity : ComponentActivity() {

	...

	override fun onCreate(savedInstanceState: Bundle?) {

		...
		// Post inject

		viewModel.onNavigate = {
			// Perform Navigation to Another Activity
		}

		// Pre setContent {
		...

	}

	private fun cleanUp() {
		viewModel.onNavigate = null
		// Pre dealloc _viewModel
		...
	}

}
```

#### Advantages
- Handle events as side effects like navigation without retorting to including non-UI data as state.

#### Disadvantages
- This is not out of box implementation. Had to implement some things ourselves.

### Decision Time

Overall, Approach 2 offers better solution than Approach 1, since with Approach 2, State won't be changed for such event, keeping recomposition of UI at bay.

## My Observations

### Advantages of MVI + Redux:
- `Reducer` acts as the single source of truth on logic and state management.
- `MiddleWare` is decoupled from `Reducer` by design, so it can perform independently.
- **UI can be easily implemented** by observing the latest change in `State` propagated by `Reducer`.
- If there's no change in `State`, `StateFlow<State>` won't emit any new State to UI. Hence in this case UI wont recompose.
- `Reducer` and `MiddleWare` are unit testable.

### Disadvantages of MVI + Redux:
- Have to deal with creating many individual components for MVI + Redux.
- **Triggering any non-State event to UI** (like navigating to another Activity, showing Dialog for examples) with State alone is **futile without triggering recomposition** (as working just with State will force us to encapsulate those event details in State). You'll need a mechanism in MVI + Redux that does not trigger
- Since in UI, all we are doing is consuming State from MVIViewModel, **any change in State will trigger recomposition**, which with time can be bad for performance if UI is complex,.
