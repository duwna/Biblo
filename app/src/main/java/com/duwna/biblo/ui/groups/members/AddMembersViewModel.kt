package com.duwna.biblo.ui.groups.members

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.repositories.GroupsRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddMembersViewModel : BaseViewModel<AddMembersState>(AddMembersState()) {

    private val repository = GroupsRepository()

    init {
        loadFirstMember()
    }

    private fun loadFirstMember() {
        doAsync {
            val userInfo = repository.getUserInfo()
            postUpdateList { add(userInfo) }
        }
    }

    fun insertMember(name: String) {
        when {
            name.trim().isBlank() -> {
                notify(Notify.TextMessage("Имя не может быть пустым")); return
            }
            checkMemberContains(name) -> {
                notify(Notify.TextMessage("Участник с таким именем и аватаром уже содержится")); return
            }
        }
        updateList { add(AddMemberItem(name, currentState.memberAvatarUri)) }
        setImageUri(null)
    }

    fun removeMember(position: Int) {
        if (position == 0) notify(Notify.TextMessage("Невозможно удалить себя из группы"))
        else updateList { removeAt(position) }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(memberAvatarUri = uri) }
    }

    fun createGroup(groupName: String, groupCurrency: String, groupAvatarUri: Uri?) {

        if (currentState.members.size < 2) {
            notify(Notify.TextMessage("В группе может быть не меньше двух участников"))
            return
        }

        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                val users = currentState.members.map {
                    User(name = it.name, avatarUri = it.avatarUri)
                }
                repository.insertGroup(groupName, groupCurrency, groupAvatarUri, users)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                notify(Notify.Error())
                postUpdateState { copy(isLoading = false) }
            }
        }
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
    val ready: Unit? = null
) : IViewModelState
