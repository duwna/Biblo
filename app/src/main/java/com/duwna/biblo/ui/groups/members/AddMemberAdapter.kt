package com.duwna.biblo.ui.groups.members

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.AddMemberItem
import com.duwna.biblo.utils.toInitials
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_member.view.*

class AddMemberAdapter(
    private val onRemoveClicked: (position: Int) -> Unit
) : ListAdapter<AddMemberItem, AddMemberViewHolder>(AddMemberDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddMemberViewHolder {
        val containerView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_member, parent, false)
        return AddMemberViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: AddMemberViewHolder, position: Int) {
        holder.bind(getItem(position), onRemoveClicked)
    }
}

class AddMemberDiffCallback : DiffUtil.ItemCallback<AddMemberItem>() {
    override fun areItemsTheSame(oldItem: AddMemberItem, newItem: AddMemberItem): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: AddMemberItem, newItem: AddMemberItem): Boolean {
        return oldItem == newItem
    }
}

class AddMemberViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(
        item: AddMemberItem,
        onRemoveClicked: (position: Int) -> Unit
    ) = itemView.run {

        tv_title.text = item.name

        if (item.avatarUri != null) {
            iv_avatar.isAvatarMode = true
            Glide.with(this).load(item.avatarUri).into(iv_avatar)
        } else {
            iv_avatar.isAvatarMode = false
            iv_avatar.setInitials(item.name.toInitials())
        }

        iv_delete.setOnClickListener { onRemoveClicked(adapterPosition) }
    }
}