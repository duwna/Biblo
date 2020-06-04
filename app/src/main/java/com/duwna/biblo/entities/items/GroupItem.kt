package com.duwna.biblo.entities.items

data class GroupItem(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val currency: String,
    val lastUpdate: String,
    val members: List<MemberItem>
)

data class MemberItem(
    val name: String,
    val avatarUrl: String?
)