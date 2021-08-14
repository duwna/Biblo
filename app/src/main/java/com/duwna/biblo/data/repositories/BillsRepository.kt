package com.duwna.biblo.data.repositories

import com.duwna.biblo.entities.database.DatabaseConstants.BILLS
import com.duwna.biblo.entities.database.DatabaseConstants.GROUPS
import com.duwna.biblo.entities.database.Bill
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class BillsRepository @Inject constructor(
    private val database: FirebaseFirestore,
) {

    suspend fun insertBill(idGroup: String, bill: Bill) {
        database.collection(GROUPS)
            .document(idGroup)
            .collection(BILLS)
            .add(bill)
            .await()

        database.collection(GROUPS)
            .document(idGroup)
            .update("lastUpdate", Date())
            .await()
    }

    suspend fun deleteBill(idGroup: String, idBill: String) {
        database.collection(GROUPS)
            .document(idGroup)
            .collection(BILLS)
            .document(idBill)
            .delete()
            .await()
    }

    fun subscribeOnBills(idGroup: String): Flow<List<Bill>> = callbackFlow {

        val subscription = database.collection(GROUPS)
            .document(idGroup)
            .collection(BILLS)
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

    suspend fun deleteGroup(idGroup: String) {
        database.collection(GROUPS)
            .document(idGroup)
            .delete()
            .await()
    }
}