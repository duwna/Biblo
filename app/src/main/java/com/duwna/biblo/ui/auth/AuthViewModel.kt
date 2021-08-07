package com.duwna.biblo.ui.auth

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.R
import com.duwna.biblo.data.repositories.AuthRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AuthViewModel : BaseViewModel<AuthState>(AuthState()) {

    private val repository = AuthRepository()

    fun firebaseAuthWithGoogle(idToken: String?) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                repository.authWithGoogle(idToken)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                notify(Notify.MessageFromRes(R.string.message_auth_error))
                postUpdateState { copy(isLoading = false) }
            }
        }
    }

    fun enter(email: String, password: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                repository.signInWithEmail(email, password)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                notify(Notify.MessageFromRes(R.string.message_auth_error))
                postUpdateState { copy(isLoading = false) }
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch(IO) {
            try {
                repository.resetPassword(email)
                notify(Notify.MessageFromRes(R.string.message_password_link_sent))
            } catch (t: Throwable) {
                notify(Notify.MessageFromRes(R.string.message_no_user_found))
            }
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val ready: Unit? = null
) : IViewModelState