package com.duwna.biblo.ui.auth.profile

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.repositories.AuthRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ProfileViewModel : BaseViewModel<ProfileState>(ProfileState()) {

    private val repository = AuthRepository()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch(IO) {
            try {
                val user = repository.getUser()
                postUpdateState { copy(user = user, isLoading = false) }
            } catch (t: Throwable) {
                notify(Notify.Error())
            }
        }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(tmpAvatarUri = uri) }
    }

    fun saveUser(name: String) {
        val user = currentState.user?.copy(
            name = name, avatarUri = currentState.tmpAvatarUri
        ) ?: return
        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                repository.insertUser(user)
                postUpdateState { copy(isLoading = false) }
                notify(Notify.TextMessage("Данные сохранены!"))
            } catch (t: Throwable) {
                notify(Notify.Error())
            }
        }
    }

    fun signOut() = repository.signOut()
}

data class ProfileState(
    val user: User? = null,
    val tmpAvatarUri: Uri? = null,
    val isLoading: Boolean = true
) : IViewModelState