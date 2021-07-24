package com.duwna.biblo.repositories

import com.duwna.biblo.entities.database.Bill
import com.duwna.biblo.entities.database.Message
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    fun subscribeOnBills(): Flow<List<Bill>> = callbackFlow {

        val subscription = reference
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, _ ->
                val list = querySnapshot?.documents?.map {
                    it.toObject<Bill>()!!.apply { idBill = it.id }
                }!!
                offer(list)
            }

        awaitClose {
            subscription.remove()
        }
    }
}