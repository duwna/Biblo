package com.duwna.biblo.ui.group.chat

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.ui.dialogs.ImageActionDialog
import com.duwna.biblo.ui.dialogs.ImageActionDialog.Companion.showImageActionDialog
import com.duwna.biblo.utils.tryOrNull
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ChatFragment : BaseFragment<ChatViewModel>() {

    private lateinit var groupItem: GroupItem
    override val layout: Int = R.layout.fragment_chat

    override val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(groupItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ImageActionDialog.IMAGE_ACTIONS_KEY) { _, bundle ->
            val result = bundle[ImageActionDialog.SELECT_ACTION_KEY] as? String
            if (result == ImageActionDialog.DELETE_ACTION_KEY) viewModel.setImageUri(null)
            else viewModel.setImageUri(tryOrNull { Uri.parse(result) })
        }
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
            val hasImage = viewModel.currentState.imageUri != null
            findNavController().showImageActionDialog(hasImage)
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

        if (state.imageUri != null) Glide.with(this).load(state.imageUri).into(iv_add_img)
        else iv_add_img.setImageResource(R.drawable.ic_baseline_add_photo_alternate_24)

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

