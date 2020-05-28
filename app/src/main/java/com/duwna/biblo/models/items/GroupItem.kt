package com.duwna.biblo.models.items

data class GroupItem(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val currency: String,
    val lastUpdate: String,
    val members: List<String>
)