package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.User
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class AuthRepository : BaseRepository() {

    override val reference = database.collection("users")

    suspend fun authWithGoogle(idToken: String?) {
        val result = auth
            .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .await()

        // if first auth -> write data to db
        if (result.additionalUserInfo?.isNewUser == true) {
            val user = User(
                name = auth.currentUser?.displayName ?: "",
                email = auth.currentUser?.email,
                avatarUrl = auth.currentUser?.photoUrl?.toString()
            )
            insertUser(user)
        // if user already exists -> load data from network
        } else {
            updateLocalUserInfoFromNetwork()
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

    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()

        updateLocalUserInfoFromNetwork()
    }

    suspend fun insertUser(user: User) {
        val avatarUrl = user.avatarUri?.let { uploadImage("users", firebaseUserId, it) }
        val newUser = if (avatarUrl != null) user.copy(avatarUrl = avatarUrl) else user

        reference.document(firebaseUserId)
            .set(newUser)
            .await()

        PrefManager.saveUser(newUser.copy(idUser = firebaseUserId))
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() = auth.signOut()

    private suspend fun updateLocalUserInfoFromNetwork() {
        val newUser = reference.document(firebaseUserId).get().await().toObject<User>()!!
        PrefManager.saveUser(newUser.copy(idUser = firebaseUserId))
    }
}
