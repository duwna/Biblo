package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.User
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository : BaseRepository() {

    suspend fun authWithGoogle(idToken: String?) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .await()

        val user = User(name = auth.currentUser?.displayName ?: "", email = auth.currentUser?.email)
        insertUser(user)
    }

    suspend fun registerUserWithEmail(name: String, email: String, password: String, avatarUri: Uri?) {
        auth.createUserWithEmailAndPassword(email, password)
            .await()

        val user = User(firebaseUserId, name, email, avatarUri)
        insertUser(user)

        avatarUri?.let { uploadImg("users", firebaseUserId, it) }
    }

    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()
    }

    private suspend fun insertUser(user: User) {
        database.collection("users")
            .document(firebaseUserId)
            .set(user)
            .await()
    }
}