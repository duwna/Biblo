package com.duwna.biblo.ui.group.bills

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.entities.database.Bill
import com.duwna.biblo.entities.items.BillMemberItem
import com.duwna.biblo.entities.items.BillsViewItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.repositories.BillsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.utils.format
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class BillsViewModel(private val groupItem: GroupItem) : BaseViewModel<BillsState>(BillsState()) {

    private val repository = BillsRepository(groupItem.id)

    init {
        doAsync { subscribeOnBillsList() }
    }

    private suspend fun subscribeOnBillsList() {
        repository.subscribeOnBills().collect { bills ->
            val billsViewItems = bills.toBillViewItemList()
            postUpdateState { copy(bills = billsViewItems, isLoading = false) }
        }
    }

    fun loadBills() {
//        viewModelScope.launch(IO) {
//            try {
//                var billsViewItems = emptyList<BillsViewItem>()
//                //delay to avoid interrupting animation between fragments
//                val mills = measureTimeMillis {
//                    val bills = repository.loadBills()
//                    if (bills.isNotEmpty()) billsViewItems = bills.toBillViewItemList()
//                }
//                // 300 - animation length mills
//                val delay = 300L - mills
//                if (delay > 0) delay(delay)
//                postUpdateState { copy(bills = billsViewItems, isLoading = false) }
//            } catch (t: Throwable) {
//                postUpdateState { copy(isLoading = false) }
//                t.printStackTrace()
//                notify(Notify.DataError)
//            }
//        }
    }

    private fun List<Bill>.toBillViewItemList(): List<BillsViewItem> {
        //bills + header
        val billItems: MutableList<BillsViewItem> = ArrayList(this.size + 1)
        //map to count statistics
        val membersMap = mutableMapOf<String, Double>().apply {
            groupItem.members.forEach { put(it.id, 0.0) }
        }
        this.forEach { bill ->
            billItems.add(bill.toBillItem())
            //count statistics
            bill.payers.forEach { (id, sum) ->
                membersMap[id] = (membersMap[id] ?: 0.0) + sum
            }
            bill.debtors.forEach { (id, sum) ->
                membersMap[id] = (membersMap[id] ?: 0.0) - sum
            }
        }
        val headerMembers = groupItem.members.map {
            BillMemberItem(it.id, it.name, it.avatarUrl, membersMap[it.id] ?: 0.0)
        }
        billItems.add(
            0,
            BillsViewItem.Header(
                groupItem.name,
                groupItem.currency,
                groupItem.avatarUrl,
                headerMembers
            )
        )
        return billItems
    }

    private fun Bill.toBillItem() = BillsViewItem.Bill(
        idBill,
        title,
        description,
        timestamp.format("HH:mm\ndd.MM"),
        payers.map {
            val member = groupItem.members.find { member -> it.key == member.id }!!
            BillMemberItem(it.key, member.name, member.avatarUrl, it.value)
        },
        debtors.map {
            val member = groupItem.members.find { member -> it.key == member.id }!!
            BillMemberItem(it.key, member.name, member.avatarUrl, it.value)
        }
    )

    fun deleteBill(idBill: String) {
        viewModelScope.launch(IO) {
            try {
                repository.deleteBill(idBill)
                loadBills()
            } catch (t: Throwable) {
                notify(Notify.DataError)
            }
        }
    }
}


data class BillsState(
    val isLoading: Boolean = false,
    val bills: List<BillsViewItem> = emptyList()
) : IViewModelState

class BillsViewModelFactory(private val groupItem: GroupItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return BillsViewModel(groupItem) as T
    }
}