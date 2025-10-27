package com.cashi.shared.domain.usecase

import com.cashi.shared.data.model.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import io.mockk.*
import kotlinx.coroutines.test.runTest

class PaymentValidationTest : DescribeSpec({
    
    describe("Payment Validation") {
        val mockPaymentRepository = mockk<com.cashi.shared.domain.repository.PaymentRepository>()
        val mockTransactionRepository = mockk<com.cashi.shared.domain.repository.TransactionRepository>()
        val useCase = SendPaymentUseCase(mockPaymentRepository, mockTransactionRepository)
        
        beforeEach {
            clearAllMocks()
        }
        
        describe("Email validation") {
            it("should accept valid email formats") = runTest {
                val validEmails = listOf(
                    "test@example.com",
                    "user.name@domain.co.uk",
                    "test+tag@example.org",
                    "123@test.com",
                    "a@b.c"
                )
                
                validEmails.forEach { email ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = email,
                        amount = 100.0,
                        currency = "USD"
                    )
                    
                    val paymentResponse = PaymentResponse(
                        success = true,
                        message = "Payment processed successfully",
                        transactionId = "txn_123"
                    )
                    
                    coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                    coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
                    
                    val result = useCase(paymentRequest)
                    result.isSuccess shouldBe true
                }
            }
            
            it("should reject invalid email formats") = runTest {
                val invalidEmails = listOf(
                    "invalid-email",
                    "@example.com",
                    "test@",
                    "test..test@example.com",
                    "test@.com",
                    "test@example.",
                    "",
                    " ",
                    "test@example..com"
                )
                
                invalidEmails.forEach { email ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = email,
                        amount = 100.0,
                        currency = "USD"
                    )
                    
                    val result = useCase(paymentRequest)
                    result.isFailure shouldBe true
                    result.exceptionOrNull()?.message shouldBe "Validation failed: Invalid email format"
                }
            }
            
            it("should handle email validation with property-based testing") = runTest {
                checkAll(Arb.string()) { email ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = email,
                        amount = 100.0,
                        currency = "USD"
                    )
                    
                    val result = useCase(paymentRequest)
                    
                    // If email is invalid, result should be failure
                    if (!isValidEmail(email)) {
                        result.isFailure shouldBe true
                    }
                }
            }
        }
        
        describe("Amount validation") {
            it("should accept positive amounts") = runTest {
                val validAmounts = listOf(0.01, 1.0, 100.0, 999999.99, 1000000.0)
                
                validAmounts.forEach { amount ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = "test@example.com",
                        amount = amount,
                        currency = "USD"
                    )
                    
                    val paymentResponse = PaymentResponse(
                        success = true,
                        message = "Payment processed successfully",
                        transactionId = "txn_123"
                    )
                    
                    coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                    coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
                    
                    val result = useCase(paymentRequest)
                    result.isSuccess shouldBe true
                }
            }
            
            it("should reject zero and negative amounts") = runTest {
                val invalidAmounts = listOf(0.0, -0.01, -1.0, -100.0, -999999.99)
                
                invalidAmounts.forEach { amount ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = "test@example.com",
                        amount = amount,
                        currency = "USD"
                    )
                    
                    val result = useCase(paymentRequest)
                    result.isFailure shouldBe true
                    result.exceptionOrNull()?.message shouldBe "Validation failed: Amount must be greater than 0"
                }
            }
            
            it("should handle amount validation with property-based testing") = runTest {
                checkAll(Arb.double()) { amount ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = "test@example.com",
                        amount = amount,
                        currency = "USD"
                    )
                    
                    val result = useCase(paymentRequest)
                    
                    // If amount is invalid, result should be failure
                    if (amount <= 0) {
                        result.isFailure shouldBe true
                    }
                }
            }
        }
        
        describe("Currency validation") {
            it("should accept supported currencies") = runTest {
                val supportedCurrencies = listOf("USD", "EUR", "GBP")
                
                supportedCurrencies.forEach { currency ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = "test@example.com",
                        amount = 100.0,
                        currency = currency
                    )
                    
                    val paymentResponse = PaymentResponse(
                        success = true,
                        message = "Payment processed successfully",
                        transactionId = "txn_123"
                    )
                    
                    coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                    coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
                    
                    val result = useCase(paymentRequest)
                    result.isSuccess shouldBe true
                }
            }
            
            it("should reject unsupported currencies") = runTest {
                val unsupportedCurrencies = listOf("INVALID", "CAD", "JPY", "AUD", "", " ", "123")
                
                unsupportedCurrencies.forEach { currency ->
                    val paymentRequest = PaymentRequest(
                        recipientEmail = "test@example.com",
                        amount = 100.0,
                        currency = currency
                    )
                    
                    val result = useCase(paymentRequest)
                    result.isFailure shouldBe true
                    result.exceptionOrNull()?.message shouldBe "Validation failed: Unsupported currency. Supported: USD, EUR, GBP"
                }
            }
        }
        
        describe("Combined validation") {
            it("should handle multiple validation errors") = runTest {
                val paymentRequest = PaymentRequest(
                    recipientEmail = "invalid-email",
                    amount = -100.0,
                    currency = "INVALID"
                )
                
                val result = useCase(paymentRequest)
                result.isFailure shouldBe true
                
                val errorMessage = result.exceptionOrNull()?.message
                errorMessage shouldNotBe null
                errorMessage!! shouldBe "Validation failed: Invalid email format"
            }
            
            it("should pass all validations for valid payment") = runTest {
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 100.0,
                    currency = "USD"
                )
                
                val paymentResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_123"
                )
                
                coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
                
                val result = useCase(paymentRequest)
                result.isSuccess shouldBe true
            }
        }
        
        describe("Edge cases") {
            it("should handle very large amounts") = runTest {
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = Double.MAX_VALUE,
                    currency = "USD"
                )
                
                val paymentResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_123"
                )
                
                coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
                
                val result = useCase(paymentRequest)
                result.isSuccess shouldBe true
            }
            
            it("should handle very small amounts") = runTest {
                val paymentRequest = PaymentRequest(
                    recipientEmail = "test@example.com",
                    amount = 0.01,
                    currency = "USD"
                )
                
                val paymentResponse = PaymentResponse(
                    success = true,
                    message = "Payment processed successfully",
                    transactionId = "txn_123"
                )
                
                coEvery { mockPaymentRepository.sendPayment(paymentRequest) } returns Result.success(paymentResponse)
                coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
                
                val result = useCase(paymentRequest)
                result.isSuccess shouldBe true
            }
        }
    }
})

// Helper function for email validation (same as in use case)
private fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return emailRegex.matches(email)
}
