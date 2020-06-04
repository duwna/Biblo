package com.duwna.biblo.base

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

open class BaseRepository {

    protected val auth = FirebaseAuth.getInstance()

    protected val firebaseUserId
        get() = auth.currentUser!!.uid

    protected val database = Firebase.firestore.apply {
        firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build()
    }

    protected val storage = Firebase.storage.reference

    fun userExists() = auth.currentUser != null
}


