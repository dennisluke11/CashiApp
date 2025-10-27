Feature: Payment Processing
  As a user of the Cashi mobile app
  I want to send payments to recipients
  So that I can transfer money securely

  Background:
    Given the payment system is available
    And the user has a valid account

  @smoke @payment-success
  Scenario: Send a successful payment with valid details
    Given the user enters a valid recipient email "test@example.com"
    And the user enters an amount of "100.0"
    And the user selects currency "USD"
    When the user submits the payment
    Then the payment should be processed successfully
    And the transaction should be saved to Firestore
    And the user should see a success message "Payment sent"
    And the transaction should appear in the transaction history

  @validation @email-validation
  Scenario: Send payment with invalid email format
    Given the user enters an invalid recipient email "invalid-email"
    And the user enters an amount of "100.0"
    And the user selects currency "USD"
    When the user submits the payment
    Then the payment should fail validation
    And the user should see an error message "Invalid email format"
    And no transaction should be saved to Firestore

  @validation @amount-validation
  Scenario: Send payment with negative amount
    Given the user enters a valid recipient email "test@example.com"
    And the user enters a negative amount of "-100.0"
    And the user selects currency "USD"
    When the user submits the payment
    Then the payment should fail validation
    And the user should see an error message "Amount must be greater than 0"
    And no transaction should be saved to Firestore

  @validation @currency-validation
  Scenario: Send payment with unsupported currency
    Given the user enters a valid recipient email "test@example.com"
    And the user enters an amount of "100.0"
    And the user selects currency "INVALID"
    When the user submits the payment
    Then the payment should fail validation
    And the user should see an error message "Unsupported currency"
    And no transaction should be saved to Firestore

  @edge-case @zero-amount
  Scenario: Send payment with zero amount
    Given the user enters a valid recipient email "test@example.com"
    And the user enters an amount of "0.0"
    And the user selects currency "USD"
    When the user submits the payment
    Then the payment should fail validation
    And the user should see an error message "Amount must be greater than 0"

  @edge-case @large-amount
  Scenario: Send payment with large amount
    Given the user enters a valid recipient email "test@example.com"
    And the user enters an amount of "999999.99"
    And the user selects currency "USD"
    When the user submits the payment
    Then the payment should be processed successfully
    And the transaction should be saved to Firestore

Feature: Transaction History
  As a user of the Cashi mobile app
  I want to view my transaction history
  So that I can track my payments

  Background:
    Given the payment system is available
    And there are existing transactions in Firestore

  @smoke @transaction-history
  Scenario: View transaction history with existing transactions
    Given the user navigates to transaction history
    When the app loads the transactions
    Then the user should see a list of transactions
    And each transaction should display recipient email, amount, currency, and timestamp
    And transactions should be ordered by timestamp descending
    And the transaction count should be greater than 0

  @edge-case @empty-history
  Scenario: View empty transaction history
    Given the user has no transactions
    When the user navigates to transaction history
    Then the user should see "No transactions found"
    And the transaction count should be 0

  @real-time @live-updates
  Scenario: Real-time transaction updates
    Given the user is viewing transaction history
    And there are 2 existing transactions
    When a new payment is processed successfully
    Then the transaction history should update automatically
    And the transaction count should increase to 3
    And the new transaction should appear at the top of the list

Feature: Payment Validation
  As a user of the Cashi mobile app
  I want my payment inputs to be validated
  So that I can avoid errors and ensure successful transactions

  Background:
    Given the payment system is available

  @validation @comprehensive-validation
  Scenario: Comprehensive payment validation
    Given the user enters payment details:
      | Field     | Value              |
      | Email     | test@example.com   |
      | Amount    | 100.0              |
      | Currency  | USD                |
    When the user submits the payment
    Then all validations should pass
    And the payment should be processed successfully

  @validation @multiple-validation-errors
  Scenario: Multiple validation errors
    Given the user enters payment details:
      | Field     | Value              |
      | Email     | invalid-email      |
      | Amount    | -50.0              |
      | Currency  | INVALID            |
    When the user submits the payment
    Then validation should fail with multiple errors
    And the user should see appropriate error messages