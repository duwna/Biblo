package com.duwna.biblo.ui.groups

import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.biblo.R
import com.duwna.biblo.base.BaseFragment
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.utils.circularHide
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : BaseFragment<GroupsViewModel>() {

    override val viewModel: GroupsViewModel by viewModels()
    override val layout: Int = R.layout.fragment_groups

    private val groupsAdapter = GroupsAdapter(
        onItemClicked = {

        }
    )

    override fun setupViews() {

        rv_groups.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupsAdapter
        }

        fab.setOnClickListener {
//            viewModel.loadGroups()
//            if (!isOpen) wave_view.circularShow() else wave_view.circularHide()
            findNavController().navigate(R.id.action_groups_to_add_group)
        }
    }

    override fun bindState(state: IViewModelState) {
        state as GroupsViewModelState

        when {
            state.isLoading -> wave_view.isVisible = true
            wave_view.isVisible && ViewCompat.isAttachedToWindow(wave_view) -> wave_view.circularHide()
            else -> wave_view.isVisible = false
        }

//        findNavController().popBackStack()
        if (!state.isAuth) findNavController().navigate(R.id.action_groups_to_auth)

        groupsAdapter.submitList(state.groups)
    }
}