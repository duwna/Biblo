package com.duwna.biblo.models.database

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Group(
    @get:Exclude
    var idGroup: String = "",
    val name: String = "",
    val lastUpdate: String = "",
    val currency: String = "",
    val usersIds: List<String> = emptyList()
)
