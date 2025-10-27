package com.cashi.shared.data.remote

import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse

class PaymentApiClient(private val apiService: PaymentApiService) {
    
    suspend fun sendPayment(paymentRequest: PaymentRequest): Result<PaymentResponse> {
        return try {
            val response = apiService.sendPayment(paymentRequest)
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun validatePayment(paymentRequest: PaymentRequest): Result<Boolean> {
        return try {
            val isValid = apiService.validatePayment(paymentRequest)
            Result.success(isValid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}