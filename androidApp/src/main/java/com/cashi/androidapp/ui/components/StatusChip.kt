package com.cashi.androidapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.cashi.androidapp.ui.theme.Dimens
import com.cashi.shared.data.model.PaymentStatus

@Composable
fun StatusChip(status: PaymentStatus) {
    val (backgroundColor, textColor) = when (status) {
        PaymentStatus.COMPLETED -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        PaymentStatus.PENDING -> MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        PaymentStatus.FAILED -> MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
    }
    
    Surface(
        color = backgroundColor,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = status.name,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = Dimens.spacingSmall, vertical = Dimens.spacingSmall / 2)
        )
    }
}

