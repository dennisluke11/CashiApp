package com.cashi.shared.data.local

import com.cashi.shared.data.model.Transaction
import kotlinx.coroutines.flow.Flow

expect class FirebaseFirestoreClient {
    suspend fun saveTransaction(transaction: Transaction): Result<Unit>
    fun getTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Result<Transaction?>
    suspend fun deleteTransaction(id: String): Result<Unit>
    suspend fun updateTransactionStatus(id: String, status: String): Result<Unit>
}
