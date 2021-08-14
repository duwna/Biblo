package com.duwna.biblo.ui.auth.profile

import android.net.Uri
import com.duwna.biblo.R
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.data.repositories.AuthRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<ProfileState>(ProfileState()) {

    init {
        launchSafety { postUpdateState { copy(user = repository.getLocalUserInfo()) } }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(tmpAvatarUri = uri) }
    }

    fun saveUser(name: String) {

        val user = currentState.user?.copy(
            name = name, avatarUri = currentState.tmpAvatarUri
        ) ?: return

        launchSafety {
            showLoading()
            repository.insertUser(user)
            postUpdateState { copy(user = user) }
            notify(Notify.MessageFromRes(R.string.message_data_saved))
        }
    }

    fun deleteAvatar() {
        setImageUri(null)
        updateState { copy(user = currentState.user?.copy(avatarUrl = null)) }
    }

    fun saveTheme(mode: Int) {
        launchSafety { repository.saveTheme(mode) }
    }

    fun signOut() = repository.signOut()
}

data class ProfileState(
    val user: User? = null,
    val tmpAvatarUri: Uri? = null,
) : IViewModelState
