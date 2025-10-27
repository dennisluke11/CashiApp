package com.cashi.androidapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.cashi.androidapp.R
import com.cashi.androidapp.ui.components.TransactionItem
import com.cashi.androidapp.ui.theme.Dimens
import com.cashi.androidapp.ui.utils.stringRes
import com.cashi.shared.presentation.viewmodel.TransactionHistoryViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionHistoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: TransactionHistoryViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = stringRes(R.string.back))
            }
            Text(
                text = stringRes(R.string.transaction_history),
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.width(Dimens.spacingXXLarge)) // Balance the back button
        }
        
        Spacer(modifier = Modifier.height(Dimens.spacingMedium))
        
        // Error Message
        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(Dimens.cardPadding)
                )
            }
            Spacer(modifier = Modifier.height(Dimens.spacingMedium))
        }
        
        // Loading State
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Transactions List
            if (uiState.transactions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Text(
                            text = stringRes(R.string.no_transactions_found),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(Dimens.spacingLarge)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(Dimens.listItemSpacing)
                ) {
                    items(uiState.transactions) { transaction ->
                        TransactionItem(transaction = transaction)
                    }
                }
            }
        }
    }
}
