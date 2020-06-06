package com.duwna.biblo.entities.database

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Bill(
    val title: String = "",
    val description: String = "",
    val timestamp: Date = Date(),
    val payers: Map<String, Double> = mapOf(),
    val debtors: Map<String, Double> = mapOf(),
    @get:Exclude
    var idBill: String = ""
)