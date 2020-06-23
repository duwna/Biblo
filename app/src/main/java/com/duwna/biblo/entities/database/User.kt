package com.duwna.biblo.entities.database

import android.net.Uri
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    @get:Exclude
    var idUser: String? = null,
    val name: String = "",
    val email: String? = null,
    val avatarUrl: String? = null,
    @get:Exclude
    // for adding user avatar to storage
    var avatarUri: Uri? = null
)