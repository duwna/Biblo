package com.duwna.biblo.ui.groups.add

import android.app.Activity
import android.content.Intent
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.PICK_IMAGE_CODE
import com.duwna.biblo.utils.pickImageFromGallery
import com.duwna.biblo.utils.toInitials
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_group.*


class AddGroupFragment : BaseFragment<AddGroupViewModel>() {

    override val viewModel: AddGroupViewModel by viewModels()
    override val layout: Int = R.layout.fragment_add_group
    private val args: AddGroupFragmentArgs by navArgs()


    override fun setupViews() {

        switch_currency.setOnCheckedChangeListener { _, isChecked ->
            spinner.isInvisible = isChecked
            til_title.isInvisible = !isChecked
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

        //edit group mode
        args.groupItem?.let { groupItem ->
            root.toolbar.title = getString(R.string.label_edit_group)
            et_group_name.setText(groupItem.name)
            setupCurrency(groupItem)
            btn_delete_group.isVisible = true
            btn_delete_group.setOnClickListener {
                Snackbar.make(
                    root.container,
                    getString(R.string.label_delete_group),
                    Snackbar.LENGTH_SHORT
                ).setAction(getString(R.string.label_delete)) {
                        viewModel.deleteGroup(groupItem.id)
                    }.show()
            }
        }
    }


    override fun bindState(state: IViewModelState) {
        state as AddGroupState

        when {
            state.tmpAvatarUri != null -> {
                iv_avatar.isAvatarMode = true
                Glide.with(this).load(state.tmpAvatarUri).into(iv_avatar)
            }
            args.groupItem?.avatarUrl != null -> {
                iv_avatar.isAvatarMode = true
                Glide.with(this).load(args.groupItem?.avatarUrl).into(iv_avatar)
            }
            else -> {
                iv_avatar.isAvatarMode = false
            }
        }

        state.deleted?.let { findNavController().popBackStack() }
    }

    private fun navigateToMembersScreen() {

        val currency = if (switch_currency.isChecked) et_group_currency.text.toString()
        else spinner.selectedItem as String

        val name = et_group_name.text.toString()

        if (!viewModel.validateInput(name, currency)) return

        val action = AddGroupFragmentDirections.actionAddGroupToAddMembers(
            name,
            viewModel.currentState.tmpAvatarUri,
            currency,
            args.groupItem
        )

        findNavController().navigate(action)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            viewModel.setImageUri(data?.data)
        }
    }

    private fun setupCurrency(groupItem: GroupItem) {
        var found = false
        for (i in 0 until spinner.adapter.count) {
            if (spinner.adapter.getItem(i) == groupItem.currency) {
                spinner.setSelection(i)
                found = true
                break
            }
        }
        if (!found) {
            switch_currency.isChecked = true
            et_group_currency.setText(groupItem.currency)
        }
    }
}
