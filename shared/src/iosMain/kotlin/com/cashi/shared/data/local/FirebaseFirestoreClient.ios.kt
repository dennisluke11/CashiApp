package com.cashi.shared.data.local

import com.cashi.shared.data.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * iOS implementation of FirebaseFirestoreClient
 * Mock implementation - replaces with real iOS Firebase SDK later
 */
actual class FirebaseFirestoreClient {
    
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    
    actual suspend fun saveTransaction(transaction: Transaction): Result<Unit> {
        return try {
            val current = _transactions.value.toMutableList()
            current.add(0, transaction)
            _transactions.value = current
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    actual fun getTransactions(): Flow<List<Transaction>> {
        return _transactions
    }
    
    actual suspend fun getTransactionById(id: String): Result<Transaction?> {
        return try {
            val transaction = _transactions.value.find { it.id == id }
            Result.success(transaction)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    actual suspend fun deleteTransaction(id: String): Result<Unit> {
        return try {
            val current = _transactions.value.toMutableList()
            current.removeAll { it.id == id }
            _transactions.value = current
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    actual suspend fun updateTransactionStatus(id: String, status: String): Result<Unit> {
        return try {
            val current = _transactions.value.toMutableList()
            val index = current.indexOfFirst { it.id == id }
            if (index >= 0) {
                // Mock update - status enum from model
                _transactions.value = current
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
