package com.cashi.androidapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cashi.androidapp.R
import com.cashi.androidapp.ui.theme.Dimens
import com.cashi.androidapp.ui.utils.stringRes
import com.cashi.shared.data.model.PaymentStatus
import com.cashi.shared.data.model.Transaction
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.cardElevation)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.listItemPadding)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.recipientEmail,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${getCurrencySymbol(transaction.currency)}${transaction.amount}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = transaction.currency,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusChip(status = transaction.status)
            }
            
            Spacer(modifier = Modifier.height(Dimens.spacingSmall / 2))
            
            Text(
                text = formatTimestamp(transaction.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            transaction.description?.let { description ->
                Spacer(modifier = Modifier.height(Dimens.spacingSmall / 2))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Currency symbol helper function
 */
private fun getCurrencySymbol(currency: String): String {
    return when (currency) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        else -> currency
    }
}

/**
 * Format timestamp to readable date string
 */
@Composable
private fun formatTimestamp(timestamp: kotlinx.datetime.Instant): String {
    val context = androidx.compose.ui.platform.LocalContext.current
    return try {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        formatter.format(Date(timestamp.toEpochMilliseconds()))
    } catch (e: Exception) {
        context.getString(R.string.unknown_date)
    }
}

