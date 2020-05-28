package com.duwna.biblo.ui.groups.add

import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.biblo.R
import com.duwna.biblo.base.BaseFragment
import com.duwna.biblo.base.IViewModelState
import kotlinx.android.synthetic.main.fragment_add_group.*


class AddGroupFragment : BaseFragment<AddGroupViewModel>() {

    override val viewModel: AddGroupViewModel by viewModels()
    override val layout: Int = R.layout.fragment_add_group

    private val addMemberAdapter = AddMemberAdapter(
        onRemoveClicked = { position -> viewModel.removeMember(position) }
    )

    override fun setupViews() {

        rv_members.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addMemberAdapter
            isScrollContainer = false
            isNestedScrollingEnabled = false
        }

        iv_add_member.setOnClickListener {
            viewModel.insertMember(et_member_name.text.toString())
        }

        switch_currency.setOnClickListener {
            spinner.isInvisible = switch_currency.isChecked
            til_name.isInvisible = !switch_currency.isChecked
        }

        btn_create.setOnClickListener {
//            log(ll_members.getMemberNamesList())
        }

    }

    override fun bindState(state: IViewModelState) {
        state as AddGroupState

        addMemberAdapter.submitList(state.members)
    }

}
