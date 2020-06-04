package com.duwna.biblo.ui.auth

import androidx.lifecycle.viewModelScope
import com.duwna.biblo.base.BaseViewModel
import com.duwna.biblo.base.IViewModelState
import com.duwna.biblo.base.Notify
import com.duwna.biblo.repositories.AuthRepository
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AuthViewModel : BaseViewModel<AuthState>(AuthState()) {

    private val repository = AuthRepository()

    fun firebaseAuthWithGoogle(idToken: String?) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                repository.firebaseAuthWithGoogle(idToken)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                notify(Notify.TextMessage("Возникла ошибка авторизации"))
                postUpdateState { copy(isLoading = false) }
            }
        }
    }

    fun enter(email: String, password: String) {
        updateState { copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                repository.signInWithEmailAndPassword(email, password)
                postUpdateState { copy(ready = Unit) }
            } catch (t: Throwable) {
                notify(Notify.TextMessage("Возникла ошибка авторизации"))
                postUpdateState { copy(isLoading = false) }
            }
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val ready: Unit? = null
) : IViewModelState