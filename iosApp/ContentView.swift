//
//  ContentView.swift
//  iosApp
//
//  Created for Cashi Mobile App
//

import SwiftUI
import shared

struct ContentView: View {
    @StateObject private var viewModel = PaymentViewModel(
        sendPaymentUseCase: SendPaymentUseCase(
            paymentRepository: PaymentRepositoryImpl(
                paymentApiClient: PaymentApiClient(paymentApiService: createPaymentApiService())
            ),
            transactionRepository: TransactionRepositoryImpl(
                firestoreClient: createFirebaseFirestoreClient()
            )
        )
    )
    
    var body: some View {
        NavigationView {
            PaymentScreenSwiftUI(viewModel: viewModel)
        }
    }
}

struct PaymentScreenSwiftUI: View {
    @ObservedObject var viewModel: PaymentViewModel
    
    @State private var recipientEmail: String = ""
    @State private var amount: String = ""
    @State private var selectedCurrency: Currency = Currency.usd
    
    @State private var showingHistory = false
    
    var body: some View {
        VStack(spacing: 20) {
            // Header
            HStack {
                Text("Send Payment")
                    .font(.largeTitle)
                    .bold()
                
                Spacer()
                
                Button(action: {
                    showingHistory = true
                }) {
                    Image(systemName: "list.bullet")
                        .font(.title2)
                }
            }
            .padding(.horizontal)
            
            // Payment Form
            VStack(spacing: 16) {
                // Recipient Email
                VStack(alignment: .leading, spacing: 8) {
                    Text("Recipient Email")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    TextField("Enter email", text: $recipientEmail)
                        .textFieldStyle(RoundedBorderTextFieldStyle())
                        .autocapitalization(.none)
                        .keyboardType(.emailAddress)
                }
                
                // Amount and Currency Row
                HStack(spacing: 16) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Amount")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        TextField("Enter amount", text: $amount)
                            .textFieldStyle(RoundedBorderTextFieldStyle())
                            .keyboardType(.decimalPad)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Currency")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                        Picker("Currency", selection: $selectedCurrency) {
                            Text("USD").tag(Currency.usd)
                            Text("EUR").tag(Currency.eur)
                            Text("GBP").tag(Currency.gbp)
                        }
                        .pickerStyle(MenuPickerStyle())
                    }
                }
                
                // Send Button
                Button(action: {
                    viewModel.sendPayment()
                }) {
                    HStack {
                        if viewModel.isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        }
                        Text("Send Payment")
                            .font(.headline)
                    }
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(Color.blue)
                    .foregroundColor(.white)
                    .cornerRadius(10)
                }
                .disabled(viewModel.isLoading)
            }
            .padding()
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .padding(.horizontal)
            
            Spacer()
            
            // Instructions
            VStack(alignment: .leading, spacing: 8) {
                Text("Instructions")
                    .font(.headline)
                
                Text("• Enter a valid email address for the recipient")
                Text("• Enter the amount you want to send")
                Text("• Select the currency (USD, EUR, GBP)")
                Text("• Tap 'Send Payment' to process the transaction")
            }
            .font(.caption)
            .foregroundColor(.secondary)
            .padding()
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(Color(.systemGray6))
            .cornerRadius(12)
            .padding(.horizontal)
        }
        .sheet(isPresented: $showingHistory) {
            TransactionHistoryViewSwiftUI()
        }
    }
}

struct TransactionHistoryViewSwiftUI: View {
    var body: some View {
        Text("Transaction History")
            .font(.title)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    // Back button would be here
                }
            }
    }
}

