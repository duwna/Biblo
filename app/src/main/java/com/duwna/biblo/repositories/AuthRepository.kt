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

    suspend fun registerUserWithEmail(
        name: String,
        email: String,
        password: String,
        avatarUri: Uri?
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .await()

        val user = User(firebaseUserId, name, email, null, avatarUri)
        insertUser(user)
    }

    private suspend fun insertUser(user: User) {

        val avatarUrl = user.avatarUri?.let { uploadImg("users", firebaseUserId, it) }

        database.collection("users")
            .document(firebaseUserId)
            .set(user.copy(avatarUrl = avatarUrl))
            .await()
    }

    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}