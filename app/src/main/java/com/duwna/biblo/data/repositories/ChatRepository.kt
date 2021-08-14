package com.duwna.biblo.data.repositories

import android.net.Uri
import com.duwna.biblo.entities.database.DatabaseConstants.GROUPS
import com.duwna.biblo.entities.database.DatabaseConstants.MESSAGES
import com.duwna.biblo.data.UploadImageManager
import com.duwna.biblo.entities.database.Message
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.utils.userId
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class ChatRepository @Inject constructor(
    private val database: FirebaseFirestore,
    private val imageManager: UploadImageManager,
    private val auth: FirebaseAuth,
) {

    suspend fun insertMessage(idGroup: String, text: String, imgUri: Uri?) {
        val message = Message(auth.userId(), text, Date())

        val idMessage = database.collection(GROUPS)
            .document(idGroup)
            .collection(MESSAGES)
            .add(message)
            .await()
            .id

        imageManager.uploadOrNull(
            MESSAGES,
            idMessage,
            imgUri,
            UploadImageManager.Resolution.DEFAULT
        )?.let { imgUrl ->
            database.collection(GROUPS)
                .document(idGroup)
                .collection(MESSAGES)
                .document(idMessage)
                .update("imgUrl", imgUrl)
        }
    }

    suspend fun deleteMessage(idGroup: String, messageItem: MessageItem) {
        database.collection(GROUPS)
            .document(idGroup)
            .collection(MESSAGES)
            .document(messageItem.id)
            .delete()
            .await()

        messageItem.imgUrl?.let { imageManager.deleteImage(MESSAGES, messageItem.id) }
    }

    fun subscribeOnMessages(idGroup: String): Flow<List<Message>> = callbackFlow {

        val subscription = database.collection(GROUPS)
            .document(idGroup)
            .collection(MESSAGES)
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