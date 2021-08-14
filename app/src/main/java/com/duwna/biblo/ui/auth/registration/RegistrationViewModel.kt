package com.duwna.biblo.ui.auth.registration

import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.R
import com.duwna.biblo.data.repositories.AuthRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<RegistrationState>(RegistrationState()) {

    fun registerUser(name: String, email: String, password: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.registerUserWithEmail(name, email, password, currentState.avatarUri)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                if (t.localizedMessage != null) notify(Notify.TextMessage(t.localizedMessage!!))
                else notify(Notify.MessageFromRes(R.string.message_registration_error))
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