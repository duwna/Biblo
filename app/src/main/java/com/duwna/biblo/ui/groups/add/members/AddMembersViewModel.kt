package com.duwna.biblo.ui.groups.add.members

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.base.BaseViewModel
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.base.Notify
import com.duwna.biblo.models.database.Group
import com.duwna.biblo.models.database.User
import com.duwna.biblo.models.items.AddMemberItem
import com.duwna.biblo.repositories.GroupsRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddMembersViewModel : BaseViewModel<AddMembersState>(AddMembersState()) {

    private val repository = GroupsRepository()

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
        updateList { removeAt(position) }
    }

    fun setImageUri(uri: Uri?) {
        updateState { it.copy(memberAvatarUri = uri) }
    }

    fun createGroup(groupName: String, groupCurrency: String, groupAvatarUri: Uri?) {
        updateState { it.copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                val users = currentState.members.map {
                    User(name = it.name, avatarUri = it.avatarUri)
                }
                repository.insertGroup(groupName, groupCurrency, groupAvatarUri, users)
                postUpdateState { it.copy(ready = Unit) }
            } catch (t: Throwable) {
                t.printStackTrace()
                notify(Notify.Error())
            }
            postUpdateState { it.copy(isLoading = false) }
        }
    }

    private fun updateList(block: MutableList<AddMemberItem>.() -> Unit) {
        updateState {
            it.copy(members = currentState.members.toMutableList().apply {
                block()
            })
        }
    }

    private fun checkMemberContains(name: String): Boolean {
        var contains = false
        currentState.members.forEach {
            if (it.name == name && it.avatarUri == currentState.memberAvatarUri) contains = true
        }
        return contains
    }


}

data class AddMembersState(
    val members: List<AddMemberItem> = emptyList(),
    val memberAvatarUri: Uri? = null,
    val isLoading: Boolean = false,
    val ready: Unit? = null
) : IViewModelState
