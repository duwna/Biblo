package com.duwna.biblo.entities.items

data class AddBillMemberItem(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val isChecked: Boolean = true,
    val sum: Double = 0.0
)
