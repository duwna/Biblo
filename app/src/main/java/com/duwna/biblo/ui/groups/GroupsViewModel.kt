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

    fun initialize() {
        if (repository.userExists()) {
            loadGroups()
        } else {
            updateState { copy(isAuth = false) }
        }
    }

    private fun loadGroups() {
        viewModelScope.launch(IO) {
            try {
                val groupItems = repository.loadGroupItems()
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