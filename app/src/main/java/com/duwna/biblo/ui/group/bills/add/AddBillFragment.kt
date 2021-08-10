package com.duwna.biblo.ui.group.bills.add

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.format
import com.robinhood.ticker.TickerUtils
import kotlinx.android.synthetic.main.fragment_add_bill.*
import kotlinx.android.synthetic.main.item_add_bill.view.*
import java.util.*

class AddBillFragment : BaseFragment<AddBillViewModel>() {

    override val layout: Int = R.layout.fragment_add_bill

    override val viewModel: AddBillViewModel by viewModels {
        AddBillViewModelFactory(arguments?.getSerializable("groupItem") as GroupItem)
    }

    private val payersAdapter: AddBillAdapter by lazy {
        AddBillAdapter(
            onCheckBoxClicked = { index -> viewModel.setPayerChecked(index) },
            onTextChanged = { index, value -> viewModel.setPayerSum(index, value) }
        )
    }

    private val debtorsAdapter: AddBillAdapter by lazy {
        AddBillAdapter(
            onCheckBoxClicked = { index -> viewModel.setDebtorChecked(index) },
            onTextChanged = { index, value -> viewModel.setDebtorSum(index, value) }
        )
    }


    override fun setupViews() {

        ticker_sum.setCharacterLists(TickerUtils.provideNumberList())

        rv_payers.apply {
            layoutManager = LinearLayoutManager(context)
            isScrollContainer = false
            isNestedScrollingEnabled = false
            adapter = payersAdapter
        }

        rv_debtors.apply {
            layoutManager = LinearLayoutManager(context)
            isScrollContainer = false
            isNestedScrollingEnabled = false
            adapter = debtorsAdapter
        }

        btn_split.setOnClickListener {
            viewModel.splitSum()
            showSplitSum()
        }

        btn_create_bill.setOnClickListener {
            viewModel.createBill(
                et_title.text.toString(),
                et_description.text.toString()
            )
        }

        tv_date.setOnClickListener {
            showDateDialog()
        }

        switch_date.setOnCheckedChangeListener { _, isChecked ->
            tv_date.isVisible = !isChecked
            if (!isChecked) viewModel.setDate(Date())
        }
    }


    override fun bindState(state: IViewModelState) {
        state as AddBillState

        container.isVisible = state.showViews

        ticker_sum.text = state.sum.format()
        payersAdapter.submitList(state.payerList)
        debtorsAdapter.submitList(state.debtorList)

        tv_date.text = state.date.format("dd.MM")

        state.onBillAdded?.setListener { findNavController().popBackStack() }
    }

    private fun showSplitSum() {
        viewModel.currentState.debtorList.forEachIndexed { index, addBillMemberItem ->
            val holder = rv_debtors.findViewHolderForAdapterPosition(index) as? AddBillViewHolder
            holder?.containerView?.et_sum?.setText(addBillMemberItem.sum.format())
        }
    }

    private fun showDateDialog() {
        val calendar = Calendar.getInstance()
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
            }
            viewModel.setDate(calendar.time)
        }
        DatePickerDialog(
            root,
            listener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    companion object {
        fun args(groupItem: GroupItem) = bundleOf("groupItem" to groupItem)
    }

}