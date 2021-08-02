package com.duwna.biblo.ui.group.chat

import android.Manifest
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ChatFragment : BaseFragment<ChatViewModel>() {

    private lateinit var groupItem: GroupItem
    override val layout: Int = R.layout.fragment_chat

    private val permissionResult = registerPermissionResult {
        imagePickResult.launch("image/*")
    }

    private val imagePickResult = registerImagePickResult { uri ->
        viewModel.setImageUri(uri)
    }

    override val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(groupItem)
    }

    private val chatAdapter = ChatAdapter(
        onItemLongClicked = { showDeleteMessageSnackbar(it) }
    )

    override fun setupViews() {
        groupItem = arguments?.getSerializable("groupItem") as GroupItem

        rv_messages.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = chatAdapter
        }

        iv_add_img.setOnClickListener {
            if (viewModel.currentState.imgUri != null) viewModel.setImageUri(null)
            else permissionResult.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        iv_send.setOnClickListener {
            viewModel.sendMessage(et_message.text.toString())
        }
    }

    override fun bindState(state: IViewModelState) {
        state as ChatState

        if (state.showNoMessagesText) {
            tv_no_messages.isVisible = true
            tv_no_messages.animate().alpha(1f).duration = 500
        } else {
            tv_no_messages.isVisible = false
            tv_no_messages.alpha = 0f
        }

        state.imgUri?.let { Glide.with(this).load(it).into(iv_add_img) }
            ?: run { iv_add_img.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24) }

        state.onMessageSent?.setListener {
            rv_messages.smoothScrollToPosition(chatAdapter.itemCount)
            et_message.text.clear()
        }

        chatAdapter.submitList(state.messages)
    }

    private fun showDeleteMessageSnackbar(messageItem: MessageItem) {
        Snackbar.make(
            requireView(), requireContext().getString(R.string.label_delete_message),
            Snackbar.LENGTH_SHORT
        ).apply {

            setAction(requireContext().getString(R.string.label_delete)) {
                viewModel.deleteMessage(messageItem)
            }

            anchorView = bottom_container
            show()
        }
    }

    companion object {
        fun newInstance(groupItem: GroupItem) = ChatFragment().apply {
            arguments = bundleOf("groupItem" to groupItem)
        }
    }
}

