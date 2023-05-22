
# Chapter 2: Testing Tentacles

In this section, we'll cover both Unit and Instrumentation Testing. Both are divided into their separate chapters.

#### Sub-Chapters
1. [Unit Test](CHAPTER0201.md)
2. [Instrumentation Test](CHAPTER0202.md)

## Demonstration : Login screen

We develop a Login screen like below:

![Login Screen Demonstration](https://github.com/ritwikjamuar/ComposeSample/blob/main/documentation/assets/login_screen_demo.gif)

#### Components for Interaction:
- Field for User Name
- Field for Password
- Button that toggles Password visibility
- Button that initiates Login

#### Behaviour Explained:
- Initially, screen would not show any validation even though User Name and Password is empty.
- User Name:
	- When user name is empty after it is entered first time, an error validation for empty user name is shown.
	- When user reaches the limit of character, user will be shown a warning validation text indicating the same.
	- When user exceeds the length of user name, user will be shown an error validation text indicating by how much characters user name has exceeded.
- Password:
	- When password is empty after it is entered first time, an error validation for empty password is shown.
	- Password should not contain any illegal characters (like ?, ., %, ~). If user enter those character(s), an error validation indicating Illegal Character is shown.
- If there is any kind of validation being shown for any field, Login button is disabled.
- Login button will be enabled only if User Name and Password does not show any validation text.
- One exception to the preceding logic is that if User Name field shows the validation for Limit Reached, it is still valid, and hence Login button is enabled if Password is valid.
- When Login button is clicked, a Progress indicator is shown, and if the mocked operation of Login completes, then we hide the Progress Indicator.

#### Code Components:
- [FieldValidation.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/model/FieldValidation.kt): Contains `enum` that encapsulates all the validations of Login Screen.
 - [LoginState.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/state/LoginState.kt): `State` of Login Screen.
 - [LoginAction.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/action/LoginAction.kt): Encapsulates all the events from Login Screen as `Action`.
 - [SampleRepository.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/repository/SampleRepository.kt): A repository that emulates Login being performed.
 - [LoggingMiddleWare.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/middleware/LoggingMiddleWare.kt): A `MiddleWare` that handles performing REST Call to Login as a side-effect.
 - [LoginReducer.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/reducer/LoginReducer.kt): Takes any `LoginAction` and reduces current `LoginState` to a new one.
 - [LoginViewModel.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/mvi/viewModel/LoginViewModel.kt): Hosts the `Store<LoginState, LoginAction>` and receives the events from the Login Screen.
 - [LoginActivity.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/ui/activity/LoginActivity.kt): Hosts the `LoginScreen`, observe the new state emission from `LoginViewModel` and propagates new state to `LoginScreen`.
 - [LoginScreen.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/main/java/sample/jetpack/compose/ui/screen/LoginScreen.kt): Actual `@Composable` UI that shows User Name & Password field, Login button, Progress indicator and such.
 - [LoginReducerTest.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/test/java/sample/jetpack/compose/mvi/reducer/LoginReducerTest.kt): Unit Test class of `LoginReducer`.
 - [LoginMiddleWareTest.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/test/java/sample/jetpack/compose/mvi/middleWare/LoginMiddleWareTest.kt): Unit Test class of `LoginMiddleWare`.
 - [LoginScreenTest.kt](https://github.com/ritwikjamuar/ComposeSample/blob/main/app/src/androidTest/java/sample/jetpack/compose/ui/screen/LoginScreenTest.kt): Instrumentation Test class of `LoginScreen`.
