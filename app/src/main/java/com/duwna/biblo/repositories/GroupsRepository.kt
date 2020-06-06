package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.Group
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MemberItem
import com.duwna.biblo.utils.format
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class GroupsRepository : BaseRepository() {

    suspend fun loadGroupItems(): List<GroupItem> {
        val groups = database.collection("groups")
            .whereArrayContains("usersIds", firebaseUserId)
            .get()
            .await()
            .documents
            .map { it!!.toObject<Group>()!!.apply { idGroup = it.id } }

        if (groups.isEmpty()) return emptyList()

        val userIds = groups.flatMap { it.usersIds }.toSet().toList()

        val memberItems = mutableListOf<MemberItem>().apply {
            database.collection("users")
                .whereIn(FieldPath.documentId(), userIds)
                .get()
                .await()
                .forEach {
                    val name = it.getString("name")!!
                    val avatarUrl = getImageUrl("users", it.id)
                    add(MemberItem(it.id, name, avatarUrl))
                }
        }

        return groups.map { group ->
            GroupItem(
                group.idGroup,
                group.name,
                getImageUrl("groups", group.idGroup),
                group.currency,
                group.lastUpdate.format("HH:mm\ndd.MM"),
                group.usersIds.map { idUser -> memberItems.find { it.id == idUser }!! }
            )
        }
    }

    suspend fun getUserInfo(): AddMemberItem {
        val name = database.collection("users")
            .document(firebaseUserId)
            .get()
            .await()
            .getString("name")!!

        val avatarUri = getImageUrl("users", firebaseUserId)?.let { Uri.parse(it) }
        return AddMemberItem(name, avatarUri)
    }


    suspend fun insertGroup(
        name: String,
        currency: String,
        avatarUri: Uri?,
        users: List<User>
    ) {

        val userIds = mutableListOf<String>().apply {
            users.forEachIndexed { index, user ->
                if (index != 0) {
                    // create user
                    val userId = createUser(user)
                    add(userId)
                    user.avatarUri?.let { addAvatar("users", userId, it) }
                } else {
                    // current user
                    add(firebaseUserId)
                }
            }
        }

        val group = Group(name, currency, userIds, Date())

        val idGroup = database.collection("groups")
            .add(group)
            .await()
            .id

        avatarUri?.let { addAvatar("groups", idGroup, it) }
    }

    private suspend fun createUser(user: User): String {
        return database.collection("users")
            .add(user)
            .await()
            .id
    }

    fun signOut() {
        auth.signOut()
    }
}

