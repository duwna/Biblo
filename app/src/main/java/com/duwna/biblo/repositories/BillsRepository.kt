package com.duwna.biblo.repositories

import com.duwna.biblo.entities.database.Bill
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class BillsRepository : BaseRepository() {

    suspend fun insertBill(idGroup: String, bill: Bill) {
        database.collection("groups")
            .document(idGroup)
            .collection("bills")
            .add(bill)
            .await()
    }

    suspend fun loadBills(idGroup: String): List<Bill> {
        return database.collection("groups")
            .document(idGroup)
            .collection("bills")
            .get()
            .await()
            .documents
            .map { it.toObject<Bill>()!! }
    }
}