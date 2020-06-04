package com.duwna.biblo.ui.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.models.items.GroupItem
import com.duwna.biblo.ui.custom.MemberView
import com.duwna.biblo.utils.toInitials
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_group.view.*
import java.util.*

class GroupsAdapter(private val onItemClicked: (GroupItem) -> Unit) :
    ListAdapter<GroupItem, GroupViewHolder>(GroupsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val containerView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }

}

class GroupsDiffCallback : DiffUtil.ItemCallback<GroupItem>() {
    override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
        return oldItem == newItem
    }
}

class GroupViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(
        item: GroupItem,
        onItemClicked: (GroupItem) -> Unit
    ) = itemView.run {

        tv_name.text = item.name
        tv_last_update.text = item.lastUpdate
        tv_currency.text = item.currency

        if (item.avatarUrl == null) {
            iv_avatar.isAvatarMode = false
            iv_avatar.setInitials(item.name.toInitials())
        } else {
            iv_avatar.isAvatarMode = true
            Glide.with(context)
                .load(item.avatarUrl)
                .into(iv_avatar)
        }

//        tv_members.text = buildString {
//            item.members.forEach { append("$it, ") }
//            delete(length - 2, length - 1)
//        }

        flexbox.removeAllViews()
        item.members.forEach {
            val memberView = MemberView(
                it,
                if (Random().nextBoolean())
                "https://firebasestorage.googleapis.com/v0/b/biblo-6d48f.appspot.com/o/user_avatars%2F9Te9ZX74ndqUEL3Qu0kT?alt=media&token=a108b817-4559-4f2d-b0e2-b61db8547214"
                else null,
                context
            )
            flexbox.addView(memberView)
        }

        setOnClickListener { onItemClicked(item) }
    }
}