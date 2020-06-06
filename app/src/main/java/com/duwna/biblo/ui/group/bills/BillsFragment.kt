package com.duwna.biblo.ui.group.bills

import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.circularHide
import kotlinx.android.synthetic.main.fragment_bills.*

class BillsFragment : BaseFragment<BillsViewModel>() {

    private lateinit var groupItem: GroupItem

    override val viewModel: BillsViewModel by viewModels()
    override val layout: Int = R.layout.fragment_bills

    override fun setupViews() {
        groupItem = arguments?.getSerializable("groupItem") as GroupItem


        fab.setOnClickListener {
            findNavController().navigate(
                R.id.navigation_add_bill,
                bundleOf("groupItem" to groupItem),
                navOptions {
                    anim {
                        enter = R.anim.slide_from_right_to_center
                        exit = R.anim.slide_from_center_to_left
                        popEnter = R.anim.slide_from_left_to_center
                        popExit = R.anim.slide_from_center_to_right
                    }
                }
            )
        }
    }

    override fun bindState(state: IViewModelState) {
        state as BillsState

        when {
            state.isLoading -> wave_view.isVisible = true
            wave_view.isVisible && ViewCompat.isAttachedToWindow(wave_view) -> wave_view.circularHide()
            else -> wave_view.isVisible = false
        }

        if (!state.isLoading && state.bills.isEmpty()) {
            tv_no_bills.animate().alpha(1f).duration = 500
        } else {
            tv_no_bills.alpha = 0f
        }
    }

    companion object {
        fun newInstance(groupItem: GroupItem) = BillsFragment().apply {
            arguments = bundleOf(
                "groupItem" to groupItem
            )
        }
    }
}

