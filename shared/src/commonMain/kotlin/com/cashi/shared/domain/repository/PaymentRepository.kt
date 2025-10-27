package com.cashi.shared.domain.repository

import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse
import com.cashi.shared.data.model.Transaction
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun sendPayment(paymentRequest: PaymentRequest): Result<PaymentResponse>
    suspend fun validatePayment(paymentRequest: PaymentRequest): Result<Boolean>
}

interface TransactionRepository {
    suspend fun saveTransaction(transaction: Transaction): Result<Unit>
    fun getTransactions(): Flow<List<Transaction>>
    suspend fun getTransactionById(id: String): Result<Transaction?>
    suspend fun deleteTransaction(id: String): Result<Unit>
    suspend fun updateTransactionStatus(id: String, status: String): Result<Unit>
}
