package com.cashi.shared.data.remote

import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Retrofit interface
interface PaymentApiServiceRetrofit {
    @POST("payments")
    suspend fun sendPayment(@Body paymentRequest: PaymentRequest): retrofit2.Response<PaymentResponse>
    
    @POST("validate")
    suspend fun validatePayment(@Body paymentRequest: PaymentRequest): retrofit2.Response<Boolean>
}

actual class PaymentApiService(private val retrofitService: PaymentApiServiceRetrofit) {
    actual suspend fun sendPayment(paymentRequest: PaymentRequest): PaymentResponse {
        return try {
            val response = retrofitService.sendPayment(paymentRequest)
            if (response.isSuccessful) {
                response.body() ?: PaymentResponse(
                    success = false,
                    message = "Empty response",
                    transactionId = null
                )
            } else {
                PaymentResponse(
                    success = false,
                    message = response.message(),
                    transactionId = null
                )
            }
        } catch (e: Exception) {
            PaymentResponse(
                success = false,
                message = e.message ?: "Error occurred",
                transactionId = null
            )
        }
    }
    
    actual suspend fun validatePayment(paymentRequest: PaymentRequest): Boolean {
        return try {
            val response = retrofitService.validatePayment(paymentRequest)
            response.isSuccessful && (response.body() == true)
        } catch (e: Exception) {
            false
        }
    }
}

actual fun createPaymentApiService(): PaymentApiService {
    val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.8.101:3000/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val retrofitService = retrofit.create(PaymentApiServiceRetrofit::class.java)
    
    return PaymentApiService(retrofitService)
}

