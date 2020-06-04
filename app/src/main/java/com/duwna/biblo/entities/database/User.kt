package com.duwna.biblo.entities.database

import android.net.Uri
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
    @get:Exclude
    var idUser: String = "",
    val name: String = "",
    val email: String? = null,
    @get:Exclude
    var avatarUri: Uri? = null
)