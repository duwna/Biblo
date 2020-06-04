package com.duwna.biblo.ui.groups

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.base.BaseViewModel
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.base.Notify
import com.duwna.biblo.models.items.GroupItem
import com.duwna.biblo.repositories.GroupsRepository
import com.duwna.biblo.utils.getGroupList
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class GroupsViewModel : BaseViewModel<GroupsViewModelState>(
    GroupsViewModelState()
) {

    private val repository = GroupsRepository()

    init {
        if (!repository.userExists()) updateState { copy(isAuth = false) }
        updateState { copy(isLoading = true) }
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch(IO) {
            try {
                val groupItems = repository.loadGroupItems()
//                val groupItems = getGroupList()
                postUpdateState { copy(groups = groupItems, isLoading = false) }
            } catch (t: Throwable) {
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