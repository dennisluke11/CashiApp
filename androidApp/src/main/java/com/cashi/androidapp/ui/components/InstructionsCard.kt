package com.cashi.androidapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cashi.androidapp.R
import com.cashi.androidapp.ui.theme.Dimens
import com.cashi.androidapp.ui.utils.stringRes

@Composable
fun InstructionsCard(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(Dimens.cardPadding)
        ) {
            Text(
                text = stringRes(R.string.instructions_title),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))
            Text(
                text = "• ${stringRes(R.string.instruction_email)}\n" +
                        "• ${stringRes(R.string.instruction_amount)}\n" +
                        "• ${stringRes(R.string.instruction_currency)}\n" +
                        "• ${stringRes(R.string.instruction_send)}",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start
            )
        }
    }
}

