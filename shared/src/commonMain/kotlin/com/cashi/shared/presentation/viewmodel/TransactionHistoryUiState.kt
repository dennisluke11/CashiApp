package com.cashi.shared.presentation.viewmodel

import com.cashi.shared.data.model.Transaction

/**
 * UI state for the Transaction History screen
 * Contains the list of transactions, loading state, and error messages
 */
data class TransactionHistoryUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

