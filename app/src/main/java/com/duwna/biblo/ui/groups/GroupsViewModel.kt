package com.duwna.biblo.ui.groups

import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.data.repositories.GroupsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState

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
        launchSafety {
            if (currentState.groups.isEmpty()) showLoading()
            val groupItems = repository.loadGroupItems()

            if (groupItems.isEmpty()) {
                postUpdateState { copy(groups = emptyList(), showNoGroupsText = true) }
            } else {
                postUpdateState {
                    copy(
                        groups = groupItems,
                        showNoGroupsText = false,
                        onNewGroupsLoaded = if (currentState.groups.size < groupItems.size)
                            Event(Unit) else null
                    )
                }
            }
        }
    }
}


data class GroupsViewModelState(
    val groups: List<GroupItem> = emptyList(),
    val isAuth: Boolean = true,
    val showNoGroupsText: Boolean = false,
    val onNewGroupsLoaded: Event<Unit>? = null
) : IViewModelState