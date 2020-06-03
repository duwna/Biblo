package com.duwna.biblo.repositories

import com.duwna.biblo.base.BaseRepository
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository : BaseRepository() {

    suspend fun firebaseAuthWithGoogle(idToken: String?) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .await()
    }

}