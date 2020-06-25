package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.utils.log
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class AuthRepository : BaseRepository() {

    override val reference = database.collection("users")

    suspend fun authWithGoogle(idToken: String?) {
        val result = auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .await()

        val isNew = result.additionalUserInfo?.isNewUser

        if (isNew == true) {
            val user =
                User(name = auth.currentUser?.displayName ?: "", email = auth.currentUser?.email)
            insertUser(user)
        }
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

    suspend fun insertUser(user: User) {
        val avatarUrl = user.avatarUri?.let { uploadImg("users", firebaseUserId, it) }

        reference.document(firebaseUserId)
            .set(if (avatarUrl != null) user.copy(avatarUrl = avatarUrl) else user)
            .await()
    }

    suspend fun getUser(): User {
        return reference.document(firebaseUserId).get().await().toObject<User>()!!
    }

    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() = auth.signOut()
}