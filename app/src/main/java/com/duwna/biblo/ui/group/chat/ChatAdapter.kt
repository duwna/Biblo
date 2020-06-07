package com.duwna.biblo.ui.group.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.MessageItem
import com.duwna.biblo.utils.toInitials
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_message.view.*

class ChatAdapter : ListAdapter<MessageItem, MessageViewHolder>(MessagesDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val containerView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class MessagesDiffCallback : DiffUtil.ItemCallback<MessageItem>() {
    override fun areItemsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MessageItem, newItem: MessageItem): Boolean {
        return oldItem == newItem
    }
}

class MessageViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(
        item: MessageItem
    ) = itemView.run {

        tv_text.text = item.text
        tv_name.text = item.name
        tv_timestamp.text = item.timestamp

        if (item.avatarUrl != null) {
            iv_avatar.isAvatarMode = true
            Glide.with(this).load(item.avatarUrl).into(iv_avatar)
        } else {
            iv_avatar.isAvatarMode = false
            iv_avatar.setInitials(item.name.toInitials())
        }

        tv_text.isVisible = item.text.isNotEmpty()

        if (item.imgUrl != null) {
            iv_image.isVisible = true
            Glide.with(this).load(item.imgUrl).into(iv_image)
        } else {
            iv_image.isVisible = false
        }
    }
}