package com.duwna.biblo.ui.auth.registration

import android.net.Uri
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.dialogs.ImageActionDialog
import com.duwna.biblo.ui.dialogs.ImageActionDialog.Companion.showImageActionDialog
import com.duwna.biblo.utils.hideKeyBoard
import com.duwna.biblo.utils.tryOrNull
import kotlinx.android.synthetic.main.fragment_registration.*

class RegistrationFragment : BaseFragment<RegistrationViewModel>() {
    override val viewModel: RegistrationViewModel by viewModels()
    override val layout: Int = R.layout.fragment_registration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ImageActionDialog.IMAGE_ACTIONS_KEY) { _, bundle ->
            val result = bundle[ImageActionDialog.SELECT_ACTION_KEY] as? String
            if (result == ImageActionDialog.DELETE_ACTION_KEY) viewModel.setImageUri(null)
            else viewModel.setImageUri(tryOrNull { Uri.parse(result) })
        }
    }

    override fun setupViews() {
        iv_avatar.isAvatarMode = true

        btn_registration.setOnClickListener {
            it.hideKeyBoard()
            viewModel.registerUser(
                et_name.text.toString(),
                et_email.text.toString(),
                et_sum.text.toString()
            )
        }

        iv_avatar.setOnClickListener {
            val hasAvatar = viewModel.currentState.avatarUri != null
            findNavController().showImageActionDialog(hasAvatar)
        }
    }

    override fun bindState(state: IViewModelState) {
        state as RegistrationState

        showViews(state.isLoading)

        if (state.avatarUri != null) Glide.with(this).load(state.avatarUri).into(iv_avatar)
        else iv_avatar.setImageResource(R.drawable.ic_baseline_account_circle_24)

        state.ready?.let { findNavController().navigate(R.id.action_registration_to_groups) }
    }

    private fun showViews(isLoading: Boolean) {
        container.isVisible = !isLoading
    }
}