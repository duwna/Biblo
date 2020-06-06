package com.duwna.biblo.ui.group.bills.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.entities.database.Bill
import com.duwna.biblo.entities.items.AddBillMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.repositories.BillsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.utils.equalsDelta
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.util.*

class AddBillViewModel(private val groupItem: GroupItem) : BaseViewModel<AddBillState>(
    AddBillState(
        payerList = groupItem.members.map { AddBillMemberItem(it.id, it.name, it.avatarUrl) },
        debtorList = groupItem.members.map { AddBillMemberItem(it.id, it.name, it.avatarUrl) }
    )
) {

    private val repository = BillsRepository()

    fun setPayerSum(index: Int, value: Double) {
        updateState {
            copy(
                payerList = currentState.payerList.toMutableList().apply {
                    this[index] = this[index].copy(sum = value)
                }
            )
        }
        updateState { copy(sum = payerList.sumByDouble { it.sum }) }
    }

    fun setPayerChecked(index: Int) {
        updateState {
            copy(payerList = currentState.payerList.toMutableList().apply {
                this[index] = this[index].copy(
                    isChecked = !currentState.payerList[index].isChecked,
                    sum = 0.0
                )
            })
        }
    }

    fun splitSum() {
        val sum = currentState.sum / currentState.debtorList.count { it.isChecked }
        updateState {
            copy(
                debtorList = currentState.debtorList.map {
                    if (it.isChecked) it.copy(sum = sum)
                    else it
                }
            )
        }
    }

    fun setDebtorChecked(index: Int) {
        updateState {
            copy(debtorList = currentState.debtorList.toMutableList().apply {
                this[index] = this[index].copy(
                    isChecked = !currentState.debtorList[index].isChecked,
                    sum = 0.0
                )
            })
        }
    }

    fun setDebtorSum(index: Int, value: Double) {
        updateState {
            copy(
                debtorList = currentState.debtorList.toMutableList().apply {
                    this[index] = this[index].copy(sum = value)
                }
            )
        }
    }

    fun createBill(title: String, description: String, timestamp: Date) {

        if (!isSumValid()) {
            notify(Notify.TextMessage("Сумма долга должна соответствовать сумме оплаты"))
            return
        }

        if (title.isBlank()) {
            notify(Notify.TextMessage("Добавьте описание"))
            return
        }

        val payers = mutableMapOf<String, Double>().apply {
            currentState.payerList.forEach {
                if (it.isChecked && it.sum != 0.0) put(it.id, it.sum)
            }
        }

        val debtors = mutableMapOf<String, Double>().apply {
            currentState.debtorList.forEach {
                if (it.isChecked && it.sum != 0.0) put(it.id, it.sum)
            }
        }

        val bill = Bill(title, description, timestamp, payers, debtors)
        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                repository.insertBill(groupItem.id, bill)
                postUpdateState { copy(isLoading = false, ready = Unit) }
            } catch (t: Throwable) {
                t.printStackTrace()
                postUpdateState { copy(isLoading = false) }
                notify(Notify.Error())
            }
        }
    }

    private fun isSumValid(): Boolean = currentState.sum.equalsDelta(
        currentState.debtorList.sumByDouble { if (it.isChecked) it.sum else 0.0 }
    )
}

data class AddBillState(
    val payerList: List<AddBillMemberItem>,
    val debtorList: List<AddBillMemberItem>,
    val sum: Double = 0.0,
    val isLoading: Boolean = false,
    val ready: Unit? = null
) : IViewModelState


class AddBillViewModelFactory(private val groupItem: GroupItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddBillViewModel(groupItem) as T
    }
}