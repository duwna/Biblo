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
            findNavController().navigate(R.id.action_groups_to_add_group)
        }
    }

    override fun bindState(state: IViewModelState) {
        state as GroupsViewModelState

        if (!state.isAuth) findNavController().navigate(R.id.action_groups_to_auth)

        when {
            state.isLoading -> wave_view.isVisible = true
            wave_view.isVisible && ViewCompat.isAttachedToWindow(wave_view) -> wave_view.circularHide()
            else -> wave_view.isVisible = false
        }

        tv_no_groups.isVisible = !state.isLoading && state.groups.isEmpty()

        groupsAdapter.submitList(state.groups)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                viewModel.signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


}