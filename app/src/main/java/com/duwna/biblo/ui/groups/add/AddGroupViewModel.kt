package com.duwna.biblo.ui.groups.add

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.duwna.biblo.R
import com.duwna.biblo.data.repositories.GroupsRepository
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddGroupViewModel @Inject constructor(
    private val repository: GroupsRepository,
    handle: SavedStateHandle
) : BaseViewModel<AddGroupState>(AddGroupState()) {

    private val groupItem = handle.get<GroupItem>("groupItem")

    init {
        initMembers()
    }

    private fun initMembers() {
        if (groupItem == null) {
            // add current user to members list
            launchSafety {
                val currentUser = repository.loadCurrentUserItem()
                postUpdateList { add(currentUser) }
            }
        } else {
            updateState {
                copy(
                    members = groupItem.members.map {
                        AddMemberItem(it.name, null, it.id, it.avatarUrl)
                    }
                )
            }
        }
    }

    fun insertMember(name: String) {
        when {
            name.trim().isBlank() -> {
                notify(Notify.MessageFromRes(R.string.message_add_member_name)); return
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
            updateState { copy(memberAvatarUri = null) }
        }
    }

    fun createGroup(groupName: String, groupCurrency: String) {

        if (!isInputValid(groupName, groupCurrency)) return

        updateState { copy(showViews = false) }
        launchSafety(onError = { postUpdateState { copy(showViews = true) } }) {
            showLoading()
            val users = currentState.members.map {
                User(name = it.name, avatarUri = it.avatarUri, idUser = it.id)
            }
            repository.insertGroup(
                groupName,
                groupCurrency,
                currentState.groupAvatarUri,
                users,
                if (currentState.clearGroupAvatar) groupItem?.copy(avatarUrl = null) else groupItem
            )
            postUpdateState { copy(onGroupAdded = Event(Unit)) }
        }
    }

    fun removeMember(position: Int) {
        when {
            groupItem == null && position == 0 -> {
                notify(Notify.MessageFromRes(R.string.message_cant_delete_yourself))
            }
            groupItem?.members?.find { it.id == currentState.members[position].id } != null -> {
                notify(Notify.MessageFromRes(R.string.message_cant_delete_user))
            }
            else -> updateList { removeAt(position) }
        }
    }

    private fun isInputValid(name: String, currency: String): Boolean {
        if (currentState.members.size < 2) {
            notify(Notify.MessageFromRes(R.string.message_group_contain_two_members))
            return false
        }
        if (name.trim().isBlank()) {
            notify(Notify.MessageFromRes(R.string.message_add_group_name))
            return false
        }
        if (currency.trim().isBlank()) {
            notify(Notify.MessageFromRes(R.string.message_add_currency))
            return false
        }
        return true
    }

    fun setImageUri(uri: Uri?) {
        when (currentState.imageAction) {
            AddGroupState.ImageAction.GROUP_AVATAR -> {
                updateState { copy(groupAvatarUri = uri, clearGroupAvatar = true) }
            }
            AddGroupState.ImageAction.MEMBER_AVATAR -> {
                updateState { copy(memberAvatarUri = uri) }
            }
        }
    }

    fun handleSearchMode() {
        updateState { copy(isSearchMode = !currentState.isSearchMode) }
    }

    fun setImageAction(imageAction: AddGroupState.ImageAction) {
        updateState { copy(imageAction = imageAction) }
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
    val members: List<AddMemberItem> = emptyList(),
    val groupAvatarUri: Uri? = null,
    val memberAvatarUri: Uri? = null,
    val isSearchMode: Boolean = false,
    val showViews: Boolean = true,
    val onGroupAdded: Event<Unit>? = null,
    val imageAction: ImageAction? = null,
    val clearGroupAvatar: Boolean = false
) : IViewModelState {
    // which image to change after activity result callback
    enum class ImageAction { GROUP_AVATAR, MEMBER_AVATAR }
}