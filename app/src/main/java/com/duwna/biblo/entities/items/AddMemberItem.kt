package com.duwna.biblo.entities.items

import android.net.Uri

data class AddMemberItem(
    val name: String,
    val avatarUri: Uri? = null,
    val id: String? = null,
    val avatarUrl: String? = null
)