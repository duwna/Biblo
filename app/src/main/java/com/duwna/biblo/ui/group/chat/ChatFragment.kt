package com.duwna.biblo.ui.group.chat

import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.bumptech.glide.Glide
import com.duwna.biblo.ImageViewFragment
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


class ChatFragment : BaseFragment<ChatViewModel>() {

    override val layout: Int = R.layout.fragment_chat

    override val viewModel: ChatViewModel by viewModels {
        val groupItem = arguments?.getSerializable("groupItem") as GroupItem
        ChatViewModelFactory(groupItem)
    }

    private val chatAdapter: ChatAdapter by lazy {
        ChatAdapter(
            onItemLongClicked = { showDeleteMessageSnackbar(it) },
            onImageClicked = { showImageFragment(it) }
        )
    }

    private val linearSmoothScroller: LinearSmoothScroller by lazy {
        object : LinearSmoothScroller(context) {
            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 300f / displayMetrics.densityDpi
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentFragment?.setFragmentResultListener(ImageActionDialog.IMAGE_ACTIONS_KEY) { _, bundle ->
            val result = bundle[ImageActionDialog.SELECT_ACTION_KEY] as? String
            if (result == ImageActionDialog.DELETE_ACTION_KEY) viewModel.setImageUri(null)
            else viewModel.setImageUri(tryOrNull { Uri.parse(result) })
        }
    }

    override fun setupViews() {

        rv_messages.apply {
            layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
            adapter = chatAdapter
        }

        iv_add_img.setOnClickListener {
            val hasImage = viewModel.currentState.imageUri != null
            findNavController().showImageActionDialog(hasImage)
        }

        iv_send.setOnClickListener {
            viewModel.sendMessage(et_message.text.toString().trim())
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
        else iv_add_img.setImageResource(R.drawable.ic_baseline_insert_photo_24)

        chatAdapter.submitList(state.messages)

        state.onMessageSent?.setListener {
            // if list is not scrolled up -> slow scroll to end
            // else fast scroll
            val layoutManager = rv_messages.layoutManager as LinearLayoutManager
            if (chatAdapter.itemCount - layoutManager.findLastVisibleItemPosition() < 5) {
                linearSmoothScroller.targetPosition = chatAdapter.itemCount
                layoutManager.startSmoothScroll(linearSmoothScroller)
            } else {
                rv_messages.smoothScrollToPosition(chatAdapter.itemCount)
            }
            et_message.text.clear()
        }
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

    private fun showImageFragment(url: String) {
        findNavController().navigate(
            R.id.navigation_image_view,
            ImageViewFragment.args(url),
            navOptions {
                anim {
                    enter = R.anim.slide_from_right_to_center
                    exit = R.anim.slide_from_center_to_left
                    popEnter = R.anim.slide_from_left_to_center
                    popExit = R.anim.slide_from_center_to_right
                }
            }
        )
    }

    companion object {
        fun newInstance(groupItem: GroupItem) = ChatFragment().apply {
            arguments = bundleOf("groupItem" to groupItem)
        }
    }
}


