package com.duwna.biblo.ui.groups

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.circularHide
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : BaseFragment<GroupsViewModel>() {

    override val viewModel: GroupsViewModel by viewModels()
    override val layout: Int = R.layout.fragment_groups

    override fun onResume() {
        super.onResume()
        viewModel.initialize()
    }

    private val groupsAdapter = GroupsAdapter(
        onItemClicked = { groupItem ->
            val action = GroupsFragmentDirections.actionGroupsToGroup(groupItem)
            findNavController().navigate(action)
        },
        onItemLongClicked = { groupItem ->
            val action = GroupsFragmentDirections.actionGroupsToAddGroup(groupItem)
            findNavController().navigate(action)
        }
    )

    override fun setupViews() {

        rv_groups.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = groupsAdapter
        }

        fab.setOnClickListener {
            val action = GroupsFragmentDirections.actionGroupsToAddGroup(null)
            findNavController().navigate(action)
        }
    }

    override fun bindState(state: IViewModelState) {
        state as GroupsViewModelState

        if (!state.isAuth) findNavController().navigate(R.id.action_groups_to_auth)

        if (state.isLoading) biblo_loading_view.show()
        else biblo_loading_view.hide()


        if (!state.isLoading && state.groups.isEmpty()) {
            tv_no_groups.isVisible = true
            tv_no_groups.alpha = 0f
            tv_no_groups.animate().alpha(1f).duration = 500
        } else {
            tv_no_groups.isVisible = false
        }

        groupsAdapter.submitList(state.groups)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_groups, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_profile -> {
                findNavController().navigate(R.id.action_groups_to_profile)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}