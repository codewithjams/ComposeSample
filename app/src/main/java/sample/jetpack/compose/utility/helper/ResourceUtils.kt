package sample.jetpack.compose.utility.helper

import androidx.annotation.ArrayRes
import androidx.annotation.ColorRes
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes

import java.io.IOException
import java.io.InputStream

interface ResourceUtils {

	fun getString(@StringRes resourceID : Int) : String

	fun getString(@StringRes resourceID : Int, vararg arguments : Any?) : String

	fun getStringArray(@ArrayRes resourceID : Int): Array<String>

	fun getInteger(@IntegerRes resourceID : Int) : Int

	fun getColour(@ColorRes resourceID : Int) : Int

	@Throws(IOException::class)
	fun getAsset(fileName : String) : InputStream?

}
