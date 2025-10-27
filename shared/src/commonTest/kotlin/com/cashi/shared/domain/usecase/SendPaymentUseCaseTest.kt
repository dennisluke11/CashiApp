package com.cashi.shared.domain.usecase

import com.cashi.shared.data.model.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import kotlinx.coroutines.test.runTest

class SendPaymentUseCaseTest : DescribeSpec({
    
    describe("SendPaymentUseCase") {
        val mockPaymentRepository = mockk<com.cashi.shared.domain.repository.PaymentRepository>()
        val mockTransactionRepository = mockk<com.cashi.shared.domain.repository.TransactionRepository>()
        val useCase = SendPaymentUseCase(mockPaymentRepository, mockTransactionRepository)
        
        beforeEach {
            clearAllMocks()
        }
        
        describe("when sending a valid payment") {
            it("should process payment successfully") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val paymentResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_123",
                    timestamp = "2023-01-01T00:00:00Z"
                )
                
                coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
                
                // When
                val result = useCase(paymentRequest)
                
                // Then
                result.isSuccess shouldBe true
                result.getOrNull() shouldNotBe null
                coVerify { mockPaymentRepository.sendPayment(paymentRequest) }
                coVerify { mockTransactionRepository.saveTransaction(any()) }
            }
        }
        
        describe("when payment request is invalid") {
            it("should fail validation for invalid email") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "invalid-email",
                    amount = 100.0,
                    currency = "USD"
                )
                
                // When
                val result = useCase(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldBe "Validation failed: Invalid email format"
                coVerify(exactly = 0) { mockPaymentRepository.sendPayment(any()) }
            }
            
            it("should fail validation for negative amount") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = -100.0,
                    currency = "USD"
                )
                
                // When
                val result = useCase(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldBe "Validation failed: Amount must be greater than 0"
                coVerify(exactly = 0) { mockPaymentRepository.sendPayment(any()) }
            }
            
            it("should fail validation for unsupported currency") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "INVALID"
                )
                
                // When
                val result = useCase(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldBe "Validation failed: Unsupported currency. Supported: USD, EUR, GBP"
                coVerify(exactly = 0) { mockPaymentRepository.sendPayment(any()) }
            }
        }
        
        describe("when API call fails") {
            it("should propagate the error") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val error = Exception("Network error")
                coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.failure(error)
                
                // When
                val result = useCase(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                result.exceptionOrNull() shouldBe error
                coVerify(exactly = 0) { mockTransactionRepository.saveTransaction(any()) }
            }
        }
        
        describe("when API returns unsuccessful response") {
            it("should fail with API error message") = runTest {
                // Given
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val paymentResponse = PaymentResponse(
                    success = false,
                    message = "Insufficient funds"
                )
                
                coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                
                // When
                val result = useCase(paymentRequest)
                
                // Then
                result.isFailure shouldBe true
                result.exceptionOrNull()?.message shouldBe "Insufficient funds"
                coVerify(exactly = 0) { mockTransactionRepository.saveTransaction(any()) }
            }
        }
    }
})
