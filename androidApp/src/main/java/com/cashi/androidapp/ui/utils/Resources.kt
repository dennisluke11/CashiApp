package com.cashi.androidapp.ui.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.cashi.androidapp.R

/**
 * String resource IDs for the Cashi app
 */
object StringRes {
    // Payment Screen
    const val send_payment_title = R.string.send_payment_title
    const val transaction_history = R.string.transaction_history
    const val recipient_email = R.string.recipient_email
    const val amount = R.string.amount
    const val currency = R.string.currency
    const val send_payment_button = R.string.send_payment_button
    const val instructions_title = R.string.instructions_title
    const val instruction_email = R.string.instruction_email
    const val instruction_amount = R.string.instruction_amount
    const val instruction_currency = R.string.instruction_currency
    const val instruction_send = R.string.instruction_send
    
    // Transaction History
    const val transaction_history_title = R.string.transaction_history
    const val back = R.string.back
    const val no_transactions_found = R.string.no_transactions_found
    const val unknown_date = R.string.unknown_date
    
    // Errors
    const val please_fill_all_fields = R.string.please_fill_all_fields
    const val invalid_amount_format = R.string.invalid_amount_format
    const val payment_failed = R.string.payment_failed
}

/**
 * Extension function to easily access string resources in Compose
 */
@Composable
fun stringRes(@StringRes id: Int): String = stringResource(id)
