package com.duwna.biblo.entities.items


sealed class BillsViewItem {
    data class Bill(
        val id: String,
        val title: String,
        val description: String,
        val timestamp: String,
        val payers: List<BillMemberItem>,
        val debtors: List<BillMemberItem>
    ) : BillsViewItem()

    data class Header(
        val name: String,
        val currency: String,
        val avatarUrl: String?,
        val members: List<BillMemberItem>
    ) : BillsViewItem()

    //data class Refund()
}


data class BillMemberItem(
    val id: String,
    val name: String,
    val avatarUrl: String?,
    val sum: Double
)
