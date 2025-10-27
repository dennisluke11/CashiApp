package com.cashi.shared.data.local

import com.cashi.shared.data.model.Transaction
import com.cashi.shared.data.model.PaymentStatus
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual class FirebaseFirestoreClient {
    private val firestore = FirebaseFirestore.getInstance()
    private val transactionsCollection = firestore.collection("transactions")
    
    actual suspend fun saveTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val data = hashMapOf(
                "id" to transaction.id,
                "recipientEmail" to transaction.recipientEmail,
                "amount" to transaction.amount,
                "currency" to transaction.currency,
                "timestamp" to transaction.timestamp.toString(),
                "status" to transaction.status.name,
                "description" to (transaction.description ?: "")
            )
            transactionsCollection.document(transaction.id)
                .set(data)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("Firebase", "Save failed: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    actual fun getTransactions(): Flow<List<Transaction>> = callbackFlow {
        val listener = transactionsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val transactions = snapshot.documents.mapNotNull { doc ->
                        try {
                            val data = doc.data
                            if (data != null) {
                                Transaction(
                                    id = data["id"] as? String ?: doc.id,
                                    recipientEmail = data["recipientEmail"] as? String ?: "",
                                    amount = (data["amount"] as? Number)?.toDouble() ?: 0.0,
                                    currency = data["currency"] as? String ?: "",
                                    timestamp = kotlinx.datetime.Instant.parse(data["timestamp"] as? String ?: ""),
                                    status = when (data["status"] as? String) {
                                        "COMPLETED" -> PaymentStatus.COMPLETED
                                        "PENDING" -> PaymentStatus.PENDING
                                        "FAILED" -> PaymentStatus.FAILED
                                        else -> PaymentStatus.COMPLETED
                                    },
                                    description = data["description"] as? String
                                )
                            } else null
                        } catch (e: Exception) {
                            android.util.Log.e("Firebase", "Error parsing transaction: ${e.message}")
                            null
                        }
                    }
                    trySend(transactions)
                }
            }
        
        awaitClose { listener.remove() }
    }
    
    actual suspend fun getTransactionById(id: String): Result<Transaction?> {
        return try {
            val document = transactionsCollection.document(id).get().await()
            if (document.exists()) {
                val transaction = document.toObject(Transaction::class.java)
                Result.success(transaction)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    actual suspend fun deleteTransaction(id: String): Result<Unit> {
        return try {
            transactionsCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    actual suspend fun updateTransactionStatus(id: String, status: String): Result<Unit> {
        return try {
            transactionsCollection.document(id)
                .update("status", status)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
