package com.duwna.biblo.ui.groups.add.members

import android.app.Activity
import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.base.BaseFragment
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.utils.*
import kotlinx.android.synthetic.main.fragment_add_members.*


class AddMembersFragment : BaseFragment<AddMembersViewModel>() {

    override val layout: Int = R.layout.fragment_add_members

    private val args: AddMembersFragmentArgs by navArgs()
    override val viewModel: AddMembersViewModel by viewModels()

    private val addMemberAdapter = AddMemberAdapter(
        onRemoveClicked = { position -> viewModel.removeMember(position) }
    )

    override fun setupViews() {

        rv_members.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addMemberAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        btn_add_member.setOnClickListener {
            viewModel.insertMember(et_member_name.text.toString())
            et_member_name.setText("")
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
    }

    override fun bindState(state: IViewModelState) {
        state as AddMembersState

        when {
            !state.isLoading -> wave_view.isVisible = false
            !wave_view.isVisible && ViewCompat.isAttachedToWindow(wave_view) -> wave_view.circularShow()
            else -> wave_view.isVisible = true
        }

        showViews(state.isLoading)

        if (state.memberAvatarUri != null) {
            iv_avatar.isAvatarMode = true
            Glide.with(this).load(state.memberAvatarUri).into(iv_avatar)
        } else {
            iv_avatar.isAvatarMode = false
        }

        addMemberAdapter.submitList(state.members)

        state.ready?.run { findNavController().popBackStack(R.id.navigation_groups, true) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            viewModel.setImageUri(data?.data)
        }
    }

    private fun showViews(isLoading: Boolean) {
        rv_members.isVisible = !isLoading
        iv_picture.isVisible = !isLoading
        btn_add_member.isVisible = !isLoading
        btn_create_group.isVisible = !isLoading
        btn_search_member.isVisible = !isLoading
        til_member_name.isVisible = !isLoading
        iv_avatar.isVisible = !isLoading
    }

}