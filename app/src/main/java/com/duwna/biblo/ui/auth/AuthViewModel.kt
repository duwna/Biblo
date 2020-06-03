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
        updateState { it.copy(isLoading = true) }
        viewModelScope.launch(IO) {
            try {
                repository.firebaseAuthWithGoogle(idToken)
            } catch (t: Throwable) {
                notify(Notify.TextMessage("Возникла ошибка авторизации"))
            }
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false
) : IViewModelState