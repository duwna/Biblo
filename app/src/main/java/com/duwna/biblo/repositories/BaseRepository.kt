package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.utils.randomID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await

open class BaseRepository {

    fun userExists() = auth.currentUser != null

    protected val auth = FirebaseAuth.getInstance()

    protected val firebaseUserId
        get() = auth.currentUser!!.uid

    protected val database = Firebase.firestore.apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    private val storage = Firebase.storage.reference

    protected suspend fun uploadImg(path: String, name: String, avatarUri: Uri): String {
        val ref = storage.child(path)
            .child(name)

        ref.putFile(avatarUri).await()

        return ref.downloadUrl.await().toString()
    }

    protected suspend fun getImageUrl(path: String, name: String): String? = try {
        storage.child(path)
            .child(name)
            .downloadUrl
            .await()
            .toString()
    } catch (t: Throwable) {
        null
    }


}


