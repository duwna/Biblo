package com.duwna.biblo.ui.groups.add

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.R
import com.duwna.biblo.repositories.GroupsRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AddGroupViewModel : BaseViewModel<AddGroupState>(AddGroupState()) {

    private val repository = GroupsRepository()

    fun validateInput(name: String, currency: String): Boolean = when {
        name.trim().isBlank() -> {
            notify(Notify.MessageFromRes(R.string.message_add_name))
            false
        }
        currency.trim().isBlank() -> {
            notify(Notify.MessageFromRes(R.string.message_add_currency))
            false
        }
        else -> true
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(tmpAvatarUri = uri) }
    }

    fun deleteGroup(id: String) {
        viewModelScope.launch(IO) {
            try {
                repository.deleteGroup(id)
                postUpdateState { copy(deleted = Unit) }
            } catch (t: Throwable) {
                notify(Notify.DataError)
            }
        }
    }

}

data class AddGroupState(
    val tmpAvatarUri: Uri? = null,
    val deleted: Unit? = null
) : IViewModelState