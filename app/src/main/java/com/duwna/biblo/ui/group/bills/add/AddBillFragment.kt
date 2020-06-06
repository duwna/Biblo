package com.duwna.biblo.ui.group.bills.add

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
    private lateinit var groupItem: GroupItem

    override val viewModel: AddBillViewModel by viewModels {
        AddBillViewModelFactory(groupItem)
    }

    private val payersAdapter = AddBillAdapter(
        onCheckBoxClicked = { index -> viewModel.setPayerChecked(index) },
        onTextChanged = { index, value -> viewModel.setPayerSum(index, value) }
    )

    private val debtorsAdapter = AddBillAdapter(
        onCheckBoxClicked = { index -> viewModel.setDebtorChecked(index) },
        onTextChanged = { index, value -> viewModel.setDebtorSum(index, value) }
    )

    override fun setupViews() {
        groupItem = arguments?.getSerializable("groupItem") as GroupItem

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
                et_description.text.toString(),
                Date()
            )
        }
    }


    override fun bindState(state: IViewModelState) {
        state as AddBillState

        showViews(state.isLoading)

        ticker_sum.text = state.sum.format()
        payersAdapter.submitList(state.payerList)
        debtorsAdapter.submitList(state.debtorList)

        state.ready?.run { findNavController().popBackStack() }
    }

    private fun showSplitSum() {
        viewModel.currentState.debtorList.forEachIndexed { index, addBillMemberItem ->
            val holder = rv_debtors.findViewHolderForAdapterPosition(index) as? AddBillViewHolder
            holder?.containerView?.et_sum?.setText(addBillMemberItem.sum.format())
        }
    }

    private fun showViews(isLoading: Boolean) {
        container.isVisible = !isLoading
        wave_view.isVisible = isLoading
    }
}