package com.duwna.biblo.ui.group.chat

import android.app.Activity
import android.content.Intent
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.GroupItem
import com.duwna.biblo.ui.base.BaseFragment
import com.duwna.biblo.ui.base.IViewModelState
import com.duwna.biblo.utils.PICK_IMAGE_CODE
import com.duwna.biblo.utils.circularHide
import com.duwna.biblo.utils.pickImageFromGallery
import kotlinx.android.synthetic.main.fragment_chat.*

class ChatFragment : BaseFragment<ChatViewModel>() {

    private lateinit var groupItem: GroupItem
    override val layout: Int = R.layout.fragment_chat

    override val viewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(groupItem)
    }

    private val chatAdapter = ChatAdapter()

    override fun setupViews() {
        groupItem = arguments?.getSerializable("groupItem") as GroupItem

        rv_messages.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        iv_add_img.setOnClickListener {
            pickImageFromGallery()
        }

        iv_send.setOnClickListener {
            viewModel.sendMessage(et_message.text.toString())
            et_message.setText("")
        }
    }

    override fun bindState(state: IViewModelState) {
        state as ChatState

        when {
            state.isLoading -> wave_view.isVisible = true
            wave_view.isVisible && ViewCompat.isAttachedToWindow(wave_view) -> wave_view.circularHide()
            else -> wave_view.isVisible = false
        }
        state.imgUri?.let { Glide.with(this).load(it).into(iv_add_img) }

        chatAdapter.submitList(state.messages)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE) {
            viewModel.setImageUri(data?.data)
        }
    }

    companion object {
        fun newInstance(groupItem: GroupItem) = ChatFragment().apply {
            arguments = bundleOf(
                "groupItem" to groupItem
            )
        }
    }
}

