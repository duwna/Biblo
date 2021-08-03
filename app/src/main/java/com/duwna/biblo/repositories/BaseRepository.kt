package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.App
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.utils.FileUtil.toFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.resolution
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
        resolution: Resolution = Resolution.LOW
    ): String {

        val ctx = App.appContext
        val oldFile = imgUri.toFile(ctx)

        val newFile = Compressor.compress(ctx, oldFile) {
            when (resolution) {
                Resolution.LOW -> resolution(100, 100)
                Resolution.DEFAULT -> default()
            }
        }

        val ref = storage
            .child(path)
            .child(name)

        ref.putStream(newFile.inputStream()).await()

        return ref.downloadUrl.await().toString()
    }

    protected suspend fun deleteImage(path: String, name: String) {
        storage.child(path).child(name).delete().await()
    }

    enum class Resolution { LOW, DEFAULT }
}

