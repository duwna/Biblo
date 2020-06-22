package com.duwna.biblo.ui.group.bills

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.BillsViewItem
import com.duwna.biblo.entities.items.BillsViewItem.Bill
import com.duwna.biblo.entities.items.BillsViewItem.Header
import com.duwna.biblo.ui.custom.MemberView
import com.duwna.biblo.utils.format
import com.duwna.biblo.utils.toInitials
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_bill.view.*
import kotlinx.android.synthetic.main.item_bill_header.view.*

class BillsAdapter(private val onItemClicked: (Bill) -> Unit) :
    ListAdapter<BillsViewItem, BillViewHolder>(BillsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val containerView = when (viewType) {
            0 -> layoutInflater.inflate(R.layout.item_bill, parent, false)
            else -> layoutInflater.inflate(R.layout.item_bill_header, parent, false)
        }
        return BillViewHolder(containerView)
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is Bill -> 0
        is Header -> 1
    }

    override fun onBindViewHolder(holder: BillViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is Bill -> holder.bindBill(item, onItemClicked)
            is Header -> holder.bindHeader(item)
        }
    }

}

class BillsDiffCallback : DiffUtil.ItemCallback<BillsViewItem>() {
    override fun areItemsTheSame(oldItem: BillsViewItem, newItem: BillsViewItem): Boolean {
        return when {
            oldItem is Bill && newItem is Bill -> oldItem.id == newItem.id
            else -> true
        }
    }

    override fun areContentsTheSame(oldItem: BillsViewItem, newItem: BillsViewItem): Boolean {
        return oldItem == newItem
    }
}

class BillViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bindBill(
        item: Bill,
        onItemClicked: (Bill) -> Unit
    ) = itemView.run {

        tv_title.text = item.title
        tv_timestamp.text = item.timestamp
        tv_description.text = item.description

        flexbox_payers.removeAllViews()
        flexbox_debtors.removeAllViews()

        item.payers.forEach {
            val memberView = MemberView(context, "${it.name}  |  ${it.sum.format()}", it.avatarUrl)
            flexbox_payers.addView(memberView)
        }

        item.debtors.forEach {
            val memberView = MemberView(context, "${it.name}  |  ${it.sum.format()}", it.avatarUrl)
            flexbox_debtors.addView(memberView)
        }

        setOnLongClickListener {
            Snackbar.make(this, "Удаление чека \"${item.title}\"", Snackbar.LENGTH_SHORT)
                .setAction("Удалить") { onItemClicked(item) }
                .show()
            true
        }
    }

    fun bindHeader(item: Header) = itemView.run {

        if (item.avatarUrl == null) {
            iv_avatar.isAvatarMode = false
            iv_avatar.setInitials(item.name.toInitials())
        } else {
            iv_avatar.isAvatarMode = true
            Glide.with(context)
                .load(item.avatarUrl)
                .into(iv_avatar)
        }

        tv_currency.text = item.currency

        flexbox_header_payers.removeAllViews()
        flexbox_header_debtors.removeAllViews()

        item.members.forEach {
            val memberView = MemberView(context, "${it.name}  |  ${it.sum.format()}", it.avatarUrl)
            if (it.sum >= 0) flexbox_header_payers.addView(memberView)
            else flexbox_header_debtors.addView(memberView)
        }
    }

}