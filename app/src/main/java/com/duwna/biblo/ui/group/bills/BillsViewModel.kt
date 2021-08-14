package com.duwna.biblo.ui.group.bills

import androidx.lifecycle.SavedStateHandle
import com.duwna.biblo.data.repositories.BillsRepository
import com.duwna.biblo.entities.database.Bill
import com.duwna.biblo.entities.items.BillMemberItem
import com.duwna.biblo.entities.items.BillsViewItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.format
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class BillsViewModel @Inject constructor(
    private val repository: BillsRepository,
    handle: SavedStateHandle
) : BaseViewModel<BillsState>(BillsState()) {

    private val groupItem = handle.get<GroupItem>("groupItem")!!

    init {
        launchSafety { subscribeOnBillsList() }
    }

    private suspend fun subscribeOnBillsList() {
        showLoading()
        repository.subscribeOnBills(groupItem.id).collect { bills ->
            if (bills.isEmpty()) postUpdateState {
                copy(bills = emptyList(), showNoBillsText = true)
            } else postUpdateState {
                copy(bills = bills.toBillViewItemList(), showNoBillsText = false)
            }
            hideLoading()
        }
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
        launchSafety {
            showLoading()
            repository.deleteBill(groupItem.id, idBill)
        }
    }

    fun deleteGroup() {
        launchSafety {
            showLoading()
            repository.deleteGroup(groupItem.id)
            postUpdateState { copy(onGroupDeleted = Event(Unit)) }
        }
    }

    fun generateStatisticsMessage(
        labelGroup: String,
        labelCurrency: String,
        labelStatistics: String,
        labelWhoPayed: String,
        labelForWhom: String,
        labelAllBills: String
    ): String = buildString {
        currentState.bills.forEach { billItem ->
            when (billItem) {
                is BillsViewItem.Header -> {
                    append(
                        """
                        $labelGroup: ${billItem.name}
                        $labelCurrency: ${billItem.currency}
                        $labelStatistics:
                        
                        $labelWhoPayed
                        """.trimIndent()
                    )
                    billItem.members.filter { it.sum > 0 }.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                    append("\n\n$labelForWhom:")
                    billItem.members.filter { it.sum < 0 }.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                    append("\n_____________________")
                    append("\n$labelAllBills\n")
                }
                is BillsViewItem.Bill -> {
                    append("\n${billItem.title}")
                    if (billItem.description.isNotBlank()) append("\n${billItem.description}")
                    append("\n${billItem.timestamp}")
                    append("\n\n$labelWhoPayed")
                    billItem.payers.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                    append("\n\n$labelForWhom")
                    billItem.debtors.forEach { member ->
                        append("\n${member.name}: ${member.sum.format()}")
                    }
                    append("\n_____________________")
                }
            }
        }
    }

}

data class BillsState(
    val bills: List<BillsViewItem> = emptyList(),
    val showNoBillsText: Boolean = false,
    val onGroupDeleted: Event<Unit>? = null
) : IViewModelState