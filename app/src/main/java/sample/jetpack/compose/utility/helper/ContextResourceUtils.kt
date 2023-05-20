package sample.jetpack.compose.utility.helper

import android.content.Context

import androidx.core.content.ContextCompat

import java.io.InputStream

import javax.inject.Inject

class ContextResourceUtils @Inject constructor(private val context : Context) : ResourceUtils {

	override fun getString(resourceID : Int) : String =
		context.getString(resourceID)

	override fun getString(resourceID : Int, vararg arguments : Any?) : String =
		context.getString(resourceID, arguments)

	override fun getStringArray(resourceID : Int) : Array<String> =
		context.resources.getStringArray(resourceID)

	override fun getInteger(resourceID : Int) : Int =
		context.resources.getInteger(resourceID)

	override fun getColour(resourceID : Int) : Int =
		ContextCompat.getColor(context, resourceID)

	override fun getAsset(fileName : String) : InputStream =
		context.resources.assets.open(fileName)

}
