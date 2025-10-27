package com.cashi.shared.data.remote

import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.data.model.PaymentResponse
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import retrofit2.Response
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

class ApiResponseParsingTest : DescribeSpec({
    
    describe("API Response Parsing") {
        val mockApiService = mockk<PaymentApiService>()
        val apiClient = PaymentApiClient(mockApiService)
        val json = Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
        
        beforeEach {
            clearAllMocks()
        }
        
        describe("Payment Response Parsing") {
            it("should parse successful payment response correctly") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val expectedResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_123456789",
                    timestamp = "2023-01-01T12:00:00Z"
                )
                
                val retrofitResponse = Response.success(expectedResponse)
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
                val response = result.getOrNull()
                response shouldNotBe null
                response!!.success shouldBe true
                response.message shouldBe "Payment processed successfully"
                response.transactionId shouldBe "txn_123456789"
                response.timestamp shouldBe "2023-01-01T12:00:00Z"
            }
            
            it("should parse failed payment response correctly") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val expectedResponse = PaymentResponse(
                    success = false,
                    message = "Insufficient funds",
                    transactionId = null,
                    timestamp = null
                )
                
                val retrofitResponse = Response.success(expectedResponse)
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
                val response = result.getOrNull()
                response shouldNotBe null
                response!!.success shouldBe false
                response.message shouldBe "Insufficient funds"
                response.transactionId shouldBe null
                response.timestamp shouldBe null
            }
            
            it("should handle HTTP error responses") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val retrofitResponse = Response.error<PaymentResponse>(
                    400, 
                    okhttp3.ResponseBody.create(null, "Bad Request")
                )
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                val exception = result.exceptionOrNull()
                exception shouldNotBe null
                exception!!.message shouldBe "Payment failed with status: 400 - Bad Request"
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
            
            it("should handle empty response body") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val retrofitResponse = Response.success<PaymentResponse>(null)
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                val exception = result.exceptionOrNull()
                exception shouldNotBe null
                exception!!.message shouldBe "Empty response body"
            }
        }
        
        describe("Validation Response Parsing") {
            it("should parse successful validation response") = runTest {
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
            
            it("should parse failed validation response") = runTest {
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
            
            it("should handle validation HTTP error responses") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val retrofitResponse = Response.error<Boolean>(
                    500, 
                    okhttp3.ResponseBody.create(null, "Internal Server Error")
                )
                coEvery { mockApiService.validatePayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.validatePayment(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                val exception = result.exceptionOrNull()
                exception shouldNotBe null
                exception!!.message shouldBe "Validation failed with status: 500 - Internal Server Error"
            }
        }
        
        describe("JSON Serialization/Deserialization") {
            it("should serialize PaymentRequest correctly") {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                // When
                val jsonString = json.encodeToString(paymentRequest)
                
                // Then
                jsonString shouldNotBe null
                jsonString shouldBe """{"recipientEmail":"test@example.com","amount":100.0,"currency":"USD"}"""
            }
            
            it("should deserialize PaymentRequest correctly") {
                // Given
                val jsonString = """{"recipientEmail":"test@example.com","amount":100.0,"currency":"USD"}"""
                
                // When
                val paymentRequest = json.decodeFromString<PaymentRequest>(jsonString)
                
                // Then
                paymentRequest.recipientEmail shouldBe "test@example.com"
                paymentRequest.amount shouldBe 100.0
                paymentRequest.currency shouldBe "USD"
            }
            
            it("should serialize PaymentResponse correctly") {
                // Given
                val paymentResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_123",
                    timestamp = "2023-01-01T12:00:00Z"
                )
                
                // When
                val jsonString = json.encodeToString(paymentResponse)
                
                // Then
                jsonString shouldNotBe null
                jsonString.contains("success") shouldBe true
                jsonString.contains("Payment processed successfully") shouldBe true
                jsonString.contains("txn_123") shouldBe true
            }
            
            it("should deserialize PaymentResponse correctly") {
                // Given
                val jsonString = """{"success":true,"message":"Payment processed successfully","transactionId":"txn_123","timestamp":"2023-01-01T12:00:00Z"}"""
                
                // When
                val paymentResponse = json.decodeFromString<PaymentResponse>(jsonString)
                
                // Then
                paymentResponse.success shouldBe true
                paymentResponse.message shouldBe "Payment processed successfully"
                paymentResponse.transactionId shouldBe "txn_123"
                paymentResponse.timestamp shouldBe "2023-01-01T12:00:00Z"
            }
            
            it("should handle malformed JSON gracefully") {
                // Given
                val malformedJson = """{"success":true,"message":"Payment processed successfully","transactionId":"txn_123","timestamp":"2023-01-01T12:00:00Z"invalid"""
                
                // When & Then
                try {
                    json.decodeFromString<PaymentResponse>(malformedJson)
                    // Should not reach here
                    false shouldBe true
                } catch (e: Exception) {
                    // Expected to throw exception
                    e.message shouldNotBe null
                }
            }
            
            it("should handle missing fields gracefully") {
                // Given
                val jsonWithMissingFields = """{"success":true}"""
                
                // When
                val paymentResponse = json.decodeFromString<PaymentResponse>(jsonWithMissingFields)
                
                // Then
                paymentResponse.success shouldBe true
                paymentResponse.message shouldBe ""
                paymentResponse.transactionId shouldBe null
                paymentResponse.timestamp shouldBe null
            }
        }
        
        describe("Edge Cases") {
            it("should handle very large amounts") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = Double.MAX_VALUE,
                    currency = "USD"
                )
                
                val expectedResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_large"
                )
                
                val retrofitResponse = Response.success(expectedResponse)
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
            }
            
            it("should handle special characters in email") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test+tag@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val expectedResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_special"
                )
                
                val retrofitResponse = Response.success(expectedResponse)
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
            }
            
            it("should handle unicode characters") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "tëst@éxämplé.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val expectedResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_unicode"
                )
                
                val retrofitResponse = Response.success(expectedResponse)
                coEvery { mockApiService.sendPayment(paymentRequest) } returns retrofitResponse
                
                // When
                val result = apiClient.sendPayment(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
            }
        }
    }
})
