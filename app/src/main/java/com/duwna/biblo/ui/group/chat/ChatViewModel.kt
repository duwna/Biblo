package com.duwna.biblo.ui.group.chat

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.repositories.ChatRepository
import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.base.Notify
import com.duwna.biblo.utils.format
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ChatViewModel(private val groupItem: GroupItem) : BaseViewModel<ChatState>(ChatState()) {

    private val repository = ChatRepository()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch(IO) {
            try {
                getMessageList()
            } catch (t: Throwable) {
                postUpdateState { copy(isLoading = false) }
                notify(Notify.Error())
            }
        }
    }

    private suspend fun getMessageList() {
        val messageItems = repository.getMessagesList(groupItem.id).map { message ->
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
        postUpdateState { copy(isLoading = false, messages = messageItems, imgUri = null) }
    }

    fun setImageUri(uri: Uri?) {
        updateState { copy(imgUri = uri) }
    }

    fun sendMessage(text: String) {
        if (currentState.imgUri == null && text.isBlank()) return
        viewModelScope.launch(IO) {
            try {
                repository.insertMessage(groupItem.id, text, currentState.imgUri)
                getMessageList()
            } catch (t: Throwable) {
                notify(Notify.Error())
            }
        }
    }

    fun deleteMessage(idMessage: String) {
        viewModelScope.launch(IO) {
            try {
                repository.deleteMessage(groupItem.id, idMessage)
                getMessageList()
            } catch (t: Throwable) {
                notify(Notify.Error())
            }
        }
    }

    fun disableScroll() {
        updateState { copy(hasScrolled = true) }
    }
}

data class ChatState(
    val isLoading: Boolean = true,
    val messages: List<MessageItem> = emptyList(),
    val imgUri: Uri? = null,
    val hasScrolled: Boolean = false
) : IViewModelState

class ChatViewModelFactory(private val groupItem: GroupItem) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(groupItem) as T
    }
}