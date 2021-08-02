package com.duwna.biblo.ui.groups.add

import android.Manifest
import android.net.Uri
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.custom.MemberView
import com.duwna.biblo.utils.hideKeyBoard
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_add_group.*
import kotlinx.android.synthetic.main.fragment_add_group.container


class AddGroupFragment : BaseFragment<AddGroupViewModel>() {

    private val args: AddGroupFragmentArgs by navArgs()
    override val layout: Int = R.layout.fragment_add_group

    override val viewModel: AddGroupViewModel by viewModels {
        AddGroupViewModelFactory(args.groupItem)
    }

    private val groupAvatarPermissionResult = registerPermissionResult {
        groupAvatarPickResult.launch("image/*")
    }

    private val memberAvatarPermissionResult = registerPermissionResult {
        memberAvatarPickResult.launch("image/*")
    }

    private val groupAvatarPickResult = registerImagePickResult { uri ->
        viewModel.setGroupImageUri(uri)
    }

    private val memberAvatarPickResult = registerImagePickResult { uri ->
        viewModel.setMemberImageUri(uri)
    }

    override fun setupViews() {

        switch_currency.setOnCheckedChangeListener { _, isChecked ->
            spinner.isInvisible = isChecked
            til_title.isInvisible = !isChecked
        }

        btn_create_group.setOnClickListener {
            createGroup()
        }

        btn_handle_mode.setOnClickListener {
            viewModel.handleSearchMode()
        }

        iv_avatar.setOnClickListener {
            groupAvatarPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        iv_member_avatar.setOnClickListener {
            memberAvatarPermissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        iv_avatar.isAvatarMode = true
        iv_member_avatar.isAvatarMode = true

        btn_add_member.setOnClickListener {
            viewModel.insertMember(et_member_name.text.toString())
            if (!viewModel.currentState.isSearchMode) et_member_name.setText("")
            requireContext().hideKeyBoard(it)
        }

        //edit group mode
        args.groupItem?.let { groupItem ->
            root.toolbar.title = getString(R.string.label_edit_group)
            et_group_name.setText(groupItem.name)
            setupCurrency(groupItem.currency)
            btn_create_group.setText(R.string.btn_save)
        }
    }

    override fun bindState(state: IViewModelState) {
        state as AddGroupState

        setupSearchMode(state.isSearchMode)
        setupMembers(state.members)
        setupAvatars(state.groupAvatarUri, state.memberAvatarUri)

        container.isVisible = state.showViews

        state.onGroupAdded?.setListener {
            findNavController().popBackStack()
        }
    }

    private fun createGroup() {
        val currency = if (switch_currency.isChecked) et_group_currency.text.toString()
        else spinner.selectedItem as String

        val name = et_group_name.text.toString()
        if (!viewModel.validateInput(name, currency)) return

        viewModel.createGroup(name, currency)
    }

    private fun setupAvatars(groupAvatarUri: Uri?, memberAvatarUri: Uri?) {
        when {
            groupAvatarUri != null -> Glide.with(this)
                .load(groupAvatarUri).into(iv_avatar)

            args.groupItem?.avatarUrl != null -> Glide.with(this)
                .load(args.groupItem?.avatarUrl).into(iv_avatar)

            else -> iv_avatar.setImageResource(R.drawable.ic_baseline_supervised_user_circle_24)
        }

        if (memberAvatarUri != null) {
            Glide.with(this).load(memberAvatarUri).into(iv_member_avatar)
        } else {
            iv_member_avatar.setImageResource(R.drawable.ic_baseline_account_circle_24)
        }
    }

    private fun setupSearchMode(isSearchMode: Boolean) {
        if (isSearchMode) {
            btn_add_member.setText(R.string.btn_make_search)
            btn_handle_mode.setText(R.string.btn_cancel)
            iv_member_avatar.isVisible = false
            iv_add_member_avatar.isVisible = false
            til_member_name.setHint(R.string.label_email)
            et_member_name.inputType = EditorInfo.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        } else {
            btn_add_member.setText(R.string.btn_add)
            iv_add_member_avatar.isVisible = true
            et_member_name.inputType = EditorInfo.TYPE_TEXT_VARIATION_PERSON_NAME
            btn_handle_mode.setText(R.string.btn_search)
            iv_member_avatar.isVisible = true
            til_member_name.setHint(R.string.label_name)
        }
    }

    private fun setupMembers(members: List<AddMemberItem>) {
        flexbox_members.removeAllViews()
        members.forEachIndexed { index, member ->
            val memberView = MemberView(
                context = requireContext(),
                name = member.name,
                avatarUrl = member.avatarUrl,
                avatarUri = member.avatarUri,
                isClickable = true
            ).apply {
                setOnLongClickListener {
                    PopupMenu(context, this).apply {
                        menu.add(context.getString(R.string.label_delete))
                            .setOnMenuItemClickListener {
                                viewModel.removeMember(index)
                                true
                            }
                        show()
                    }
                    true
                }
            }
            flexbox_members.addView(memberView)
        }
    }

    private fun setupCurrency(currency: String) {
        // currency from spinner
        for (i in 0 until spinner.adapter.count) {
            if (spinner.adapter.getItem(i) == currency) {
                spinner.setSelection(i)
                switch_currency.isChecked = false
                return
            }
        }
        // own currency
        switch_currency.isChecked = true
        et_group_currency.setText(currency)
    }
}
