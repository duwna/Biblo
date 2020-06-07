package com.duwna.biblo.repositories

import com.duwna.biblo.entities.database.Bill
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import java.util.*

class BillsRepository : BaseRepository() {

    suspend fun insertBill(idGroup: String, bill: Bill) {
        database.collection("groups")
            .document(idGroup)
            .collection("bills")
            .add(bill)
            .await()

        database.collection("groups")
            .document(idGroup)
            .update("lastUpdate", Date())
            .await()
    }

    suspend fun loadBills(idGroup: String): List<Bill> {
        return database.collection("groups")
            .document(idGroup)
            .collection("bills")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .get()
            .await()
            .documents
            .map { it.toObject<Bill>()!!.apply { idBill = it.id } }
    }
}