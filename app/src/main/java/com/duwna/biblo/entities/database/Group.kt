package com.duwna.biblo.entities.database

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Group(
    val name: String = "",
    val currency: String = "",
    val usersIds: List<String> = emptyList(),
    val lastUpdate: Date = Date(),
    val avatarUrl: String? = null,
    @get:Exclude
    var idGroup: String = ""
)