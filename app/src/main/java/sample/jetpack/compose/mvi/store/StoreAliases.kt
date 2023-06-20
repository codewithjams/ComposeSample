package sample.jetpack.compose.mvi.store

import sample.jetpack.compose.mvi.action.FileTransferAction
import sample.jetpack.compose.mvi.action.LoginAction
import sample.jetpack.compose.mvi.action.MainAction

import sample.jetpack.compose.mvi.state.FileTransferState
import sample.jetpack.compose.mvi.state.LoginState
import sample.jetpack.compose.mvi.state.MainState

typealias MainStore = Store<MainState, MainAction>
typealias LoginStore = Store<LoginState, LoginAction>
typealias FileTransferStore = Store<FileTransferState, FileTransferAction>
