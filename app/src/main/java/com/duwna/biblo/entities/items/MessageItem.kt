package com.duwna.biblo.entities.items

data class MessageItem(
    var id: String,
    val name: String,
    val avatarUrl: String?,
    val text: String,
    val timestamp: String,
    val imgUrl: String? = null
)