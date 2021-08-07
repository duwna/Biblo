package com.duwna.biblo.ui.auth

import com.duwna.biblo.R
import com.duwna.biblo.data.repositories.AuthRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify

class AuthViewModel : BaseViewModel<AuthState>(AuthState()) {

    private val repository = AuthRepository()

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