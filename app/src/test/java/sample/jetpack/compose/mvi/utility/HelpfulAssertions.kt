package sample.jetpack.compose.mvi.utility

import com.google.common.truth.Truth

fun Boolean.assert(expected : Boolean) {
	Truth.assertThat(this).apply {
		if (expected) {
			isTrue()
		} else {
			isFalse()
		}
	}
}
