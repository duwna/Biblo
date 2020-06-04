package com.duwna.biblo.ui.groups.add

import android.app.Activity
import android.content.Intent
import androidx.core.view.isInvisible
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
import kotlinx.android.synthetic.main.fragment_add_group.*


class AddGroupFragment : BaseFragment<AddGroupViewModel>() {

    override val viewModel: AddGroupViewModel by viewModels()
    override val layout: Int = R.layout.fragment_add_group


    override fun setupViews() {

        switch_currency.setOnClickListener {
            spinner.isInvisible = switch_currency.isChecked
            til_name.isInvisible = !switch_currency.isChecked
        }

        btn_add_members.setOnClickListener {
            navigateToMembersScreen()
        }

        iv_avatar.setOnClickListener {
            pickImageFromGallery()
        }

        et_group_name.doOnTextChanged { text, _, _, _ ->
            iv_avatar.setInitials(text.toString().toInitials())
        }
    }


    override fun bindState(state: IViewModelState) {
        state as AddGroupState

        if (state.memberAvatarUri != null) {
            iv_avatar.isAvatarMode = true
            Glide.with(this).load(state.memberAvatarUri).into(iv_avatar)
        } else {
            iv_avatar.isAvatarMode = false
        }
    }

    private fun navigateToMembersScreen() {

        val currency = if (switch_currency.isChecked) et_group_currency.text.toString()
        else spinner.selectedItem as String

        val name = et_group_name.text.toString()

        if (!viewModel.validateInput(name, currency)) return

        val action = AddGroupFragmentDirections.actionAddGroupToAddMembers(
            name,
            viewModel.currentState.memberAvatarUri,
            currency
        )

        findNavController().navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            viewModel.setImageUri(data?.data)
        }
    }
}
