package com.duwna.biblo.ui.groups.members

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.R
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.repositories.GroupsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.utils.tryOrNull
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddMembersViewModel(private val groupItem: GroupItem?) :
    BaseViewModel<AddMembersState>(AddMembersState()) {

    private val repository = GroupsRepository()

    init {
        // Edit group mode -> add all users
        if (groupItem != null) {
            updateList {
                addAll(groupItem.members.map {
                    AddMemberItem(
                        it.name,
                        tryOrNull { Uri.parse(it.avatarUrl) },
                        it.id
                    )
                })
            }
        // Create group mode -> add only yourself
        } else {
            launchSafety {
                val info = repository.getUserInfo()
                postUpdateList { add(info) }
            }
        }

    }


    fun insertMember(name: String) {
        when {
            name.trim().isBlank() -> {
                notify(Notify.MessageFromRes(R.string.message_add_name)); return
            }
            checkMemberContains(name) -> {
                notify(Notify.MessageFromRes(R.string.message_member_contains)); return
            }
        }

        if (currentState.isSearch) {
            viewModelScope.launch(IO) {
                try {
                    val memberItem = repository.searchMember(name)
                    if (memberItem != null) postUpdateList { add(memberItem) }
                    else notify(Notify.MessageFromRes(R.string.message_no_user_found))
                } catch (t: Throwable) {
                    t.printStackTrace()
                    notify(Notify.DataError)
                }
            }
        } else {
            updateList { add(AddMemberItem(name, currentState.memberAvatarUri)) }
            setImageUri(null)
        }
    }

    fun removeMember(position: Int) {
        when {
            position == 0 -> notify(Notify.MessageFromRes(R.string.message_cant_delete_yourself))
            groupItem?.members?.find { it.id == currentState.members[position].id } != null ->
                notify(Notify.MessageFromRes(R.string.message_cant_delete_user))
            else -> updateList { removeAt(position) }
        }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(memberAvatarUri = uri) }
    }

    fun createGroup(groupName: String, groupCurrency: String, groupAvatarUri: Uri?) {

        if (currentState.members.size < 2) {
            notify(Notify.MessageFromRes(R.string.message_group_contain_two_members))
            return
        }

        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                val users = currentState.members.map {
                    User(name = it.name, avatarUri = it.avatarUri, idUser = it.id)
                }
                repository.insertGroup(groupName, groupCurrency, groupAvatarUri, users, groupItem)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                notify(Notify.DataError)
                postUpdateState { copy(isLoading = false) }
            }
        }
    }

    fun handleSearchMode() {
        updateState { copy(isSearch = !currentState.isSearch) }
    }

    private fun updateList(block: MutableList<AddMemberItem>.() -> Unit) {
        updateState {
            copy(members = currentState.members.toMutableList().apply {
                block()
            })
        }
    }

    private fun postUpdateList(block: MutableList<AddMemberItem>.() -> Unit) {
        postUpdateState {
            copy(members = currentState.members.toMutableList().apply {
                block()
            })
        }
    }

    private fun checkMemberContains(name: String): Boolean {
        return currentState.members
            .find { it.name == name && it.avatarUri == currentState.memberAvatarUri } != null
    }
}

data class AddMembersState(
    val members: List<AddMemberItem> = emptyList(),
    val memberAvatarUri: Uri? = null,
    val isLoading: Boolean = false,
    val isSearch: Boolean = false,
    val ready: Unit? = null
) : IViewModelState


class AddMembersViewModelFactory(private val groupItem: GroupItem?) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddMembersViewModel(groupItem) as T
    }
}