package com.duwna.biblo.data.repositories

import android.net.Uri
import com.duwna.biblo.data.DatabaseConstants.GROUPS
import com.duwna.biblo.data.DatabaseConstants.USERS
import com.duwna.biblo.entities.database.Group
import com.duwna.biblo.entities.database.User
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.GroupMemberItem
import com.duwna.biblo.utils.format
import com.duwna.biblo.utils.tryOrNull
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class GroupsRepository : BaseRepository() {

    override val reference = database.collection(GROUPS)

    suspend fun loadGroupItems(): List<GroupItem> {

        val groups = reference
            .orderBy("lastUpdate", Query.Direction.DESCENDING)
            .whereArrayContains("usersIds", firebaseUserId)
            .get()
            .await()
            .documents
            .map { it!!.toObject<Group>()!!.apply { idGroup = it.id } }

        if (groups.isEmpty()) return emptyList()

        val userIds = groups.flatMap { it.usersIds }.distinct()

        val memberItems = loadMemberItems(userIds)

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

    private suspend fun loadMemberItems(userIds: List<String>): List<GroupMemberItem> {

        val users = mutableListOf<GroupMemberItem>()

        // only ten ids can be in one query
        for (i in userIds.indices step 10) {

            val userIdsPart = userIds.drop(i).take(10)

            val loadUsers = database.collection(USERS)
                .whereIn(FieldPath.documentId(), userIdsPart)
                .get()
                .await()
                .map {
                    val user = it.toObject<User>().apply { idUser = it.id }
                    GroupMemberItem(user.idUser!!, user.name, user.avatarUrl)
                }

            users.addAll(loadUsers)
        }
        return users
    }

    suspend fun searchMember(email: String): AddMemberItem? {
        val user = database.collection(USERS)
            .whereEqualTo("email", email)
            .get()
            .await()
            .documents
            .also { if (it.isEmpty()) return null }
            .get(0)
            .run { toObject<User>()?.apply { idUser = id } } ?: return null

        return AddMemberItem(user.name, tryOrNull { Uri.parse(user.avatarUrl) }, user.idUser)
    }

    suspend fun insertGroup(
        name: String,
        currency: String,
        avatarUri: Uri?,
        users: List<User>,
        groupItem: GroupItem?
    ) {
        if (groupItem == null) require(users[0].idUser == firebaseUserId)
        val userIds = mutableListOf<String>().apply {
            users.forEachIndexed { index, user ->
                when {
                    // current user && create new group
                    index == 0 && groupItem == null -> add(firebaseUserId)
                    //found user
                    user.idUser != null -> add(user.idUser!!)
                    //new user
                    else -> add(createUser(user))
                }
            }
        }

        val group = Group(name, currency, userIds, Date(), groupItem?.avatarUrl)

        val idGroup = if (groupItem == null) {
            // if new group
            reference.add(group)
                .await()
                .id
        } else {
            // if update existing group
            reference.document(groupItem.id)
                .set(group)
                .await()
            groupItem.id
        }

        avatarUri?.let {
            val avatarUrl = uploadImage(GROUPS, idGroup, it)
            reference.document(idGroup).update("avatarUrl", avatarUrl)
        }
    }


    private suspend fun createUser(user: User): String {

        val idUser = database.collection(USERS)
            .add(user)
            .await()
            .id

        user.avatarUri?.let {
            val avatarUrl = uploadImage(USERS, idUser, it)
            database.collection(USERS).document(idUser).update("avatarUrl", avatarUrl)
        }

        return idUser
    }
}

