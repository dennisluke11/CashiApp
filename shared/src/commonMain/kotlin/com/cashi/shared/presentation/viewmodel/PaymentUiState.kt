package com.cashi.shared.presentation.viewmodel

import com.cashi.shared.data.model.Currency

/**
 * UI state for the Payment screen
 * Contains form input fields, loading state, and messages
 */
data class PaymentUiState(
    val recipientEmail: String = "",
    val amount: String = "",
    val selectedCurrency: Currency = Currency.USD,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

