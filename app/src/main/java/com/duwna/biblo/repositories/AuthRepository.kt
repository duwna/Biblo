package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.base.BaseRepository
import com.duwna.biblo.models.database.User
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class AuthRepository : BaseRepository() {

    suspend fun firebaseAuthWithGoogle(idToken: String?) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .await()
    }

    suspend fun registerUser(name: String, email: String, password: String, avatarUri: Uri?) {
        auth.createUserWithEmailAndPassword(email, password)
            .await()

        val user = User(firebaseUserId, name, email, avatarUri)
        insertUser(user)

        avatarUri?.let { addUserAvatar(it) }
    }

    private suspend fun addUserAvatar(avatarUri: Uri) {
        storage.child("user_avatars")
            .child(firebaseUserId)
            .putFile(avatarUri)
            .await()
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()
    }

    private suspend fun insertUser(user: User): String {
        return database.collection("users")
            .add(user)
            .await()
            .id
    }
}