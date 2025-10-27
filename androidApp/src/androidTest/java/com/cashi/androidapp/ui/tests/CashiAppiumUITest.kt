package com.cashi.androidapp.ui.tests

import io.appium.java_client.AppiumDriver
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.openqa.selenium.By
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.WebDriverWait
import org.openqa.selenium.support.ui.ExpectedConditions
import java.net.URL
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CashiAppiumUITest {
    
    private lateinit var driver: AndroidDriver
    private lateinit var wait: WebDriverWait
    
    @BeforeAll
    fun setupDriver() {
        val options = UiAutomator2Options()
            .setDeviceName("Android Emulator")
            .setAppPackage("com.cashi.androidapp")
            .setAppActivity("com.cashi.androidapp.MainActivity")
            .setAutomationName("UiAutomator2")
            .setNoReset(false)
            .setFullReset(false)
            .setNewCommandTimeout(Duration.ofSeconds(60))
        
        driver = AndroidDriver(URL("http://localhost:4723"), options)
        wait = WebDriverWait(driver, Duration.ofSeconds(10))
        
        // Wait for app to load
        Thread.sleep(5000)
    }
    
    @AfterAll
    fun tearDownDriver() {
        driver.quit()
    }
    
    @Test
    @DisplayName("Send Payment - Valid Details")
    fun testSendPaymentWithValidDetails() {
        // Navigate to payment screen (should be default)
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
        
        // Enter recipient email
        val emailField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Recipient Email']"))
        emailField.clear()
        emailField.sendKeys("test@example.com")
        
        // Enter amount
        val amountField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Amount']"))
        amountField.clear()
        amountField.sendKeys("100.0")
        
        // Select currency (USD should be default)
        val currencyDropdown = driver.findElement(By.xpath("//android.widget.TextView[@text='USD']"))
        currencyDropdown.click()
        
        // Select USD from dropdown
        val usdOption = waitForElement(By.xpath("//android.widget.TextView[@text='USD']"))
        usdOption.click()
        
        // Submit payment
        val sendButton = driver.findElement(By.xpath("//android.widget.Button[@text='Send Payment']"))
        sendButton.click()
        
        // Wait for success message or loading to complete
        Thread.sleep(3000)
        
        // Verify success message appears
        val successMessage = waitForElement(By.xpath("//android.widget.TextView[contains(@text, 'Payment sent')]"))
        assertTrue(successMessage.isDisplayed, "Success message should be displayed")
        
        // Verify form is cleared
        val clearedEmailField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Recipient Email']"))
        assertTrue(clearedEmailField.text.isEmpty(), "Email field should be cleared after successful payment")
    }
    
    @Test
    @DisplayName("Send Payment - Invalid Email")
    fun testSendPaymentWithInvalidEmail() {
        // Navigate to payment screen
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
        
        // Enter invalid email
        val emailField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Recipient Email']"))
        emailField.clear()
        emailField.sendKeys("invalid-email")
        
        // Enter valid amount
        val amountField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Amount']"))
        amountField.clear()
        amountField.sendKeys("100.0")
        
        // Submit payment
        val sendButton = driver.findElement(By.xpath("//android.widget.Button[@text='Send Payment']"))
        sendButton.click()
        
        // Wait for error message
        Thread.sleep(2000)
        
        // Verify error message appears
        val errorMessage = waitForElement(By.xpath("//android.widget.TextView[contains(@text, 'Invalid email format')]"))
        assertTrue(errorMessage.isDisplayed, "Error message should be displayed for invalid email")
    }
    
    @Test
    @DisplayName("Send Payment - Negative Amount")
    fun testSendPaymentWithNegativeAmount() {
        // Navigate to payment screen
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
        
        // Enter valid email
        val emailField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Recipient Email']"))
        emailField.clear()
        emailField.sendKeys("test@example.com")
        
        // Enter negative amount
        val amountField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Amount']"))
        amountField.clear()
        amountField.sendKeys("-100.0")
        
        // Submit payment
        val sendButton = driver.findElement(By.xpath("//android.widget.Button[@text='Send Payment']"))
        sendButton.click()
        
        // Wait for error message
        Thread.sleep(2000)
        
        // Verify error message appears
        val errorMessage = waitForElement(By.xpath("//android.widget.TextView[contains(@text, 'Amount must be greater than 0')]"))
        assertTrue(errorMessage.isDisplayed, "Error message should be displayed for negative amount")
    }
    
    @Test
    @DisplayName("View Transaction History")
    fun testViewTransactionHistory() {
        // Navigate to payment screen first
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
        
        // Click on history icon
        val historyIcon = driver.findElement(By.xpath("//android.widget.ImageButton[@content-desc='Transaction History']"))
        historyIcon.click()
        
        // Wait for transaction history screen
        waitForElement(By.xpath("//android.widget.TextView[@text='Transaction History']"))
        
        // Verify we're on the transaction history screen
        val historyTitle = driver.findElement(By.xpath("//android.widget.TextView[@text='Transaction History']"))
        assertTrue(historyTitle.isDisplayed, "Transaction History title should be displayed")
        
        // Check if there are transactions or empty state
        val transactions = driver.findElements(By.xpath("//android.widget.TextView[contains(@text, '@')]"))
        if (transactions.isNotEmpty()) {
            // Verify transaction details are displayed
            val firstTransaction = transactions.first()
            assertTrue(firstTransaction.isDisplayed, "Transaction should be displayed")
        } else {
            // Verify empty state
            val emptyMessage = waitForElement(By.xpath("//android.widget.TextView[@text='No transactions found']"))
            assertTrue(emptyMessage.isDisplayed, "Empty state message should be displayed")
        }
        
        // Navigate back to payment screen
        val backButton = driver.findElement(By.xpath("//android.widget.ImageButton[@content-desc='Back']"))
        backButton.click()
        
        // Verify we're back on payment screen
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
    }
    
    @Test
    @DisplayName("Complete Payment Flow - Send and Verify in History")
    fun testCompletePaymentFlow() {
        // Step 1: Send a payment
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
        
        val emailField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Recipient Email']"))
        emailField.clear()
        emailField.sendKeys("appium-test@example.com")
        
        val amountField = driver.findElement(By.xpath("//android.widget.EditText[@hint='Amount']"))
        amountField.clear()
        amountField.sendKeys("250.0")
        
        val sendButton = driver.findElement(By.xpath("//android.widget.Button[@text='Send Payment']"))
        sendButton.click()
        
        // Wait for success
        Thread.sleep(3000)
        waitForElement(By.xpath("//android.widget.TextView[contains(@text, 'Payment sent')]"))
        
        // Step 2: Navigate to transaction history
        val historyIcon = driver.findElement(By.xpath("//android.widget.ImageButton[@content-desc='Transaction History']"))
        historyIcon.click()
        
        waitForElement(By.xpath("//android.widget.TextView[@text='Transaction History']"))
        
        // Step 3: Verify the transaction appears in history
        val transactionEmail = waitForElement(By.xpath("//android.widget.TextView[@text='appium-test@example.com']"))
        assertTrue(transactionEmail.isDisplayed, "Transaction email should be displayed in history")
        
        val transactionAmount = waitForElement(By.xpath("//android.widget.TextView[contains(@text, '250')]"))
        assertTrue(transactionAmount.isDisplayed, "Transaction amount should be displayed in history")
        
        // Step 4: Navigate back
        val backButton = driver.findElement(By.xpath("//android.widget.ImageButton[@content-desc='Back']"))
        backButton.click()
        
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
    }
    
    @Test
    @DisplayName("Currency Selection")
    fun testCurrencySelection() {
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
        
        // Click on currency dropdown
        val currencyDropdown = driver.findElement(By.xpath("//android.widget.TextView[@text='USD']"))
        currencyDropdown.click()
        
        // Select EUR
        val eurOption = waitForElement(By.xpath("//android.widget.TextView[@text='EUR']"))
        eurOption.click()
        
        // Verify EUR is selected
        val selectedCurrency = driver.findElement(By.xpath("//android.widget.TextView[@text='EUR']"))
        assertTrue(selectedCurrency.isDisplayed, "EUR should be selected")
        
        // Test GBP selection
        currencyDropdown.click()
        val gbpOption = waitForElement(By.xpath("//android.widget.TextView[@text='GBP']"))
        gbpOption.click()
        
        val selectedGBP = driver.findElement(By.xpath("//android.widget.TextView[@text='GBP']"))
        assertTrue(selectedGBP.isDisplayed, "GBP should be selected")
    }
    
    @Test
    @DisplayName("Form Validation - Empty Fields")
    fun testFormValidationEmptyFields() {
        waitForElement(By.xpath("//android.widget.TextView[@text='Send Payment']"))
        
        // Try to submit with empty fields
        val sendButton = driver.findElement(By.xpath("//android.widget.Button[@text='Send Payment']"))
        sendButton.click()
        
        // Wait for validation error
        Thread.sleep(2000)
        
        // Verify validation error appears
        val errorMessage = waitForElement(By.xpath("//android.widget.TextView[contains(@text, 'Please fill in all fields')]"))
        assertTrue(errorMessage.isDisplayed, "Validation error should be displayed for empty fields")
    }
    
    private fun waitForElement(by: By): WebElement {
        return wait.until(ExpectedConditions.presenceOfElementLocated(by))
    }
    
    private fun waitForElementToBeClickable(by: By): WebElement {
        return wait.until(ExpectedConditions.elementToBeClickable(by))
    }
}
