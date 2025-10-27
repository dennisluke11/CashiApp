package com.cashi.shared.domain.usecase

import com.cashi.shared.data.model.*
import com.cashi.shared.domain.repository.PaymentRepository
import com.cashi.shared.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class SendPaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(paymentRequest: PaymentRequest): Result<PaymentResult> {
        // Validate payment request
        val validation = validatePaymentRequest(paymentRequest)
        if (!validation.isValid) {
            return Result.failure(Exception("Validation failed: ${validation.errors.joinToString(", ")}"))
        }
        
        // Send payment via API
        val apiResult = paymentRepository.sendPayment(paymentRequest)
        return apiResult.fold(
            onSuccess = { response ->
                if (response.success) {
                    // Create transaction record
                    val transaction = Transaction(
                        id = response.transactionId ?: generateTransactionId(),
                        recipientEmail = paymentRequest.recipientEmail,
                        amount = paymentRequest.amount,
                        currency = paymentRequest.currency,
                        timestamp = Clock.System.now(),
                        status = PaymentStatus.COMPLETED,
                        description = "Payment sent to ${paymentRequest.recipientEmail}"
                    )
                    
                    // Save to Firestore
                    val saveResult = transactionRepository.saveTransaction(transaction)
                    saveResult.fold(
                        onSuccess = { 
                            Result.success(PaymentResult.Success(response.message, transaction))
                        },
                        onFailure = { error ->
                            Result.failure(Exception("Payment processed but failed to save transaction: ${error.message}"))
                        }
                    )
                } else {
                    Result.failure(Exception(response.message))
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
    
    private fun validatePaymentRequest(paymentRequest: PaymentRequest): PaymentValidationResult {
        val errors = mutableListOf<String>()
        
        // Validate email
        if (!isValidEmail(paymentRequest.recipientEmail)) {
            errors.add("Invalid email format")
        }
        
        // Validate amount
        if (paymentRequest.amount <= 0) {
            errors.add("Amount must be greater than 0")
        }
        
        // Validate currency
        val supportedCurrencies = Currency.values().map { it.code }
        if (!supportedCurrencies.contains(paymentRequest.currency)) {
            errors.add("Unsupported currency. Supported: ${supportedCurrencies.joinToString(", ")}")
        }
        
        return PaymentValidationResult(
            isValid = errors.isEmpty(),
            errors = errors
        )
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
    
    private fun generateTransactionId(): String {
        return "txn_${kotlinx.datetime.Clock.System.now().epochSeconds}_${(1000..9999).random()}"
    }
}

class GetTransactionsUseCase(
    private val transactionRepository: TransactionRepository
) {
    operator fun invoke(): Flow<List<Transaction>> {
        return transactionRepository.getTransactions()
    }
}

class GetTransactionByIdUseCase(
    private val transactionRepository: TransactionRepository
) {
    suspend operator fun invoke(id: String): Result<Transaction?> {
        return transactionRepository.getTransactionById(id)
    }
}

sealed class PaymentResult {
    data class Success(val message: String, val transaction: Transaction) : PaymentResult()
    data class Error(val message: String) : PaymentResult()
}
