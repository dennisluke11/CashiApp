package com.cashi.shared.data.remote

import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse

// Actual implementation of the expect interface
actual interface PaymentApiService {
    actual suspend fun sendPayment(paymentRequest: PaymentRequest): PaymentResponse
    actual suspend fun validatePayment(paymentRequest: PaymentRequest): Boolean
}

// Actual implementation of the expect function
actual fun createPaymentApiService(): PaymentApiService {
    return object : PaymentApiService {
        override suspend fun sendPayment(paymentRequest: PaymentRequest): PaymentResponse {
            // Mock implementation
            return PaymentResponse(
                success = true,
                message = "Payment sent successfully",
                transactionId = "ios-${kotlinx.datetime.Clock.System.now().epochSeconds}"
            )
        }
        
        override suspend fun validatePayment(paymentRequest: PaymentRequest): Boolean {
            return paymentRequest.amount > 0 && paymentRequest.recipientEmail.isNotEmpty()
        }
    }
}

