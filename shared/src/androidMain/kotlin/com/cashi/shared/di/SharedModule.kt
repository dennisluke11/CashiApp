package com.cashi.shared.di

import com.cashi.shared.data.remote.PaymentApiClient
import com.cashi.shared.data.remote.createPaymentApiService
import com.cashi.shared.domain.repository.PaymentRepository
import com.cashi.shared.domain.repository.PaymentRepositoryImpl
import com.cashi.shared.domain.repository.TransactionRepository
import com.cashi.shared.domain.repository.TransactionRepositoryImpl
import com.cashi.shared.domain.usecase.GetTransactionsUseCase
import com.cashi.shared.domain.usecase.SendPaymentUseCase
import com.cashi.shared.presentation.viewmodel.PaymentViewModel
import com.cashi.shared.presentation.viewmodel.TransactionHistoryViewModel
import org.koin.dsl.module

val sharedModule = module {
    
    // API Service
    single { createPaymentApiService() }
    
    // API Client
    single { PaymentApiClient(get()) }
    
    // Firebase Client
    single { createFirebaseFirestoreClient() }
    
    // Repositories
    single<PaymentRepository> { PaymentRepositoryImpl(get()) }
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    
    // Use Cases
    single { SendPaymentUseCase(get(), get()) }
    single { GetTransactionsUseCase(get()) }
    
    // ViewModels
    factory { PaymentViewModel(get()) }
    factory { TransactionHistoryViewModel(get()) }
}

