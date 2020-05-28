package com.duwna.biblo.ui.groups

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duwna.biblo.R
import com.duwna.biblo.models.items.GroupItem
import com.google.android.material.chip.Chip
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_group.view.*

class GroupsAdapter(private val listener: (GroupItem) -> Unit) :
    ListAdapter<GroupItem, GroupViewHolder>(ArticleDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val containerView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false)
        return GroupViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position), listener)
    }

}

class ArticleDiffCallback : DiffUtil.ItemCallback<GroupItem>() {
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
        listener: (GroupItem) -> Unit
    ) = itemView.run {

        tv_name.text = item.name
        tv_last_updare.text = item.lastUpdate
        tv_currency.text = item.currency
        iv_avatar.setInitials(item.name.first().toString())
        chip_group.removeAllViews()

        item.members.forEach { member ->
            Chip(context).apply {
                text = member
                chipIcon = resources.getDrawable(R.drawable.ic_people, context.theme)
                isClickable = false
                isFocusable = false
            }.also { chip_group.addView(it) }
        }

        setOnClickListener { listener(item) }
    }
}