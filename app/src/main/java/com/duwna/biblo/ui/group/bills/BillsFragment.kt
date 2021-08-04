package com.duwna.biblo.ui.group.bills

import android.app.AlertDialog
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.BillsViewItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_bills.*


class BillsFragment : BaseFragment<BillsViewModel>() {

    override val layout: Int = R.layout.fragment_bills
    private lateinit var groupItem: GroupItem

    override val viewModel: BillsViewModel by viewModels {
        groupItem = arguments?.getSerializable("groupItem") as GroupItem
        BillsViewModelFactory(groupItem)
    }

    private val billsAdapter: BillsAdapter by lazy {
        BillsAdapter(onItemClicked = { billItem -> showDeleteBillSnackbar(billItem) })
    }

    override fun setupViews() {
        rv_bills.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = billsAdapter
        }

        fab.setOnClickListener {
            navigateToAddBillScreen()
        }
    }

    override fun bindState(state: IViewModelState) {
        state as BillsState

        if (state.showNoBillsText) {
            tv_no_bills.isVisible = true
            tv_no_bills.animate().alpha(1f).duration = 500
        } else {
            tv_no_bills.isVisible = false
            tv_no_bills.alpha = 0f
        }

        state.onGroupDeleted?.setListener { findNavController().popBackStack() }

        billsAdapter.submitList(state.bills)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bills, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_send_statistics -> {
                sendStatistics()
                true
            }
            R.id.action_edit_group -> {
                navigateToEditGroupScreen()
                true
            }
            R.id.action_delete_group -> {
                showDeleteGroupDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDeleteBillSnackbar(billItem: BillsViewItem.Bill) {
        Snackbar.make(
            requireView(),
            "${requireContext().getString(R.string.label_delete_bill)} \"${billItem.title}\"?",
            Snackbar.LENGTH_SHORT
        ).apply {
            setAction(requireContext().getString(R.string.label_delete)) {
                viewModel.deleteBill(billItem.id)
            }
            show()
        }
    }

    private fun showDeleteGroupDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.label_delete_group)
            .setMessage(R.string.message_delete_group)
            .setPositiveButton(R.string.label_delete) { _, _ -> viewModel.deleteGroup() }
            .setNegativeButton(R.string.btn_cancel) { _, _ -> }
            .show()
    }

    private fun navigateToEditGroupScreen() {
        findNavController().navigate(
            R.id.navigation_add_group,
            bundleOf("group_item" to groupItem),
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

    private fun navigateToAddBillScreen() {
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

    private fun sendStatistics() {
        val message = viewModel.generateStatisticsMessage(
            labelGroup = getString(R.string.label_group),
            labelCurrency = getString(R.string.label_currency),
            labelStatistics = getString(R.string.label_statistics),
            labelWhoPayed = getString(R.string.label_who_payed),
            labelForWhom = getString(R.string.label_for_whom),
            labelAllBills = getString(R.string.label_all_bills),
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_statistics_subject))
            putExtra(Intent.EXTRA_TEXT, message)
        }
        startActivity(Intent.createChooser(intent, getString(R.string.label_send_by_email)))
    }

    companion object {
        fun newInstance(groupItem: GroupItem) = BillsFragment().apply {
            arguments = bundleOf(
                "groupItem" to groupItem
            )
        }
    }
}

