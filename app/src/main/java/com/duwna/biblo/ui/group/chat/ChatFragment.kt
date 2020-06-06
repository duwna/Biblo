package com.duwna.biblo.ui.group.chat

import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState

class ChatFragment : BaseFragment<ChatViewModel>() {

    private lateinit var groupItem: GroupItem

    override val viewModel: ChatViewModel by viewModels()
    override val layout: Int = R.layout.fragment_chat

    override fun setupViews() {
        groupItem = arguments?.getSerializable("groupItem") as GroupItem


    }

    override fun bindState(state: IViewModelState) {

    }

    companion object {
        fun newInstance(groupItem: GroupItem) = ChatFragment().apply {
            arguments = bundleOf(
                "groupItem" to groupItem
            )
        }
    }
}

