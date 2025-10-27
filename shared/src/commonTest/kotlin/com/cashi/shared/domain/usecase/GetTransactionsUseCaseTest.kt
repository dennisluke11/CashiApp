package com.cashi.shared.domain.usecase

import com.cashi.shared.data.model.*
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.mockk.*
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest

class GetTransactionsUseCaseTest : DescribeSpec({
    
    describe("GetTransactionsUseCase") {
        val mockTransactionRepository = mockk<com.cashi.shared.domain.repository.TransactionRepository>()
        val useCase = GetTransactionsUseCase(mockTransactionRepository)
        
        beforeEach {
            clearAllMocks()
        }
        
        describe("when getting transactions") {
            it("should return flow of transactions") = runTest {
                // Given
                val transactions = listOf(
                    Transaction(
                        id = "txn_1",
                        recipientEmail = "test1@example.com",
                        amount = 100.0,
                        currency = "USD",
                        timestamp = kotlinx.datetime.Clock.System.now(),
                        status = PaymentStatus.COMPLETED,
                        description = "Payment 1"
                    ),
                    Transaction(
                        id = "txn_2",
                        recipientEmail = "test2@example.com",
                        amount = 200.0,
                        currency = "EUR",
                        timestamp = kotlinx.datetime.Clock.System.now(),
                        status = PaymentStatus.PENDING,
                        description = "Payment 2"
                    )
                )
                
                every { mockTransactionRepository.getTransactions() } returns flowOf(transactions)
                
                // When
                val result = useCase()
                
                // Then
                result.collect { transactionList ->
                    transactionList shouldBe transactions
                    transactionList.size shouldBe 2
                }
                
                verify { mockTransactionRepository.getTransactions() }
            }
            
            it("should return empty list when no transactions") = runTest {
                // Given
                every { mockTransactionRepository.getTransactions() } returns flowOf(emptyList())
                
                // When
                val result = useCase()
                
                // Then
                result.collect { transactionList ->
                    transactionList shouldBe emptyList()
                }
                
                verify { mockTransactionRepository.getTransactions() }
            }
        }
    }
})
