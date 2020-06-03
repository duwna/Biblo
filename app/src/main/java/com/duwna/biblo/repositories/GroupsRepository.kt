package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.base.BaseRepository
import com.duwna.biblo.models.database.Group
import com.duwna.biblo.models.database.User
import com.duwna.biblo.models.items.GroupItem
import com.duwna.biblo.utils.format
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class GroupsRepository : BaseRepository() {

    suspend fun loadGroupItems(): List<GroupItem> {

        if (auth.currentUser == null) throw NoAuthException()

        val groups = database.collection("groups")
//            .whereArrayContains("users", firebaseUserId)
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
                getImageUrl(it.idGroup),
                it.currency,
                it.lastUpdate.format(),
                it.usersIds.map { idUser -> userNames[idUser]!! }
            )
        }
    }

    suspend fun insertGroup(
        name: String,
        currency: String,
        avatarUri: Uri?,
        users: List<User>
    ) {

        val userIds = mutableListOf<String>().apply {
            users.forEach { user ->
                val userId = insertUser(user)
                add(userId)
                user.avatarUri?.let { addUserAvatar(userId, it) }
            }
        }

        val group = Group(name, currency, userIds, Date())

        val idGroup = database.collection("groups")
            .add(group)
            .await()
            .id

        avatarUri?.let { addGroupAvatar(idGroup, it) }
    }

    private suspend fun insertUser(user: User): String {
        return database.collection("users")
            .add(user)
            .await()
            .id
    }

    private suspend fun getImageUrl(path: String): String? = try {
        storage.child("group_avatars")
            .child(path)
            .downloadUrl
            .await()
            .toString()
    } catch (t: Throwable) {
        null
    }

    private suspend fun addGroupAvatar(idGroup: String, avatarUri: Uri) {
        storage.child("group_avatars")
            .child(idGroup)
            .putFile(avatarUri)
            .await()
    }

    private suspend fun addUserAvatar(idUser: String, avatarUri: Uri) {
        storage.child("user_avatars")
            .child(idUser)
            .putFile(avatarUri)
            .await()
    }
}

