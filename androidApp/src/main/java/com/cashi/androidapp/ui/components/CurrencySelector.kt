package com.cashi.androidapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cashi.androidapp.R
import com.cashi.androidapp.ui.utils.stringRes
import com.cashi.shared.data.model.Currency

/**
 * Reusable currency dropdown selector component
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelector(
    selectedCurrency: Currency,
    onCurrencyChange: (Currency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedCurrency.code,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            label = { Text(stringRes(R.string.currency)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            textStyle = TextStyle(
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .menuAnchor()
                .width(100.dp)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Currency.values().forEach { currency ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = currency.code,
                            style = TextStyle(fontSize = 14.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        onCurrencyChange(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}

