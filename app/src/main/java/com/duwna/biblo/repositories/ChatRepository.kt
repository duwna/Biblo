package com.duwna.biblo.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.Message
import com.duwna.biblo.entities.items.MessageItem
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class ChatRepository(idGroup: String) : BaseRepository() {

    override val reference = database.collection("groups")
        .document(idGroup)
        .collection("messages")

    suspend fun insertMessage(text: String, imgUri: Uri?) {
        val message = Message(firebaseUserId, text, Date())

        val id = reference.add(message)
            .await()
            .id

        imgUri?.let {
            val imgUrl = uploadImage("messages", id, it, Resolution.DEFAULT)
            reference.document(id).update("imgUrl", imgUrl)
        }
    }

    suspend fun deleteMessage(messageItem: MessageItem) {
        reference.document(messageItem.id)
            .delete()
            .await()

        messageItem.imgUrl?.let { deleteImage("messages", messageItem.id) }
    }

    @ExperimentalCoroutinesApi
    fun subscribeOnMessages(): Flow<List<Message>> = callbackFlow {

        val subscription = reference
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { querySnapshot, _ ->
                val list = querySnapshot?.documents?.map {
                    it.toObject<Message>()!!.apply { id = it.id }
                }!!
                trySend(list)
            }

        awaitClose {
            subscription.remove()
        }
    }
}