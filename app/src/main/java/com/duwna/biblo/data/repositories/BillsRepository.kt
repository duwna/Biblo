package com.duwna.biblo.data.repositories

import com.duwna.biblo.data.DatabaseConstants.BILLS
import com.duwna.biblo.data.DatabaseConstants.GROUPS
import com.duwna.biblo.entities.database.Bill
import com.duwna.biblo.entities.items.GroupItem
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class BillsRepository(private val idGroup: String) : BaseRepository() {

    override val reference = database.collection(GROUPS)
        .document(idGroup)
        .collection(BILLS)

    suspend fun insertBill(bill: Bill) {
        reference.add(bill)
            .await()

        database.collection(GROUPS)
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
                trySend(list)
            }

        awaitClose {
            subscription.remove()
        }
    }

    suspend fun deleteGroup(groupItem: GroupItem) {
        database.collection(GROUPS)
            .document(groupItem.id)
            .delete()
            .await()
    }
}