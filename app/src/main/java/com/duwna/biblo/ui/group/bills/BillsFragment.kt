package com.duwna.biblo.ui.group.bills

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import com.duwna.biblo.utils.format
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_bills.*


class BillsFragment : BaseFragment<BillsViewModel>() {

    override val layout: Int = R.layout.fragment_bills

    private lateinit var groupItem: GroupItem

    override val viewModel: BillsViewModel by viewModels {
        BillsViewModelFactory(groupItem)
    }

    private val billsAdapter = BillsAdapter(
        onItemClicked = { billItem -> showDeleteBillSnackbar(billItem) }
    )

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

    override fun setupViews() {
        groupItem = arguments?.getSerializable("groupItem") as GroupItem

        rv_bills.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = billsAdapter
        }

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

        if (state.showNoBillsText) {
            tv_no_bills.isVisible = true
            tv_no_bills.animate().alpha(1f).duration = 500
        } else {
            tv_no_bills.isVisible = false
            tv_no_bills.alpha = 0f
        }

        billsAdapter.submitList(state.bills)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_bills, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_send_email -> {
                sendEmail()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("IntentReset")
    private fun sendEmail() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "", null
            )
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Biblo statistics")
        emailIntent.putExtra(Intent.EXTRA_TEXT, generateEmailMessage())
        startActivity(Intent.createChooser(emailIntent, getString(R.string.label_send_by_email)))
    }

    private fun generateEmailMessage() = buildString {
        viewModel.currentState.bills.forEach { billItem ->
            when (billItem) {
                is BillsViewItem.Header -> {
                    append(
                        "${getString(R.string.label_group)}: ${billItem.name}\n${getString(R.string.label_currency)}: ${billItem.currency}\n${
                            getString(
                                R.string.label_statistics
                            )
                        }\n${getString(R.string.label_who_payed)}:"
                    )
                    billItem.members.filter { it.sum > 0 }.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                    append("\n${getString(R.string.label_for_whom)}:")
                    billItem.members.filter { it.sum < 0 }.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                    append("\n\n${getString(R.string.label_all_bills)}")
                }
                is BillsViewItem.Bill -> {
                    append(
                        "\n\n${billItem.title}\n${billItem.description}\n${billItem.timestamp}\n${
                            getString(
                                R.string.label_who_payed
                            )
                        }:"
                    )
                    billItem.payers.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                    append("\n${getString(R.string.label_for_whom)}")
                    billItem.debtors.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                }
            }
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

