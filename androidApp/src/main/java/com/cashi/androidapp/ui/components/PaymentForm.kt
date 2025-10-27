package com.cashi.androidapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cashi.androidapp.R
import com.cashi.androidapp.ui.theme.Dimens
import com.cashi.androidapp.ui.utils.stringRes
import com.cashi.shared.data.model.Currency
import com.cashi.shared.presentation.viewmodel.PaymentUiState

@Composable
fun PaymentForm(
    uiState: PaymentUiState,
    onRecipientEmailChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onCurrencyChange: (Currency) -> Unit,
    onSendPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.cardPadding),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingMedium)
        ) {
            // Recipient Email
            OutlinedTextField(
                value = uiState.recipientEmail,
                onValueChange = onRecipientEmailChange,
                label = { Text(stringRes(R.string.recipient_email)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = uiState.errorMessage != null
            )
            
            // Amount and Currency Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.amount,
                    onValueChange = onAmountChange,
                    label = { Text(stringRes(R.string.amount)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = uiState.errorMessage != null
                )
                
                // Currency Dropdown
                CurrencySelector(
                    selectedCurrency = uiState.selectedCurrency,
                    onCurrencyChange = onCurrencyChange
                )
            }
            
            // Error Message
            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Success Message
            uiState.successMessage?.let { success ->
                Text(
                    text = success,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            
            // Send Button
            Button(
                onClick = onSendPayment,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(Dimens.progressIndicator),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(Dimens.spacingSmall))
                }
                Text(stringRes(R.string.send_payment_button))
            }
        }
    }
}

