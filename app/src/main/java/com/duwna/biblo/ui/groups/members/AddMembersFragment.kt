package com.duwna.biblo.ui.groups.members

import android.app.Activity
import android.content.Intent
import android.text.InputType
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.*
import kotlinx.android.synthetic.main.fragment_add_members.*


class AddMembersFragment : BaseFragment<AddMembersViewModel>() {

    override val layout: Int = R.layout.fragment_add_members

    private val args: AddMembersFragmentArgs by navArgs()
    override val viewModel: AddMembersViewModel by viewModels() {
        AddMembersViewModelFactory(args.groupItem)
    }

    private val addMemberAdapter = AddMemberAdapter(
        onRemoveClicked = { position -> viewModel.removeMember(position) }
    )

    override fun setupViews() {

        args.groupItem?.let { btn_create_group.text = context?.getString(R.string.btn_save) }

        rv_members.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addMemberAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        btn_add_member.setOnClickListener {
            viewModel.insertMember(et_member_name.text.toString())
            if (!viewModel.currentState.isSearch) et_member_name.setText("")
            root.hideKeyBoard(requireView())
        }

        et_member_name.doOnTextChanged { text, _, _, _ ->
            iv_avatar.setInitials(text.toString().toInitials())
        }

        btn_create_group.setOnClickListener {
            viewModel.createGroup(
                args.groupName,
                args.groupCurrency,
                args.groupAvatarUri
            )
        }

        iv_avatar.setOnClickListener {
            pickImageFromGallery()
        }

        btn_handle_mode.setOnClickListener {
            viewModel.handleSearchMode()
        }
    }

    override fun bindState(state: IViewModelState) {
        state as AddMembersState

        showSearch(state.isSearch)
        showViews(state.isLoading)
        showAvatar(!state.isSearch && !state.isLoading)

        if (state.memberAvatarUri != null) {
            iv_avatar.isAvatarMode = true
            Glide.with(this).load(state.memberAvatarUri).into(iv_avatar)
        } else {
            iv_avatar.isAvatarMode = false
        }
        addMemberAdapter.submitList(state.members)
        state.ready?.let { findNavController().navigate(R.id.action_add_members_to_groups) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            viewModel.setImageUri(data?.data)
        }
    }

    private fun showAvatar(show: Boolean) {
        iv_avatar.isVisible = show
        iv_picture.isVisible = show
    }

    private fun showViews(isLoading: Boolean) {
        rv_members.isVisible = !isLoading
        btn_add_member.isVisible = !isLoading
        btn_create_group.isVisible = !isLoading
        btn_handle_mode.isVisible = !isLoading
        til_member_name.isVisible = !isLoading
        iv_avatar.isVisible = !isLoading
    }

    private fun showSearch(isSearch: Boolean) {
        btn_add_member.text = requireContext().getString(
            if (isSearch) R.string.btn_make_search else R.string.btn_add
        )
        btn_handle_mode.text = requireContext().getString(
            if (isSearch) R.string.btn_cancel else R.string.btn_search
        )
        til_member_name.hint = requireContext().getString(
            if (isSearch) R.string.label_email else R.string.label_name
        )
        et_member_name.inputType = if (isSearch)
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        else InputType.TYPE_TEXT_VARIATION_PERSON_NAME
    }

}