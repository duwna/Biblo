package com.duwna.biblo.ui.group.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.data.repositories.ChatRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.shortFormat
import kotlinx.coroutines.flow.collect

class ChatViewModel(private val groupItem: GroupItem) : BaseViewModel<ChatState>(ChatState()) {

    private val repository = ChatRepository(groupItem.id)

    init {
        launchSafety { subscribeOnMessagesList() }
    }

    private suspend fun subscribeOnMessagesList() {
        showLoading()
        repository.subscribeOnMessages().collect { messages ->
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
            if (messageItems.isEmpty()) postUpdateState { copy(messages = emptyList(), showNoMessagesText = true) }
            else postUpdateState { copy(messages = messageItems, showNoMessagesText = false) }
            hideLoading()
        }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(imageUri = uri) }
    }

    fun sendMessage(text: String) {
        if (currentState.imageUri == null && text.isBlank()) return
        val uri = currentState.imageUri
        setImageUri(null)
        launchSafety {
            repository.insertMessage(text, uri)
            postUpdateState { copy(imageUri = null, onMessageSent = Event(Unit)) }
        }
    }

    fun deleteMessage(messageItem: MessageItem) {
        launchSafety { repository.deleteMessage(messageItem) }
    }
}

data class ChatState(
    val messages: List<MessageItem> = emptyList(),
    val imageUri: Uri? = null,
    val onMessageSent: Event<Unit>? = null,
    val showNoMessagesText: Boolean = false
) : IViewModelState

class ChatViewModelFactory(private val groupItem: GroupItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(groupItem) as T
    }
}