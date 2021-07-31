package com.duwna.biblo.ui.base

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.duwna.biblo.MainActivity
import com.duwna.biblo.R

abstract class BaseFragment<T : BaseViewModel<out IViewModelState>> : Fragment() {

    val root: MainActivity
        get() = activity as MainActivity

    protected abstract val viewModel: T
    protected abstract val layout: Int

    abstract fun setupViews()
    abstract fun bindState(state: IViewModelState)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        setupViews()
        viewModel.observeState(viewLifecycleOwner) { state -> bindState(state) }
        viewModel.observeNotifications(viewLifecycleOwner) { root.renderNotification(it) }
        viewModel.observeLoading(viewLifecycleOwner) { root.renderLoading(it)}
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    protected fun registerImagePickResult(onPicked: (Uri) -> Unit): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri ?: return@registerForActivityResult
            onPicked(uri)
        }
    }

    protected fun registerPermissionResult(onGranted: () -> Unit): ActivityResultLauncher<String> {
        return registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            when {
                isGranted -> onGranted()
                !shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    openSettings()
                }
            }
        }
    }

    private fun openSettings() {
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
}