package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.Group
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MemberItem
import com.duwna.biblo.utils.format
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class GroupsRepository : BaseRepository() {

    suspend fun loadGroupItems(): List<GroupItem> {

        var isFromCache: Boolean

        val groups = database.collection("groups")
//            .orderBy("lastUpdate", Query.Direction.ASCENDING)
            .whereArrayContains("usersIds", firebaseUserId)
            .get()
            .await()
            .apply { isFromCache = metadata.isFromCache }
            .documents
            .map { it!!.toObject<Group>()!!.apply { idGroup = it.id } }

        if (groups.isEmpty()) return emptyList()

        val userIds = groups.flatMap { it.usersIds }.toSet()

        val memberItems = userIds.map {
            loadMemberItem(it, isFromCache)
        }

        return groups.map { group ->
            GroupItem(
                group.idGroup,
                group.name,
                if (!isFromCache) getImageUrl("groups", group.idGroup) else null,
                group.currency,
                group.lastUpdate.format("HH:mm\ndd.MM"),
                group.usersIds.map { idUser -> memberItems.find { it.id == idUser }!! }
            )
        }
    }

    private suspend fun loadMemberItem(idUser: String, isFromCache: Boolean): MemberItem {
        val result = database.collection("users")
            .document(idUser)
            .get()
            .await()

        val name = result.getString("name")!!

        val avatarUrl = when {
            result.id == firebaseUserId -> auth.currentUser?.photoUrl?.toString()
            !isFromCache -> getImageUrl("users", result.id)
            else -> null
        }

        return MemberItem(result.id, name, avatarUrl)
    }

    suspend fun getUserInfo(): AddMemberItem {

        var isFromCache: Boolean

        val name = database.collection("users")
            .document(firebaseUserId)
            .get()
            .await()
            .apply { isFromCache = metadata.isFromCache }
            .getString("name")!!

        val avatarUri = if (!isFromCache) getImageUrl("users", firebaseUserId)
            ?.let { Uri.parse(it) } else null
        return AddMemberItem(name, avatarUri)
    }

    suspend fun searchMember(email: String): AddMemberItem? {
        val result = database.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
            .documents
            .also { if (it.isEmpty()) return null }
            .get(0)

        val id = result.id
        val avatarUri = getImageUrl("users", id)?.let { Uri.parse(it) }
        return AddMemberItem(result.getString("name")!!, avatarUri, id)
    }

    suspend fun insertGroup(
        name: String,
        currency: String,
        avatarUri: Uri?,
        users: List<User>
    ) {

        val userIds = mutableListOf<String>().apply {
            users.forEachIndexed { index, user ->
                when {
                    // found user
                    user.idUser.isNotEmpty() -> {
                        add(user.idUser)
                    }
                    // create user
                    index != 0 -> {
                        val userId = createUser(user)
                        add(userId)
                        user.avatarUri?.let { uploadImg("users", userId, it) }
                    }
                    // current user
                    index == 0 -> {
                        add(firebaseUserId)
                    }
                }
            }
        }

        val group = Group(name, currency, userIds, Date())

        val idGroup = database.collection("groups")
            .add(group)
            .await()
            .id

        avatarUri?.let { uploadImg("groups", idGroup, it) }
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

