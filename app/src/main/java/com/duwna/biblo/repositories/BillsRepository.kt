package com.duwna.biblo.repositories

import com.duwna.biblo.entities.database.Bill
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class BillsRepository(private val idGroup: String) : BaseRepository() {

    override val reference = database.collection("groups")
        .document(idGroup)
        .collection("bills")

    suspend fun insertBill(bill: Bill) {
        reference.add(bill)
            .await()

        database.collection("groups")
            .document(idGroup)
            .update("lastUpdate", Date())
            .await()
    }

    suspend fun deleteBill(idBill: String) {
        reference.document(idBill)
            .delete()
            .await()
    }

    suspend fun loadBills(): List<Bill> {
        return reference.orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .await()
            .documents
            .map { it.toObject<Bill>()!!.apply { idBill = it.id } }
    }
}