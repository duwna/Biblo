package com.duwna.biblo.ui.group.bills

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.circularHide
import kotlinx.android.synthetic.main.fragment_bills.*


class BillsFragment : BaseFragment<BillsViewModel>() {

    override val layout: Int = R.layout.fragment_bills

    private lateinit var groupItem: GroupItem

    override val viewModel: BillsViewModel by viewModels {
        BillsViewModelFactory(groupItem)
    }

    private val billsAdapter = BillsAdapter(
        onItemClicked = { billItem -> viewModel.deleteBill(billItem.id)}
    )

    override fun onResume() {
        super.onResume()
        viewModel.loadBills()
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

        when {
            state.isLoading -> wave_view.isVisible = true
            wave_view.isVisible && ViewCompat.isAttachedToWindow(wave_view) -> wave_view.circularHide()
            else -> wave_view.isVisible = false
        }

        if (!state.isLoading && state.bills.isEmpty()) {
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
        emailIntent.putExtra(Intent.EXTRA_TEXT, viewModel.generateEmailMessage())
        startActivity(Intent.createChooser(emailIntent, "Отправка по электронной почте"))
    }

    companion object {
        fun newInstance(groupItem: GroupItem) = BillsFragment().apply {
            arguments = bundleOf(
                "groupItem" to groupItem
            )
        }
    }
}

