package com.cashi.shared.data.model

import kotlinx.serialization.Serializable
import kotlinx.datetime.Instant

@Serializable
data class PaymentRequest(
    val recipientEmail: String,
    val amount: Double,
    val currency: String
)

@Serializable
data class PaymentResponse(
    val success: Boolean,
    val message: String,
    val transactionId: String? = null,
    val timestamp: String? = null
)

@Serializable
data class Payment(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val timestamp: Instant,
    val status: PaymentStatus
)

@Serializable
enum class PaymentStatus {
    PENDING,
    COMPLETED,
    FAILED
}

@Serializable
data class Transaction(
    val id: String,
    val recipientEmail: String,
    val amount: Double,
    val currency: String,
    val timestamp: Instant,
    val status: PaymentStatus,
    val description: String? = null
)

@Serializable
enum class Currency(val code: String, val symbol: String) {
    USD("USD", "$"),
    EUR("EUR", "€"),
    GBP("GBP", "£")
}

@Serializable
data class PaymentValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList()
)
