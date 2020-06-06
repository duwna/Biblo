package com.duwna.biblo.ui.group.bills.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duwna.biblo.R
import com.duwna.biblo.entities.items.AddBillMemberItem
import com.duwna.biblo.utils.format
import com.duwna.biblo.utils.hideKeyBoard
import com.duwna.biblo.utils.toInitials
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_add_bill.view.*

class AddBillAdapter(
    private val onCheckBoxClicked: (Int) -> Unit,
    private val onTextChanged: (Int, Double) -> Unit
) : ListAdapter<AddBillMemberItem, AddBillViewHolder>(AddBillMemberItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddBillViewHolder {
        val containerView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_add_bill, parent, false)
        return AddBillViewHolder(containerView)
    }

    override fun onBindViewHolder(holder: AddBillViewHolder, position: Int) {
        holder.bind(getItem(position), onCheckBoxClicked, onTextChanged)
    }

}

class AddBillMemberItemDiffCallback : DiffUtil.ItemCallback<AddBillMemberItem>() {
    override fun areItemsTheSame(oldItem: AddBillMemberItem, newItem: AddBillMemberItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: AddBillMemberItem,
        newItem: AddBillMemberItem
    ): Boolean {
        return oldItem.isChecked == newItem.isChecked
    }

    override fun getChangePayload(oldItem: AddBillMemberItem, newItem: AddBillMemberItem): Any? {
        return if (oldItem.isChecked != newItem.isChecked) Unit
        else super.getChangePayload(oldItem, newItem)
    }
}

class AddBillViewHolder(
    override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bind(
        item: AddBillMemberItem,
        onCheckBoxClicked: (Int) -> Unit,
        onTextChanged: (Int, Double) -> Unit
    ) = itemView.run {

        til_sum.hint = item.name

        if (item.avatarUrl != null) {
            iv_avatar.isAvatarMode = true
            Glide.with(this).load(item.avatarUrl).into(iv_avatar)
        } else {
            iv_avatar.isAvatarMode = false
            iv_avatar.setInitials(item.name.toInitials())
        }

        et_sum.doOnTextChanged { text, _, _, _ ->
            onTextChanged(
                adapterPosition,
                text.toString().toDoubleOrNull() ?: 0.0
            )
        }

        et_sum.isEnabled = item.isChecked
        et_sum.isFocusable = item.isChecked
        et_sum.isFocusableInTouchMode = item.isChecked

        if (item.sum != 0.0) et_sum.setText(item.sum.format())

        checkbox.isChecked = item.isChecked

        checkbox.setOnClickListener {
            onCheckBoxClicked(adapterPosition)
            et_sum.setText("")
            context.hideKeyBoard(this)
        }
    }
}