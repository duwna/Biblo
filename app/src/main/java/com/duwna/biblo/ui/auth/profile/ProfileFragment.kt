package com.duwna.biblo.ui.auth.profile

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.repositories.PrefManager
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.dialogs.ImageActionDialog
import com.duwna.biblo.ui.dialogs.ImageActionDialog.Companion.showImageActionDialog
import com.duwna.biblo.utils.toInitials
import com.duwna.biblo.utils.tryOrNull
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.coroutines.launch

class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val layout: Int = R.layout.fragment_profile

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ImageActionDialog.IMAGE_ACTIONS_KEY) { _, bundle ->
            val result = bundle[ImageActionDialog.SELECT_ACTION_KEY] as? String
            if (result == ImageActionDialog.DELETE_ACTION_KEY) viewModel.deleteAvatar()
            else viewModel.setImageUri(tryOrNull { Uri.parse(result) })
        }
    }

    override fun setupViews() {

        setupThemeViews()

        et_name.doOnTextChanged { text, _, _, _ ->
            iv_avatar.setInitials(text.toString().toInitials())
        }

        iv_avatar.setOnClickListener {
            val hasImage =
                viewModel.currentState.user?.avatarUrl != null || viewModel.currentState.tmpAvatarUri != null
            findNavController().showImageActionDialog(hasImage)
        }

        btn_save.setOnClickListener {
            viewModel.saveUser(et_name.text.toString())
        }

        btn_sign_out.setOnClickListener {
            viewModel.signOut()
            findNavController().popBackStack()
        }
    }

    override fun bindState(state: IViewModelState) {
        state as ProfileState

        et_name.setText(state.user?.name)
        et_email.setText(state.user?.email)
        til_email.isVisible = state.user?.email != null


        when {
            state.tmpAvatarUri != null -> {
                iv_avatar.isAvatarMode = true
                Glide.with(this).load(state.tmpAvatarUri).into(iv_avatar)
            }
            state.user?.avatarUrl != null -> {
                iv_avatar.isAvatarMode = true
                Glide.with(this).load(state.user.avatarUrl).into(iv_avatar)
            }
            else -> {
                iv_avatar.isAvatarMode = false
                state.user?.name?.toInitials()?.let { iv_avatar.setInitials(it) }
            }
        }
    }

    private fun setupThemeViews() {
        setCheckedThemeMode(AppCompatDelegate.getDefaultNightMode())

        btn_system.setOnClickListener {
            applyTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
        btn_light.setOnClickListener {
            applyTheme(AppCompatDelegate.MODE_NIGHT_NO)
        }
        btn_dark.setOnClickListener {
            applyTheme(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }

    private fun applyTheme(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        lifecycleScope.launch { PrefManager.saveThemeMode(mode) }
        setCheckedThemeMode(mode)
    }

    private fun setCheckedThemeMode(mode: Int) {
        when (mode) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                iv_system_checked.isVisible = false
                iv_light_checked.isVisible = true
                iv_dark_checked.isVisible = false
            }
            AppCompatDelegate.MODE_NIGHT_YES -> {
                iv_system_checked.isVisible = false
                iv_light_checked.isVisible = false
                iv_dark_checked.isVisible = true
            }
            else -> {
                iv_system_checked.isVisible = true
                iv_light_checked.isVisible = false
                iv_dark_checked.isVisible = false
            }
        }
    }

}
