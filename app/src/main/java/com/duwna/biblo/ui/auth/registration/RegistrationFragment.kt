package com.duwna.biblo.ui.auth.registration

import android.app.Activity
import android.content.Intent
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.base.BaseFragment
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.utils.PICK_IMAGE_CODE
import com.duwna.biblo.utils.hideKeyBoard
import com.duwna.biblo.utils.pickImageFromGallery
import com.duwna.biblo.utils.toInitials
import kotlinx.android.synthetic.main.fragmen_tregistration.*

class RegistrationFragment : BaseFragment<RegistrationViewModel>() {
    override val viewModel: RegistrationViewModel by viewModels()
    override val layout: Int = R.layout.fragmen_tregistration

    override fun setupViews() {
        btn_registration.setOnClickListener {
            root.hideKeyBoard(container)
            viewModel.registerUser(
                et_name.text.toString(),
                et_email.text.toString(),
                et_password.text.toString()
            )
        }

        iv_avatar.setOnClickListener {
            pickImageFromGallery()
        }

        et_name.doOnTextChanged { text, _, _, _ ->
            iv_avatar.setInitials(text.toString().toInitials())
        }
    }

    override fun bindState(state: IViewModelState) {
        state as RegistrationState

        showViews(state.isLoading)

        if (state.avatarUri != null) {
            iv_avatar.isAvatarMode = true
            Glide.with(this).load(state.avatarUri).into(iv_avatar)
        } else {
            iv_avatar.isAvatarMode = false
        }

        state.ready?.let { findNavController().navigate(R.id.action_registration_to_groups) }
    }

    private fun showViews(isLoading: Boolean) {
        container.isVisible = !isLoading
        wave_view.isVisible = isLoading
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            viewModel.setImageUri(data?.data)
        }
    }

}