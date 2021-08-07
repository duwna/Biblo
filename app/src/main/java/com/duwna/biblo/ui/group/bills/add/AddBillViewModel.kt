package com.duwna.biblo.ui.group.bills.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duwna.biblo.R
import com.duwna.biblo.entities.database.Bill
import com.duwna.biblo.entities.items.AddBillMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.data.repositories.BillsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.utils.equalsDelta
import java.util.*

class AddBillViewModel(groupItem: GroupItem) : BaseViewModel<AddBillState>(
    AddBillState(
        payerList = groupItem.members.map { AddBillMemberItem(it.id, it.name, it.avatarUrl) },
        debtorList = groupItem.members.map { AddBillMemberItem(it.id, it.name, it.avatarUrl) }
    )
) {
    private val repository = BillsRepository(groupItem.id)

    fun setPayerSum(index: Int, value: Double) {
        updateState {
            copy(
                payerList = currentState.payerList.toMutableList().apply {
                    this[index] = this[index].copy(sum = value)
                }
            )
        }
        updateState { copy(sum = payerList.sumOf { it.sum }) }
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

    fun createBill(title: String, description: String) {

        if (!isSumValid()) {
            notify(Notify.MessageFromRes(R.string.message_debt_pay_sum))
            return
        }
        if (title.isBlank()) {
            notify(Notify.MessageFromRes(R.string.message_add_title))
            return
        }

        updateState { copy(showViews = false) }

        launchSafety(onError = { postUpdateState { copy(showViews = true) } }) {
            showLoading()

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
            val bill = Bill(title, description, currentState.date, payers, debtors)

            repository.insertBill(bill)
            postUpdateState { copy(onBillAdded = Event(Unit)) }
        }
    }

    private fun isSumValid(): Boolean = currentState.sum.equalsDelta(
        currentState.debtorList.sumOf { if (it.isChecked) it.sum else 0.0 }
    )

    fun setDate(date: Date) {
        updateState { copy(date = date) }
    }
}

data class AddBillState(
    val payerList: List<AddBillMemberItem> = emptyList(),
    val debtorList: List<AddBillMemberItem> = emptyList(),
    val sum: Double = 0.0,
    val showViews: Boolean = true,
    val date: Date = Date(),
    val onBillAdded: Event<Unit>? = null
) : IViewModelState


class AddBillViewModelFactory(private val groupItem: GroupItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddBillViewModel(groupItem) as T
    }
}