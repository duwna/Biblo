package com.duwna.biblo.ui.groups.add

import android.net.Uri
import com.duwna.biblo.R
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify

class AddGroupViewModel : BaseViewModel<AddGroupState>(AddGroupState()) {

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

}

data class AddGroupState(
    val tmpAvatarUri: Uri? = null
) : IViewModelState