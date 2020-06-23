package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.Group
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.GroupMemberItem
import com.duwna.biblo.utils.format
import com.duwna.biblo.utils.tryOrNull
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class GroupsRepository : BaseRepository() {

    suspend fun loadGroupItems(): List<GroupItem> {


        val groups = database.collection("groups")
            .orderBy("lastUpdate", Query.Direction.ASCENDING)
            .whereArrayContains("usersIds", firebaseUserId)
            .get()
            .await()
            .documents
            .map { it!!.toObject<Group>()!!.apply { idGroup = it.id } }

        if (groups.isEmpty()) return emptyList()

        val userIds = groups.flatMap { it.usersIds }.toSet()

        val memberItems = userIds.map {
            loadMemberItem(it)
        }

        return groups.map { group ->
            GroupItem(
                group.idGroup,
                group.name,
                group.avatarUrl,
                group.currency,
                group.lastUpdate.format("HH:mm\ndd.MM"),
                group.usersIds.map { idUser -> memberItems.find { it.id == idUser }!! }
            )
        }
    }

    private suspend fun loadMemberItem(idUser: String): GroupMemberItem {
        val result = database.collection("users")
            .document(idUser)
            .get()
            .await()

        val user = result.toObject<User>()!!

        val url = when {
            user.avatarUrl != null -> user.avatarUrl
            firebaseUserId == result.id -> auth.currentUser?.photoUrl?.toString()
            else -> null
        }

        return GroupMemberItem(result.id, user.name, url)
    }

    suspend fun getUserInfo(): AddMemberItem {
        val user = loadMemberItem(firebaseUserId)

        return AddMemberItem(
            user.name,
            tryOrNull { Uri.parse(user.avatarUrl) } ?: auth.currentUser?.photoUrl,
            firebaseUserId
        )
    }

    suspend fun searchMember(email: String): AddMemberItem? {
        val user = database.collection("users")
            .whereEqualTo("email", email)
            .get()
            .await()
            .documents
            .also { if (it.isEmpty()) return null }
            .get(0)
            .toObject<User>() ?: return null


        return AddMemberItem(user.name, tryOrNull { Uri.parse(user.avatarUrl) }, user.idUser)
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
                    //found user
                    user.idUser != null -> add(user.idUser!!)
                    //new user
                    index != 0 -> add(createUser(user))
                    // current user
                    index == 0 -> add(firebaseUserId)
                }
            }
        }

        val group = Group(name, currency, userIds, Date())

        val idGroup = database.collection("groups")
            .add(group)
            .await()
            .id

        avatarUri?.let {
            val avatarUrl = uploadImg("groups", idGroup, it)
            database.collection("groups").document(idGroup).update("avatarUrl", avatarUrl)
        }
    }

    private suspend fun createUser(user: User): String {

        val idUser = database.collection("users")
            .add(user)
            .await()
            .id

        user.avatarUri?.let {
            val avatarUrl = uploadImg("users", idUser, it)
            database.collection("users").document(idUser).update("avatarUrl", avatarUrl)
        }

        return idUser
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun deleteGroup(id: String) {
        database.collection("groups")
            .document(id)
            .delete()
            .await()
    }
}

