package com.duwna.biblo.entities.database

import java.util.*

data class Bill(
    val title: String = "",
    val description: String = "",
    val timestamp: Date = Date(),
    val payers: Map<String, Double> = mapOf(),
    val debtors: Map<String, Double> = mapOf()
)