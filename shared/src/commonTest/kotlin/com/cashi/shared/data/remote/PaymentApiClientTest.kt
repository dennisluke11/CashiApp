package com.cashi.shared.data.remote

import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import retrofit2.Response
import kotlinx.coroutines.test.runTest

class PaymentApiClientTest : DescribeSpec({
    
    describe("PaymentApiClient") {
        val mockApiService = mockk<PaymentApiService>()
        val apiClient = PaymentApiClient(mockApiService)
        
        beforeEach {
            clearAllMocks()
        }
        
        describe("when sending payment") {
            it("should return success response for valid payment") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val expectedResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_123",
                    timestamp = "2023-01-01T00:00:00Z"
                )
                
                val retrofitResponse = Response.success(expectedResponse)
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
                result.getOrNull() shouldBe expectedResponse
            }
            
            it("should return failure for API error") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val retrofitResponse = Response.error<PaymentResponse>(400, okhttp3.ResponseBody.create(null, "Bad Request"))
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldBe "Payment failed with status: 400 - Bad Request"
            }
            
            it("should handle network exceptions") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val networkException = Exception("Network error")
                coEvery { mockApiService.sendPayment(paymentRequest) } throws networkException
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                result.exceptionOrNull() shouldBe networkException
            }
        }
        
        describe("when validating payment") {
            it("should return true for valid payment") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val retrofitResponse = Response.success(true)
                coEvery { mockApiService.validatePayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.validatePayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
                result.getOrNull() shouldBe true
            }
            
            it("should return false for invalid payment") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "invalid-email",
                    amount = -100.0,
                    currency = "INVALID"
                )
                
                val retrofitResponse = Response.success(false)
                coEvery { mockApiService.validatePayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.validatePayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
                result.getOrNull() shouldBe false
            }
        }
    }
})
