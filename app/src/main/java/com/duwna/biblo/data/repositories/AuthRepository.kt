package com.duwna.biblo.data.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.DatabaseConstants.USERS
import com.duwna.biblo.data.PrefManager
import com.duwna.biblo.data.UploadImageManager
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.utils.userId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val prefs: PrefManager,
    private val imageManager: UploadImageManager,
    private val database: FirebaseFirestore
) {

    suspend fun authWithGoogle(idToken: String?) {
        val result = auth
            .signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .await()

        // if first auth -> write data to db
        if (result.additionalUserInfo?.isNewUser == true) {
            val user = User(
                name = auth.currentUser?.displayName ?: "name",
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

        val user = User(auth.userId(), name, email, null, avatarUri)
        insertUser(user)
    }

    suspend fun signInWithEmail(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .await()

        updateLocalUserInfoFromNetwork()
    }

    suspend fun insertUser(user: User) {
        val avatarUrl = imageManager.uploadOrNull(USERS, auth.userId(), user.avatarUri)
        val newUser = if (avatarUrl != null) user.copy(avatarUrl = avatarUrl) else user

        database.collection(USERS)
            .document(auth.userId())
            .set(newUser)
            .await()

        prefs.saveUser(newUser.copy(idUser = auth.userId()))
    }

    suspend fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() = auth.signOut()

    suspend fun saveTheme(mode: Int) {
        prefs.saveThemeMode(mode)
    }

    private suspend fun updateLocalUserInfoFromNetwork() {

        val newUser = database.collection(USERS)
            .document(auth.userId())
            .get()
            .await()
            .toObject<User>()!!

        prefs.saveUser(newUser.copy(idUser = auth.userId()))
    }

    suspend fun getLocalUserInfo(): User {
        return prefs.loadUser()
    }
}
