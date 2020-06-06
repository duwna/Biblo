package com.duwna.biblo.ui.group.chat

import com.duwna.biblo.ui.base.BaseViewModel
import com.duwna.biblo.ui.base.IViewModelState

class ChatViewModel : BaseViewModel<ChatState>(ChatState()) {

}

data class ChatState(
    val isLoading: Boolean = true
) : IViewModelState