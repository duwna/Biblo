package com.duwna.biblo.ui.group.chat

import android.net.Uri
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.repositories.ChatRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.Event
import com.duwna.biblo.ui.base.EventObserver
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.format
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
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
                    message.timestamp.format(),
                    message.imgUrl
                )
            }
            if (messageItems.isEmpty()) postUpdateState { copy(showNoMessagesText = true) }
            else postUpdateState { copy(messages = messageItems, showNoMessagesText = false) }
            hideLoading()
        }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(imgUri = uri) }
    }

    fun sendMessage(text: String) {
        if (currentState.imgUri == null && text.isBlank()) return
        launchSafety {
            repository.insertMessage(text, currentState.imgUri)
            postUpdateState { copy(imgUri = null, messageSentEvent = Event(Unit)) }
        }
    }

    fun deleteMessage(messageItem: MessageItem) {
        launchSafety { repository.deleteMessage(messageItem) }
    }
}

data class ChatState(
    val messages: List<MessageItem> = emptyList(),
    val imgUri: Uri? = null,
    val messageSentEvent: Event<Unit>? = null,
    val showNoMessagesText: Boolean = false
) : IViewModelState

class ChatViewModelFactory(private val groupItem: GroupItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(groupItem) as T
    }
}