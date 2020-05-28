package com.duwna.biblo.models.items

import android.net.Uri

data class AddMemberItem(
    val name: String,
    val avatarUri: Uri? = null
)