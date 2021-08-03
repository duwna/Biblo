package com.duwna.biblo.ui.dialogs

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager.FEATURE_CAMERA_ANY
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.fragment.navArgs
import com.duwna.biblo.R
import com.duwna.biblo.utils.tryOrNull
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_image_action.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ImageActionDialog : BottomSheetDialogFragment() {

    private val args: ImageActionDialogArgs by navArgs()
    private var takePictureTmpUri: Uri? = null
    private var imageAction: String? = null

    private val permissionResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> {
                    when (imageAction) {
                        CAMERA_ACTION_KEY -> {
                            takePictureTmpUri = prepareTempUri()
                            cameraResult.launch(takePictureTmpUri)
                        }
                        GALLERY_ACTION_KEY -> imagePickResult.launch("image/*")
                        null -> dismiss()
                    }
                }
                !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showOpenSettingsDialog()
                }
            }
        }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                setFragmentResult(
                    IMAGE_ACTIONS_KEY,
                    bundleOf(SELECT_ACTION_KEY to takePictureTmpUri.toString())
                )
            } else {
                removeTempUri(takePictureTmpUri)
            }
            dismiss()
        }

    private val imagePickResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                setFragmentResult(IMAGE_ACTIONS_KEY, bundleOf(SELECT_ACTION_KEY to uri.toString()))
            }
            dismiss()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_image_action, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val hasCamera = requireContext().packageManager.hasSystemFeature(FEATURE_CAMERA_ANY)
        item_camera.isVisible = hasCamera

        item_camera.setOnClickListener {
            imageAction = CAMERA_ACTION_KEY
            launchAction()
        }

        item_gallery.setOnClickListener {
            imageAction = GALLERY_ACTION_KEY
            launchAction()
        }

        item_delete.isVisible = args.hasImage
        item_delete.setOnClickListener {
            setFragmentResult(IMAGE_ACTIONS_KEY, bundleOf(SELECT_ACTION_KEY to DELETE_ACTION_KEY))
            dismiss()
        }
    }

    private fun prepareTempUri(): Uri {
        val timestamp = SimpleDateFormat("HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val tempFile = File.createTempFile(
            "JPEG_$timestamp",
            ".jpg",
            storageDir
        )

        return FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.provider",
            tempFile
        )
    }

    private fun showOpenSettingsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.label_permission_required))
            .setMessage(getString(R.string.message_need_open_settings))
            .setPositiveButton(getString(R.string.label_open_settings)) { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:com.duwna.biblo")
                }
                startActivity(intent)
            }
            .show()
    }

    private fun removeTempUri(uri: Uri?) {
        uri ?: return
        requireContext().contentResolver.delete(uri, null, null)
    }

    private fun launchAction() {
        permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("takePictureTmpUri", takePictureTmpUri.toString())
        outState.putString("imageAction", imageAction)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        takePictureTmpUri = (savedInstanceState?.get("takePictureTmpUri") as? String)?.let {
            tryOrNull { Uri.parse(it) }
        }
        imageAction = savedInstanceState?.get("imageAction") as? String
    }

    companion object {
        const val IMAGE_ACTIONS_KEY = "IMAGE_ACTIONS_KEY"
        const val SELECT_ACTION_KEY = "SELECT_ACTION_KEY"
        const val DELETE_ACTION_KEY = "DELETE_ACTION_KEY"
        const val CAMERA_ACTION_KEY = "CAMERA_ACTION_KEY"
        const val GALLERY_ACTION_KEY = "GALLERY_ACTION_KEY"

        fun NavController.showImageActionDialog(hasImage: Boolean) {
            navigate(
                R.id.navigation_image_action_dialog,
                bundleOf("has_image" to hasImage)
            )
        }
    }
}