package com.duwna.biblo.entities.database

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Message(
    val from: String = "",
    val text: String = "",
    val timestamp: Date = Date(),
    val imgUrl: String? = null,
    @get:Exclude
    var id: String = ""
)