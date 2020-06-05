package com.duwna.biblo.entities.items

import java.io.Serializable

data class GroupItem(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val currency: String,
    val lastUpdate: String,
    val members: List<MemberItem>
) : Serializable

data class MemberItem(
    val name: String,
    val avatarUrl: String?
)