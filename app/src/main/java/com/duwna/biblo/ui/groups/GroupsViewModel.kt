package com.duwna.biblo.ui.groups

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.entities.items.GroupItem
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

    init {
        if (repository.userExists()) {
            updateState { copy(isLoading = true) }
            loadGroups()
        } else {
            updateState { copy(isAuth = false) }
        }
    }

    private fun loadGroups() {
        viewModelScope.launch(IO) {
            try {
                val groupItems = repository.loadGroupItems()
//                val groupItems = getGroupList()
                postUpdateState { copy(groups = groupItems, isLoading = false) }
            } catch (t: Throwable) {
                throw t
                t.printStackTrace()
                notify(Notify.Error())
            }
        }
    }

    fun signOut() {
        repository.signOut()
        updateState { copy(isAuth = false) }
    }
}


data class GroupsViewModelState(
    val groups: List<GroupItem> = emptyList(),
    val isLoading: Boolean = false,
    val isAuth: Boolean = true
) : IViewModelState