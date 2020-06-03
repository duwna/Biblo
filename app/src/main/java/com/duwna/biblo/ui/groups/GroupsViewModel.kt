package com.duwna.biblo.ui.groups

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.base.BaseRepository
import com.duwna.biblo.base.BaseViewModel
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.base.Notify
import com.duwna.biblo.models.items.GroupItem
import com.duwna.biblo.repositories.GroupsRepository
import com.duwna.biblo.utils.getGroupList
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GroupsViewModel : BaseViewModel<GroupsViewModelState>(
    GroupsViewModelState()
) {

    private val repository = GroupsRepository()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch(IO) {
            delay(3000)
//            val list = getGroupList()
//            postUpdateState { it.copy(groups = list, isLoading = false) }
            try {
                val groupItems = repository.loadGroupItems()
                postUpdateState { it.copy(groups = groupItems, isLoading = false) }
            } catch (e: BaseRepository.NoAuthException){
                postUpdateState { it.copy(isAuth = false, isLoading = false) }
            } catch (t: Throwable) {
                notify(Notify.Error())
            }
        }
    }
}


data class GroupsViewModelState(
    val groups: List<GroupItem> = emptyList(),
    val isLoading: Boolean = true,
    val isAuth: Boolean = false
) : IViewModelState