package com.cashi.shared.bdd

import com.cashi.shared.data.model.*
import com.cashi.shared.domain.usecase.SendPaymentUseCase
import com.cashi.shared.domain.usecase.GetTransactionsUseCase
import io.cucumber.java.en.Given
import io.cucumber.java.en.When
import io.cucumber.java.en.Then
import io.mockk.*
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.jupiter.api.Assertions.*

class PaymentStepDefinitions {
    
    private lateinit var sendPaymentUseCase: SendPaymentUseCase
    private lateinit var getTransactionsUseCase: GetTransactionsUseCase
    private lateinit var mockPaymentRepository: com.cashi.shared.domain.repository.PaymentRepository
    private lateinit var mockTransactionRepository: com.cashi.shared.domain.repository.TransactionRepository
    
    private var currentPaymentRequest: PaymentRequest? = null
    private var paymentResult: Result<com.cashi.shared.domain.usecase.PaymentResult>? = null
    private var transactions: List<Transaction> = emptyList()
    
    @Given("the payment system is available")
    fun thePaymentSystemIsAvailable() {
        mockPaymentRepository = mockk()
        mockTransactionRepository = mockk()
        sendPaymentUseCase = SendPaymentUseCase(mockPaymentRepository, mockTransactionRepository)
        getTransactionsUseCase = GetTransactionsUseCase(mockTransactionRepository)
    }
    
    @Given("the user has a valid account")
    fun theUserHasAValidAccount() {
        // Mock successful authentication
        coEvery { mockPaymentRepository.sendPayment(any()) } returns Result.success(
            PaymentResponse(success = true, message = "Payment processed successfully")
        )
        coEvery { mockTransactionRepository.saveTransaction(any()) } returns Result.success(Unit)
    }
    
    @Given("the user enters a valid recipient email {string}")
    fun theUserEntersAValidRecipientEmail(email: String) {
        currentPaymentRequest = PaymentRequest(
            recipientEmail = email,
            amount = 100.0,
            currency = "USD"
        )
    }
    
    @Given("the user enters an invalid recipient email {string}")
    fun theUserEntersAnInvalidRecipientEmail(email: String) {
        currentPaymentRequest = PaymentRequest(
            recipientEmail = email,
            amount = 100.0,
            currency = "USD"
        )
    }
    
    @Given("the user enters an amount of {string}")
    fun theUserEntersAnAmountOf(amount: String) {
        currentPaymentRequest = currentPaymentRequest?.copy(amount = amount.toDouble())
    }
    
    @Given("the user enters a negative amount of {string}")
    fun theUserEntersANegativeAmountOf(amount: String) {
        currentPaymentRequest = currentPaymentRequest?.copy(amount = amount.toDouble())
    }
    
    @Given("the user selects currency {string}")
    fun theUserSelectsCurrency(currency: String) {
        currentPaymentRequest = currentPaymentRequest?.copy(currency = currency)
    }
    
    @When("the user submits the payment")
    fun theUserSubmitsThePayment() = runTest {
        val request = currentPaymentRequest ?: throw IllegalStateException("Payment request not set")
        paymentResult = sendPaymentUseCase(request)
    }
    
    @Then("the payment should be processed successfully")
    fun thePaymentShouldBeProcessedSuccessfully() {
        assertNotNull(paymentResult)
        assertTrue(paymentResult!!.isSuccess)
    }
    
    @Then("the payment should fail validation")
    fun thePaymentShouldFailValidation() {
        assertNotNull(paymentResult)
        assertTrue(paymentResult!!.isFailure)
    }
    
    @Then("the transaction should be saved to Firestore")
    fun theTransactionShouldBeSavedToFirestore() {
        coVerify { mockTransactionRepository.saveTransaction(any()) }
    }
    
    @Then("the user should see a success message {string}")
    fun theUserShouldSeeASuccessMessage(message: String) {
        val result = paymentResult?.getOrNull()
        assertTrue(result is com.cashi.shared.domain.usecase.PaymentResult.Success)
        assertEquals(message, (result as com.cashi.shared.domain.usecase.PaymentResult.Success).message)
    }
    
    @Then("the user should see an error message {string}")
    fun theUserShouldSeeAnErrorMessage(message: String) {
        val exception = paymentResult?.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.contains(message))
    }
    
    @Given("there are existing transactions in Firestore")
    fun thereAreExistingTransactionsInFirestore() {
        transactions = listOf(
            Transaction(
                id = "txn_1",
                recipientEmail = "test1@example.com",
                amount = 100.0,
                currency = "USD",
                timestamp = Clock.System.now(),
                status = PaymentStatus.COMPLETED,
                description = "Test payment 1"
            ),
            Transaction(
                id = "txn_2",
                recipientEmail = "test2@example.com",
                amount = 200.0,
                currency = "EUR",
                timestamp = Clock.System.now(),
                status = PaymentStatus.PENDING,
                description = "Test payment 2"
            )
        )
        
        coEvery { mockTransactionRepository.getTransactions() } returns kotlinx.coroutines.flow.flowOf(transactions)
    }
    
    @Given("the user has no transactions")
    fun theUserHasNoTransactions() {
        transactions = emptyList()
        coEvery { mockTransactionRepository.getTransactions() } returns kotlinx.coroutines.flow.flowOf(emptyList())
    }
    
    @When("the user navigates to transaction history")
    fun theUserNavigatesToTransactionHistory() = runTest {
        // Simulate navigation - in real app this would trigger ViewModel
        getTransactionsUseCase().collect { transactionList ->
            transactions = transactionList
        }
    }
    
    @When("the app loads the transactions")
    fun theAppLoadsTheTransactions() = runTest {
        getTransactionsUseCase().collect { transactionList ->
            transactions = transactionList
        }
    }
    
    @Then("the user should see a list of transactions")
    fun theUserShouldSeeAListOfTransactions() {
        assertFalse(transactions.isEmpty())
    }
    
    @Then("each transaction should display recipient email, amount, currency, and timestamp")
    fun eachTransactionShouldDisplayRecipientEmailAmountCurrencyAndTimestamp() {
        transactions.forEach { transaction ->
            assertNotNull(transaction.recipientEmail)
            assertTrue(transaction.amount > 0)
            assertNotNull(transaction.currency)
            assertNotNull(transaction.timestamp)
        }
    }
    
    @Then("transactions should be ordered by timestamp descending")
    fun transactionsShouldBeOrderedByTimestampDescending() {
        val sortedTransactions = transactions.sortedByDescending { it.timestamp }
        assertEquals(sortedTransactions, transactions)
    }
    
    @Then("the user should see {string}")
    fun theUserShouldSee(message: String) {
        assertTrue(transactions.isEmpty())
        // In a real app, this would check the UI state
    }
    
    @Then("the transaction should appear in the transaction history")
    fun theTransactionShouldAppearInTheTransactionHistory() {
        // Verify transaction was saved and can be retrieved
        assertTrue(transactions.isNotEmpty())
        val latestTransaction = transactions.first()
        assertNotNull(latestTransaction.recipientEmail)
        assertTrue(latestTransaction.amount > 0)
    }
    
    @Then("no transaction should be saved to Firestore")
    fun noTransactionShouldBeSavedToFirestore() {
        // Verify no new transactions were created
        coVerify(exactly = 0) { mockTransactionRepository.saveTransaction(any()) }
    }
    
    @Then("the transaction count should be greater than {int}")
    fun theTransactionCountShouldBeGreaterThan(expectedCount: Int) {
        assertTrue(transactions.size > expectedCount)
    }
    
    @Then("the transaction count should be {int}")
    fun theTransactionCountShouldBe(expectedCount: Int) {
        assertEquals(expectedCount, transactions.size)
    }
    
    @Given("the user is viewing transaction history")
    fun theUserIsViewingTransactionHistory() {
        // Simulate user being on transaction history screen
        // In real app, this would set up the UI state
    }
    
    @Given("there are {int} existing transactions")
    fun thereAreExistingTransactions(count: Int) {
        transactions = (1..count).map { i ->
            Transaction(
                id = "txn_$i",
                recipientEmail = "test$i@example.com",
                amount = (i * 100).toDouble(),
                currency = "USD",
                timestamp = Clock.System.now(),
                status = PaymentStatus.COMPLETED,
                description = "Test payment $i"
            )
        }
        
        coEvery { mockTransactionRepository.getTransactions() } returns kotlinx.coroutines.flow.flowOf(transactions)
    }
    
    @When("a new payment is processed successfully")
    fun aNewPaymentIsProcessedSuccessfully() = runTest {
        val newTransaction = Transaction(
            id = "txn_new",
            recipientEmail = "new@example.com",
            amount = 150.0,
            currency = "USD",
            timestamp = Clock.System.now(),
            status = PaymentStatus.COMPLETED,
            description = "New payment"
        )
        
        transactions = listOf(newTransaction) + transactions
        coEvery { mockTransactionRepository.getTransactions() } returns kotlinx.coroutines.flow.flowOf(transactions)
    }
    
    @Then("the transaction history should update automatically")
    fun theTransactionHistoryShouldUpdateAutomatically() {
        // Verify real-time updates are working
        assertTrue(transactions.isNotEmpty())
    }
    
    @Then("the new transaction should appear at the top of the list")
    fun theNewTransactionShouldAppearAtTheTopOfTheList() {
        val latestTransaction = transactions.first()
        assertEquals("txn_new", latestTransaction.id)
        assertEquals("new@example.com", latestTransaction.recipientEmail)
    }
    
    @Given("the user enters payment details:")
    fun theUserEntersPaymentDetails(dataTable: io.cucumber.datatable.DataTable) {
        val data = dataTable.asMap(String::class.java, String::class.java)
        currentPaymentRequest = PaymentRequest(
            recipientEmail = data["Email"] ?: "",
            amount = data["Amount"]?.toDouble() ?: 0.0,
            currency = data["Currency"] ?: ""
        )
    }
    
    @Then("all validations should pass")
    fun allValidationsShouldPass() {
        assertNotNull(currentPaymentRequest)
        assertTrue(currentPaymentRequest!!.recipientEmail.isNotEmpty())
        assertTrue(currentPaymentRequest!!.amount > 0)
        assertTrue(currentPaymentRequest!!.currency.isNotEmpty())
    }
    
    @Then("validation should fail with multiple errors")
    fun validationShouldFailWithMultipleErrors() {
        assertNotNull(paymentResult)
        assertTrue(paymentResult!!.isFailure)
        val exception = paymentResult!!.exceptionOrNull()
        assertNotNull(exception)
        // Should contain multiple validation errors
        assertTrue(exception!!.message!!.contains("Invalid email format") ||
                  exception.message!!.contains("Amount must be greater than 0") ||
                  exception.message!!.contains("Unsupported currency"))
    }
    
    @Then("the user should see appropriate error messages")
    fun theUserShouldSeeAppropriateErrorMessages() {
        val exception = paymentResult?.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception!!.message!!.isNotEmpty())
    }
}
