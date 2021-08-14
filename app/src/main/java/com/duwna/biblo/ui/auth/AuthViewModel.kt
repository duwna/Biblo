package com.duwna.biblo.ui.auth

import com.duwna.biblo.R
import com.duwna.biblo.data.repositories.AuthRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository
) : BaseViewModel<AuthState>(AuthState()) {

    fun firebaseAuthWithGoogle(idToken: String?) {
        launchSafety {
            repository.authWithGoogle(idToken)
            postUpdateState { copy(ready = Event(Unit)) }
        }
    }

    fun enter(email: String, password: String) {
        launchSafety {
            repository.signInWithEmail(email, password)
            postUpdateState { copy(ready = Event(Unit)) }
        }
    }

    fun resetPassword(email: String) {
        launchSafety {
            repository.resetPassword(email)
            notify(Notify.MessageFromRes(R.string.message_password_link_sent))
        }
    }
}

data class AuthState(
    val ready: Event<Unit>? = null
) : IViewModelState