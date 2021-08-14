package com.duwna.biblo.ui.group.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.duwna.biblo.data.repositories.ChatRepository
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.shortFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    handle: SavedStateHandle
) : BaseViewModel<ChatState>(ChatState()) {

    private val groupItem = handle.get<GroupItem>("groupItem")!!

    init {
        launchSafety { subscribeOnMessagesList() }
    }

    private suspend fun subscribeOnMessagesList() {
        showLoading()
        repository.subscribeOnMessages(groupItem.id).collect { messages ->
            val messageItems = messages.map { message ->
                val member = groupItem.members.find { it.id == message.from }!!
                MessageItem(
                    message.id,
                    member.name,
                    member.avatarUrl,
                    message.text,
                    message.timestamp.shortFormat(),
                    message.imgUrl
                )
            }
            if (messageItems.isEmpty()) postUpdateState {
                copy(
                    messages = emptyList(),
                    showNoMessagesText = true
                )
            }
            else postUpdateState { copy(messages = messageItems, showNoMessagesText = false) }
            hideLoading()
        }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(imageUri = uri) }
    }

    fun sendMessage(text: String) {
        if (currentState.imageUri == null && text.isEmpty()) return
        val uri = currentState.imageUri
        setImageUri(null)
        launchSafety {
            repository.insertMessage(groupItem.id, text, uri)
            postUpdateState { copy(imageUri = null, onMessageSent = Event(Unit)) }
        }
    }

    fun deleteMessage(messageItem: MessageItem) {
        launchSafety { repository.deleteMessage(groupItem.id, messageItem) }
    }
}

data class ChatState(
    val messages: List<MessageItem> = emptyList(),
    val imageUri: Uri? = null,
    val onMessageSent: Event<Unit>? = null,
    val showNoMessagesText: Boolean = false
) : IViewModelState