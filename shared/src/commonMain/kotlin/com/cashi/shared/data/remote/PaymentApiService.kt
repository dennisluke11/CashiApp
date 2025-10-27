package com.cashi.shared.data.remote

import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse

expect class PaymentApiService {
    suspend fun sendPayment(paymentRequest: PaymentRequest): PaymentResponse
    suspend fun validatePayment(paymentRequest: PaymentRequest): Boolean
}

expect fun createPaymentApiService(): PaymentApiService

