package com.cashi.androidapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import com.cashi.androidapp.R
import com.cashi.androidapp.ui.components.InstructionsCard
import com.cashi.androidapp.ui.components.PaymentForm
import com.cashi.androidapp.ui.theme.Dimens
import com.cashi.androidapp.ui.utils.stringRes
import com.cashi.shared.presentation.viewmodel.PaymentViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateToHistory: () -> Unit,
    viewModel: PaymentViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.clearMessages()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.screenPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringRes(R.string.send_payment_title),
                style = MaterialTheme.typography.headlineMedium
            )
            IconButton(onClick = onNavigateToHistory) {
                Icon(Icons.Default.List, contentDescription = stringRes(R.string.transaction_history))
            }
        }
        
        Spacer(modifier = Modifier.height(Dimens.spacingXLarge))
        
        // Payment Form Component
        PaymentForm(
            uiState = uiState,
            onRecipientEmailChange = viewModel::updateRecipientEmail,
            onAmountChange = viewModel::updateAmount,
            onCurrencyChange = viewModel::updateCurrency,
            onSendPayment = viewModel::sendPayment
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Instructions Component
        InstructionsCard()
    }
}
