package com.cashi.shared.domain.repository

import com.cashi.shared.data.local.FirebaseFirestoreClient
import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse
import com.cashi.shared.data.model.Transaction
import com.cashi.shared.data.remote.PaymentApiClient
import kotlinx.coroutines.flow.Flow

class PaymentRepositoryImpl(
    private val paymentApiClient: PaymentApiClient
) : PaymentRepository {
    
    override suspend fun sendPayment(paymentRequest: PaymentRequest): Result<PaymentResponse> {
        return paymentApiClient.sendPayment(paymentRequest)
    }
    
    override suspend fun validatePayment(paymentRequest: PaymentRequest): Result<Boolean> {
        return paymentApiClient.validatePayment(paymentRequest)
    }
}

class TransactionRepositoryImpl(
    private val firestoreClient: FirebaseFirestoreClient
) : TransactionRepository {
    
    override suspend fun saveTransaction(transaction: Transaction): Result<Unit> {
        return firestoreClient.saveTransaction(transaction)
    }
    
    override fun getTransactions(): Flow<List<Transaction>> {
        return firestoreClient.getTransactions()
    }
    
    override suspend fun getTransactionById(id: String): Result<Transaction?> {
        return firestoreClient.getTransactionById(id)
    }
    
    override suspend fun deleteTransaction(id: String): Result<Unit> {
        return firestoreClient.deleteTransaction(id)
    }
    
    override suspend fun updateTransactionStatus(id: String, status: String): Result<Unit> {
        return firestoreClient.updateTransactionStatus(id, status)
    }
}
