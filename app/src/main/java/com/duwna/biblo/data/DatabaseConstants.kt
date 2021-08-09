package com.duwna.biblo.data

import com.duwna.biblo.BuildConfig

object DatabaseConstants {
    val USERS = if (!BuildConfig.DEBUG) "users" else "users_debug"
    val GROUPS = if (!BuildConfig.DEBUG) "groups" else "groups_debug"
    val BILLS = if (!BuildConfig.DEBUG) "bills" else "bills_debug"
    val MESSAGES = if (!BuildConfig.DEBUG) "messages" else "messages_debug"
}