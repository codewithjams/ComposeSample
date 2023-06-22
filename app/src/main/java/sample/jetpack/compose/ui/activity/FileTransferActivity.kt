package sample.jetpack.compose.ui.activity

import android.content.ContentResolver

import android.database.Cursor

import android.net.Uri

import android.os.Bundle

import android.provider.OpenableColumns

import android.webkit.MimeTypeMap

import androidx.activity.ComponentActivity

import androidx.activity.compose.setContent

import androidx.activity.result.ActivityResultLauncher

import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Surface

import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState

import androidx.compose.ui.Modifier

import androidx.lifecycle.ViewModelProvider

import dagger.android.AndroidInjection

import sample.jetpack.compose.mvi.factory.VMFactory

import sample.jetpack.compose.mvi.state.FileTransferState

import sample.jetpack.compose.mvi.viewModel.FileTransferViewModel

import sample.jetpack.compose.ui.screen.FileTransferScreen

import sample.jetpack.compose.ui.theme.ComposeSampleTheme

import sample.jetpack.compose.utility.helper.MIMETypesCollection

import java.io.File

import javax.inject.Inject

class FileTransferActivity : ComponentActivity() {

	@Inject
	lateinit var viewModelFactory : VMFactory

	private var _viewModel : FileTransferViewModel? = null

	private val viewModel : FileTransferViewModel
		get() = _viewModel!!

	private val selectFileLauncher : ActivityResultLauncher<Array<String>> =
		registerForActivityResult(ActivityResultContracts.OpenDocument()) {
			it?.let { uri ->
				onFileSelected(uri)
			}
		}

	override fun onCreate(savedInstanceState : Bundle?) {

		AndroidInjection.inject(this)

		_viewModel = ViewModelProvider(this, viewModelFactory)[FileTransferViewModel::class.java]

		super.onCreate(savedInstanceState)

		setContent {
			ComposeSampleTheme {

				val state : State<FileTransferState> = viewModel.viewState.collectAsState()

				Surface(
					modifier = Modifier.fillMaxSize()
				) {
					FileTransferScreen(
						state = state.value,
						onOptionDownloadClicked = viewModel::onOptionDownloadClicked,
						onOptionUploadClicked = viewModel::onOptionUploadClicked,
						onFileNameChanged = viewModel::onFileNameChanged,
						onURLChanged = viewModel::onURLChanged,
						onDownloadClicked = viewModel::onDownloadClicked,
						onUploadClicked = this::onUploadClicked,
						onPauseClicked = viewModel::onDownloadPauseClicked,
						onResumeClicked = viewModel::onDownloadResumeClicked
					)
				}

			}
		}

	}

	override fun onDestroy() {
		super.onDestroy()
		_viewModel = null
	}

	private fun onUploadClicked() {
		selectFileLauncher.launch(MIMETypesCollection.IMAGES)
	}

	private fun onFileSelected(uri : Uri) {

		val fileName : String = uri.getFileName() ?: return

		val path = "/storage/emulated/0/Download/$fileName"

		val file = File(path)

		if (!file.exists())
			return

		val mimeType : String = uri.getMIMEType() ?: return

		viewModel.onFileSelectedByUser(file, mimeType)

	}

	private fun Uri.getFileName(includeExtension : Boolean = true) : String? {

		val cursor : Cursor = contentResolver.query(
			this,
			null,
			null,
			null,
			null
		) ?: return null

		val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

		if (index < 0)
			return null

		cursor.moveToFirst()

		val fileName = cursor.getString(index)

		cursor.close()

		if (!includeExtension)
			return fileName.substringBeforeLast(".")

		return fileName

	}

	private fun Uri.getMIMEType() : String? = when (scheme) {
		ContentResolver.SCHEME_CONTENT -> contentResolver.getType(this)
		ContentResolver.SCHEME_FILE    -> {
			val fileExtension =
				MimeTypeMap.getFileExtensionFromUrl(toString()).toString().lowercase()
			MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
		}

		else                           -> null
	}

}
