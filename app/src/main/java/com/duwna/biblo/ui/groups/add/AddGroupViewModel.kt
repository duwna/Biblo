package com.duwna.biblo.ui.groups.add

import android.net.Uri
import com.duwna.biblo.R
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.repositories.GroupsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.utils.log
import com.duwna.biblo.utils.tryOrNull

class AddGroupViewModel : BaseViewModel<AddGroupState>(AddGroupState()) {

    private val repository = GroupsRepository()

    init {
        launchSafety {
            val currentUser = repository.getLocalUserInfo()
            log(currentUser)
            val memberItem = AddMemberItem(
                name = currentUser.name,
                avatarUrl = currentUser.avatarUrl,
                id = currentUser.idUser
            )
            postUpdateList { add(memberItem) }
        }
    }

    fun insertMember(name: String) {
        when {
            name.trim().isBlank() -> {
                notify(Notify.MessageFromRes(R.string.message_add_name)); return
            }
            memberContains(name) -> {
                notify(Notify.MessageFromRes(R.string.message_member_contains)); return
            }
        }

        if (currentState.isSearchMode) {
            launchSafety {
                val memberItem = repository.searchMember(name)

                if (memberItem != null) {
                    if (currentState.members.find { it.id == memberItem.id } != null) {
                        notify(Notify.MessageFromRes(R.string.message_member_contains))
                    }
                    postUpdateList { add(memberItem) }
                    notify(Notify.MessageFromRes(R.string.message_user_added))
                } else {
                    notify(Notify.MessageFromRes(R.string.message_no_user_found))
                }
            }
        } else {
            updateList { add(AddMemberItem(name, currentState.memberAvatarUri)) }
            setMemberImageUri(null)
        }
    }

    fun createGroup(groupName: String, groupCurrency: String) {

        if (currentState.members.size < 2) {
            notify(Notify.MessageFromRes(R.string.message_group_contain_two_members))
            return
        }

        launchSafety {
            val users = currentState.members.map {
                User(name = it.name, avatarUri = it.avatarUri, idUser = it.id)
            }
            repository.insertGroup(
                groupName,
                groupCurrency,
                currentState.groupAvatarUri,
                users,
                null
            )
            postUpdateState { copy(groupAddedEvent = Event(Unit)) }
        }
    }

    fun removeMember(position: Int) {
        when (position) {
            0 -> notify(Notify.MessageFromRes(R.string.message_cant_delete_yourself))
            else -> updateList { removeAt(position) }
        }
    }

    fun validateInput(name: String, currency: String): Boolean = when {
        name.trim().isBlank() -> {
            notify(Notify.MessageFromRes(R.string.message_add_name))
            false
        }
        currency.trim().isBlank() -> {
            notify(Notify.MessageFromRes(R.string.message_add_currency))
            false
        }
        else -> true
    }

    fun setGroupImageUri(uri: Uri?) {
        updateState { copy(groupAvatarUri = uri) }
    }

    fun setMemberImageUri(uri: Uri?) {
        updateState { copy(memberAvatarUri = uri) }
    }

    fun handleSearchMode() {
        updateState { copy(isSearchMode = !currentState.isSearchMode) }
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

    private fun memberContains(name: String): Boolean {
        return currentState.members
            .find { it.name == name && it.avatarUri == currentState.memberAvatarUri } != null
    }
}

data class AddGroupState(
    val groupAvatarUri: Uri? = null,
    val isSearchMode: Boolean = false,
    val members: List<AddMemberItem> = emptyList(),
    val memberAvatarUri: Uri? = null,
    val groupAddedEvent: Event<Unit>? = null
) : IViewModelState