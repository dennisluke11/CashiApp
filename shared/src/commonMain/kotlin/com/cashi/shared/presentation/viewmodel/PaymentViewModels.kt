package com.cashi.shared.presentation.viewmodel

import com.cashi.shared.data.model.Currency
import com.cashi.shared.data.model.PaymentRequest
import com.cashi.shared.domain.usecase.GetTransactionsUseCase
import com.cashi.shared.domain.usecase.SendPaymentUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PaymentViewModel(
    private val sendPaymentUseCase: SendPaymentUseCase
) {
    
    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    fun updateRecipientEmail(email: String) {
        _uiState.value = _uiState.value.copy(
            recipientEmail = email,
            errorMessage = null,
            successMessage = null
        )
    }
    
    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(
            amount = amount,
            errorMessage = null,
            successMessage = null
        )
    }
    
    fun updateCurrency(currency: Currency) {
        _uiState.value = _uiState.value.copy(
            selectedCurrency = currency,
            errorMessage = null,
            successMessage = null
        )
    }
    
    fun sendPayment() {
        val currentState = _uiState.value
        
        if (currentState.recipientEmail.isBlank() || currentState.amount.isBlank()) {
            _uiState.value = currentState.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }
        
        val amount = try {
            currentState.amount.toDouble()
        } catch (e: NumberFormatException) {
            _uiState.value = currentState.copy(
                errorMessage = "Invalid amount format"
            )
            return
        }
        
        val paymentRequest = PaymentRequest(
            recipientEmail = currentState.recipientEmail,
            amount = amount,
            currency = currentState.selectedCurrency.code
        )
        
        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)
        
        scope.launch {
            sendPaymentUseCase(paymentRequest)
                .fold(
                    onSuccess = { result ->
                        when (result) {
                            is com.cashi.shared.domain.usecase.PaymentResult.Success -> {
                                _uiState.value = currentState.copy(
                                    isLoading = false,
                                    successMessage = result.message,
                                    recipientEmail = "",
                                    amount = ""
                                )
                            }
                            is com.cashi.shared.domain.usecase.PaymentResult.Error -> {
                                _uiState.value = currentState.copy(
                                    isLoading = false,
                                    errorMessage = result.message
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        _uiState.value = currentState.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Payment failed"
                        )
                    }
                )
        }
    }
    
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}

class TransactionHistoryViewModel(
    private val getTransactionsUseCase: GetTransactionsUseCase
) {
    
    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()
    
    private val scope = CoroutineScope(Dispatchers.Main)
    
    init {
        loadTransactions()
    }
    
    private fun loadTransactions() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        
        scope.launch {
            getTransactionsUseCase()
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load transactions"
                    )
                }
                .collect { transactions ->
                    _uiState.value = _uiState.value.copy(
                        transactions = transactions,
                        isLoading = false,
                        errorMessage = null
                    )
                }
        }
    }
    
    fun refreshTransactions() {
        loadTransactions()
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
