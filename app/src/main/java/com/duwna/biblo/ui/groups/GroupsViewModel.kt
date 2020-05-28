package com.duwna.biblo.ui.groups

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.base.BaseViewModel
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.models.database.Group
import com.duwna.biblo.models.items.GroupItem
import com.duwna.biblo.utils.log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GroupsViewModel : BaseViewModel<GroupsViewModelState>(
    GroupsViewModelState()
) {

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch(IO) {
            delay(3000)
//            val list = getGroupList()
//            updateState { it.copy(groups = list, isLoading = false) }



        }
    }


}



data class GroupsViewModelState(
    val groups: List<GroupItem> = emptyList(),
    val isLoading: Boolean = true
) : IViewModelState