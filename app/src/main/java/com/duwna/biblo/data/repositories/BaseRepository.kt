package com.duwna.biblo.data.repositories

import android.net.Uri
import com.duwna.biblo.data.CompressManager
import com.duwna.biblo.data.PrefManager
import com.duwna.biblo.entities.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await


abstract class BaseRepository {

    abstract val reference: CollectionReference
    protected val auth = FirebaseAuth.getInstance()
    private val storage = Firebase.storage.reference

    val firebaseUserId
        get() = auth.currentUser!!.uid

    fun userExists() = auth.currentUser != null

    suspend fun getLocalUserInfo(): User {
        return PrefManager.loadUser()
    }

    protected val database = Firebase.firestore.apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    protected suspend fun uploadImage(
        path: String,
        name: String,
        imgUri: Uri,
        resolution: CompressManager.Resolution = CompressManager.Resolution.LOW
    ): String {

        val compressedFile = CompressManager.compressImage(imgUri, resolution)

        val ref = storage
            .child(path)
            .child(name)

        ref.putStream(compressedFile.inputStream()).await()

        return ref.downloadUrl.await().toString()
    }

    protected suspend fun deleteImage(path: String, name: String) {
        storage.child(path).child(name).delete().await()
    }
}

