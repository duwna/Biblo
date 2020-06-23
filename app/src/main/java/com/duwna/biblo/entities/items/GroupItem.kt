package com.duwna.biblo.entities.items

import java.io.Serializable

data class GroupItem(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val currency: String,
    val lastUpdate: String,
    val members: List<GroupMemberItem>
) : Serializable

data class GroupMemberItem(
    val id: String,
    val name: String,
    val avatarUrl: String?
) : Serializable