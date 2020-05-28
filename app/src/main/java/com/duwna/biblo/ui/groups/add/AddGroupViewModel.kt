package com.duwna.biblo.ui.groups.add

import android.net.Uri
import com.duwna.biblo.base.BaseViewModel
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.models.items.AddMemberItem

class AddGroupViewModel : BaseViewModel<AddGroupState>(AddGroupState()) {


    fun removeMember(position: Int) {
        updateList { removeAt(position) }
    }

    fun insertMember(name: String) {
        if (checkMemberContains(name)) return
        updateList { add(AddMemberItem(name)) }
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
        currentState.members.forEach { if (it.name == name) contains = true }
        return contains
    }
}

data class AddGroupState(
    val members: List<AddMemberItem> = emptyList(),
    val memberAvatarUri: Uri? = null
) : IViewModelState