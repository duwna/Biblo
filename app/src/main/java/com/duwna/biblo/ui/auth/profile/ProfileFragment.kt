package com.duwna.biblo.ui.auth.profile

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.PICK_IMAGE_CODE
import com.duwna.biblo.utils.pickImageFromGallery
import com.duwna.biblo.utils.toInitials
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragment<ProfileViewModel>() {

    override val viewModel: ProfileViewModel by viewModels()
    override val layout: Int = R.layout.fragment_profile

    override fun setupViews() {

        et_name.doOnTextChanged { text, _, _, _ ->
            iv_avatar.setInitials(text.toString().toInitials())
        }

        iv_avatar.setOnClickListener {
            pickImageFromGallery()
        }

        btn_save.setOnClickListener {
            viewModel.saveUser(et_name.text.toString())
        }

        switch_theme.isChecked =
            AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

        switch_theme.setOnCheckedChangeListener { _, isChecked ->
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked)
                    AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
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
        wave_view.isVisible = state.isLoading

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            viewModel.setImageUri(data?.data)
        }
    }
}