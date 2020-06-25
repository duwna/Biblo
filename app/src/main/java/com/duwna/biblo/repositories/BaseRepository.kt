package com.duwna.biblo.repositories

import android.graphics.Bitmap
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await


abstract class BaseRepository {

    fun userExists() = auth.currentUser != null

    protected val auth = FirebaseAuth.getInstance()

    protected val firebaseUserId
        get() = auth.currentUser!!.uid

    protected val database = Firebase.firestore.apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    abstract val reference: CollectionReference

    private val storage = Firebase.storage.reference

    protected suspend fun uploadImg(path: String, name: String, imgUri: Uri): String {
        val ref = storage.child(path)
            .child(name)
        
        ref.putFile(imgUri).await()

        return ref.downloadUrl.await().toString()
    }
}


