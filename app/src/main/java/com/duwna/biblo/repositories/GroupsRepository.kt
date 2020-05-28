package com.duwna.biblo.repositories

import com.duwna.biblo.base.BaseRepository
import com.duwna.biblo.models.database.Group
import com.duwna.biblo.models.items.GroupItem
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class GroupsRepository : BaseRepository() {

    suspend fun loadGroupItems(): List<GroupItem> {

        val groups = database.collection("groups")
            .whereArrayContains("users", firebaseUserId)
            .get()
            .await()
            .documents
            .map { it!!.toObject<Group>()!!.apply { idGroup = it.id } }

        val userIds = groups.flatMap { it.usersIds }.toSet().toList()

        val userNames = mutableMapOf<String, String>().apply {
            database.collection("users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .await()
                .forEach { put(it.id, it.getString("name")!!) }
        }


        return groups.map {
            GroupItem(
                it.idGroup, it.name,
                "getImageUrl(it.idGroup)",
                it.currency,
                it.lastUpdate,
                userIds.map { idUser -> userNames[idUser]!! }
            )
        }
    }

    suspend fun getImageUrl(path: String): String? = try {
        storage.child(path)
            .downloadUrl
            .await()
            .toString()
    } catch (t: Throwable) {
        null
    }
}

