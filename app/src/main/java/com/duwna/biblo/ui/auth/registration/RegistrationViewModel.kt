package com.duwna.biblo.ui.auth.registration

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.base.BaseViewModel
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.base.Notify
import com.duwna.biblo.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistrationViewModel : BaseViewModel<RegistrationState>(RegistrationState()) {

    private val repository = AuthRepository()

    fun registerUser(name: String, email: String, password: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.registerUser(name, email, password, currentState.avatarUri)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                notify(Notify.TextMessage(t.localizedMessage ?: "Возникла ошибка регистрации"))
                postUpdateState { copy(isLoading = false) }
            }
        }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(avatarUri = uri) }
    }
}

data class RegistrationState(
    val isLoading: Boolean = false,
    val ready: Unit? = null,
    val avatarUri: Uri? = null
) : IViewModelState