package com.duwna.biblo.ui.groups

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.entities.database.Bill
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.repositories.BillsRepository
import com.duwna.biblo.repositories.GroupsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class GroupsViewModel : BaseViewModel<GroupsViewModelState>(
    GroupsViewModelState()
) {

    private val repository = GroupsRepository()

//    init {
//        doAsync {
//            repository.subscribeOnGroupItems().collect { groupItems ->
//                postUpdateState { copy(groups = groupItems, isLoading = false) }
//            }
//        }
//    }

    fun initialize() {
        if (repository.userExists()) {
            loadGroups()
        } else {
            updateState { copy(isAuth = false) }
        }
    }

    private fun loadGroups() {
        viewModelScope.launch(IO) {

            val currency = listOf(
                "₽",
                "$",
                "¢",
                "£",
                "¤",
                "¥",
                "֏",
                "৲",
                "৳",
                "৻",
                "૱",
                "௹",
                "฿",
                "៛",
                "₠",
                "₡",
                "₢",
                "₣",
                "₤",
                "₥",
                "₦",
                "₧",
                "₨",
                "₩",
                "₪",
                "₫",
                "€",
                "₭",
                "₮",
                "₯",
                "₰",
                "₲",
                "₳",
                "₴",
                "₵",
                "₶",
                "₷",
                "₸",
                "₹",
                "₺"
            )

            val words = listOf(
                "world",
                "any",
                "content",
                "believe",
                "strange",
                "same",
                "habit",
                "behavior",
                "coal",
                "curious",
                "absolute",
                "former",
                "camera",
                "stiffen",
                "enemy",
                "salt",
                "camp",
                "bridge",
                "accept",
                "polite",
                "plant",
                "wood",
                "ill",
                "wood",
                "drive"
            )

            val users = MutableList((2..7).random()) {
                if (it == 0) User(idUser = repository.firebaseUserId)
                else User(name = words.random())
            }


//            repeat(100) {
//                repository.insertGroup(
//                    name = words.random(),
//                    currency = currency.random(),
//                    avatarUri = null,
//                    users = users,
//                    groupItem = null
//                )
//            }

            try {
                val groupItems = repository.loadGroupItems()

//                groupItems.forEach { groupItem ->
//                    val repo = BillsRepository(groupItem.id)
//                    repeat((5..30).random()) {
//                        val bill = Bill(
//                            title = words.random(),
//                            description = buildString { repeat(5) { append(words.random()) } },
//                            payers = mutableMapOf<String, Double>().apply {
//                                groupItem.members.forEach { put(it.id, (0..10000).random().toDouble()) }
//                            },
//                            debtors = mutableMapOf<String, Double>().apply {
//                                groupItem.members.forEach { put(it.id, (0..10000).random().toDouble()) }
//                            }
//                        )
//                        repo.insertBill(bill)
//                    }
//                }

                postUpdateState { copy(groups = groupItems, isLoading = false) }
            } catch (t: Throwable) {
                postUpdateState { copy(isLoading = false) }
                t.printStackTrace()
                notify(Notify.DataError)
            }
        }
    }

    fun deleteGroup(id: String) {
        viewModelScope.launch(IO) {
            try {
                repository.deleteGroup(id)
                loadGroups()
            } catch (t: Throwable) {
                notify(Notify.DataError)
            }
        }
    }
}


data class GroupsViewModelState(
    val groups: List<GroupItem> = emptyList(),
    val isLoading: Boolean = true,
    val isAuth: Boolean = true
) : IViewModelState