//
//  ContentView.swift
//  Cashi
//
//  Standalone SwiftUI version for demo purposes
//

import SwiftUI

struct ContentView: View {
    @State private var recipientEmail: String = ""
    @State private var amount: String = ""
    @State private var selectedCurrency: String = "USD"
    
    @State private var showingHistory = false
    @State private var isLoading = false
    @State private var successMessage: String? = nil
    @State private var errorMessage: String? = nil
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 24) {
                    // Header
                    HStack {
                        VStack(alignment: .leading) {
                            Text("Cashi")
                                .font(.largeTitle)
                                .bold()
                            Text("Send payments easily")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        
                        Spacer()
                        
                        Button(action: {
                            showingHistory = true
                        }) {
                            Image(systemName: "list.bullet")
                                .font(.title2)
                                .foregroundColor(.blue)
                        }
                    }
                    .padding()
                    
                    // Success/Error Messages
                    if let error = errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.red.opacity(0.1))
                            .cornerRadius(8)
                            .padding(.horizontal)
                            .onTapGesture {
                                errorMessage = nil
                            }
                    }
                    
                    if let success = successMessage {
                        Text(success)
                            .foregroundColor(.green)
                            .padding()
                            .frame(maxWidth: .infinity)
                            .background(Color.green.opacity(0.1))
                            .cornerRadius(8)
                            .padding(.horizontal)
                            .onTapGesture {
                                successMessage = nil
                            }
                    }
                    
                    // Payment Form
                    VStack(spacing: 20) {
                        Text("Send Payment")
                            .font(.title2)
                            .bold()
                            .frame(maxWidth: .infinity, alignment: .leading)
                        
                        // Recipient Email
                        VStack(alignment: .leading, spacing: 8) {
                            Text("Recipient Email")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            TextField("Enter email", text: $recipientEmail)
                                .textFieldStyle(.roundedBorder)
                                .autocapitalization(.none)
                                .keyboardType(.emailAddress)
                        }
                        
                        // Amount and Currency
                        HStack(spacing: 16) {
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Amount")
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                                TextField("Enter amount", text: $amount)
                                    .textFieldStyle(.roundedBorder)
                                    .keyboardType(.decimalPad)
                            }
                            
                            VStack(alignment: .leading, spacing: 8) {
                                Text("Currency")
                                    .font(.subheadline)
                                    .foregroundColor(.secondary)
                                Picker("Currency", selection: $selectedCurrency) {
                                    Text("USD").tag("USD")
                                    Text("EUR").tag("EUR")
                                    Text("GBP").tag("GBP")
                                }
                                .pickerStyle(.menu)
                            }
                        }
                        
                        // Send Button
                        Button(action: {
                            sendPayment()
                        }) {
                            HStack {
                                if isLoading {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                } else {
                                    Text("Send Payment")
                                        .font(.headline)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(isValidInput() ? Color.blue : Color.gray)
                            .foregroundColor(.white)
                            .cornerRadius(12)
                        }
                        .disabled(!isValidInput() || isLoading)
                    }
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(16)
                    .padding(.horizontal)
                    
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
                .padding(.vertical)
            }
            .navigationBarTitleDisplayMode(.inline)
            .sheet(isPresented: $showingHistory) {
                TransactionHistoryView()
            }
        }
    }
    
    private func isValidInput() -> Bool {
        return !recipientEmail.isEmpty &&
               !amount.isEmpty &&
               Double(amount) != nil &&
               Double(amount) ?? 0 > 0
    }
    
    private func sendPayment() {
        isLoading = true
        errorMessage = nil
        successMessage = nil
        
        // Simulate API call
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            isLoading = false
            
            // Simple validation
            if recipientEmail.contains("@") && Double(amount) != nil {
                successMessage = "Payment sent successfully to \(recipientEmail)!"
                
                // Clear form
                recipientEmail = ""
                amount = ""
            } else {
                errorMessage = "Please enter a valid email and amount"
            }
        }
    }
}

struct TransactionHistoryView: View {
    var body: some View {
        NavigationView {
            VStack(spacing: 20) {
                Image(systemName: "clock.arrow.circlepath")
                    .font(.system(size: 60))
                    .foregroundColor(.blue.opacity(0.3))
                
                Text("Transaction History")
                    .font(.title2)
                    .bold()
                
                Text("This is a demo version without backend integration")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding()
                
                Text("In the full KMP version, this would show real transactions from Firebase")
                    .font(.caption)
                    .foregroundColor(.secondary)
                    .multilineTextAlignment(.center)
                    .padding()
            }
            .padding()
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        // Dismiss
                    }
                }
            }
        }
    }
}

#Preview {
    ContentView()
}

