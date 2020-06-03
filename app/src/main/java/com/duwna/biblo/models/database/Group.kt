package com.duwna.biblo.models.database

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Group(
    val name: String = "",
    val currency: String = "",
    val usersIds: List<String> = emptyList(),
    val lastUpdate: Date = Date(),
    @get:Exclude
    var idGroup: String = ""
)