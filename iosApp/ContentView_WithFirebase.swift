//
//  ContentView.swift with Firebase integration
//  This version connects to the same Firebase as Android
//

import SwiftUI
import Firebase

@MainActor
class TransactionHistoryViewModel: ObservableObject {
    @Published var transactions: [Transaction] = []
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    
    init() {
        loadTransactions()
    }
    
    func loadTransactions() {
        isLoading = true
        errorMessage = nil
        
        FirebaseFirestore.firestore()
            .collection("transactions")
            .order(by: "timestamp", descending: true)
            .addSnapshotListener { [weak self] snapshot, error in
                guard let self = self else { return }
                
                DispatchQueue.main.async {
                    if let error = error {
                        self.errorMessage = error.localizedDescription
                        self.isLoading = false
                        return
                    }
                    
                    guard let snapshot = snapshot else {
                        self.isLoading = false
                        return
                    }
                    
                    self.transactions = snapshot.documents.compactMap { doc in
                        guard let data = doc.data() as? [String: Any] else { return nil }
                        
                        let id = data["id"] as? String ?? doc.documentID
                        let recipientEmail = data["recipientEmail"] as? String ?? ""
                        let amount = (data["amount"] as? NSNumber)?.doubleValue ?? 0.0
                        let currency = data["currency"] as? String ?? ""
                        let statusStr = data["status"] as? String ?? "COMPLETED"
                        let timestampStr = data["timestamp"] as? String ?? ""
                        
                        // Parse timestamp
                        let formatter = ISO8601DateFormatter()
                        formatter.formatOptions = [.withInternetDateTime, .withFractionalSeconds]
                        let date = formatter.date(from: timestampStr) ?? Date()
                        
                        let status = statusStr == "COMPLETED" ? TransactionStatus.completed :
                                    statusStr == "PENDING" ? .pending : .failed
                        
                        return Transaction(
                            id: id,
                            recipientEmail: recipientEmail,
                            amount: amount,
                            currency: currency,
                            timestamp: date,
                            status: status
                        )
                    }
                    
                    self.isLoading = false
                }
            }
    }
    
    func refresh() {
        loadTransactions()
    }
}

struct Transaction: Identifiable {
    let id: String
    let recipientEmail: String
    let amount: Double
    let currency: String
    let timestamp: Date
    let status: TransactionStatus
}

enum TransactionStatus {
    case completed
    case pending
    case failed
}

struct ContentView: View {
    @State private var recipientEmail: String = ""
    @State private var amount: String = ""
    @State private var selectedCurrency: String = "USD"
    
    @State private var showingHistory = false
    @State private var isLoading = false
    @State private var successMessage: String? = nil
    @State private var errorMessage: String? = nil
    @State private var transactionHistoryVM = TransactionHistoryViewModel()
    
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
                            transactionHistoryVM.refresh()
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
                        Text("• Use the history button (📋) to view transactions")
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
                TransactionHistorySheet(historyVM: transactionHistoryVM)
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
        
        // Create transaction
        let transaction = Transaction(
            id: UUID().uuidString,
            recipientEmail: recipientEmail,
            amount: Double(amount) ?? 0.0,
            currency: selectedCurrency,
            timestamp: Date(),
            status: .completed
        )
        
        // Save to Firebase
        let data: [String: Any] = [
            "id": transaction.id,
            "recipientEmail": transaction.recipientEmail,
            "amount": transaction.amount,
            "currency": transaction.currency,
            "timestamp": ISO8601DateFormatter().string(from: transaction.timestamp),
            "status": transaction.status == .completed ? "COMPLETED" : 
                      transaction.status == .pending ? "PENDING" : "FAILED",
            "description": "Payment via iOS"
        ]
        
        FirebaseFirestore.firestore()
            .collection("transactions")
            .document(transaction.id)
            .setData(data) { error in
                DispatchQueue.main.async {
                    self.isLoading = false
                    
                    if let error = error {
                        self.errorMessage = "Failed to save: \(error.localizedDescription)"
                    } else {
                        self.successMessage = "Payment sent to \(self.recipientEmail)!"
                        self.recipientEmail = ""
                        self.amount = ""
                        // Refresh history
                        self.transactionHistoryVM.refresh()
                    }
                }
            }
    }
}

struct TransactionHistorySheet: View {
    @ObservedObject var historyVM: TransactionHistoryViewModel
    
    var body: some View {
        NavigationView {
            ZStack {
                if historyVM.isLoading {
                    ProgressView()
                } else if historyVM.transactions.isEmpty {
                    VStack(spacing: 20) {
                        Image(systemName: "clock.arrow.circlepath")
                            .font(.system(size: 60))
                            .foregroundColor(.blue.opacity(0.3))
                        
                        Text("No Transactions Yet")
                            .font(.title2)
                            .bold()
                        
                        Text("Send a payment to see transaction history")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }
                } else {
                    List(historyVM.transactions) { transaction in
                        TransactionRow(transaction: transaction)
                    }
                }
                
                if let error = historyVM.errorMessage {
                    VStack {
                        Spacer()
                        Text(error)
                            .foregroundColor(.red)
                            .padding()
                            .background(Color.white)
                            .cornerRadius(8)
                            .padding()
                    }
                }
            }
            .navigationTitle("Transaction History")
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        // This will be handled by sheet presentation
                    }
                }
            }
        }
    }
}

struct TransactionRow: View {
    let transaction: Transaction
    
    var body: some View {
        HStack(alignment: .top) {
            VStack(alignment: .leading, spacing: 4) {
                Text(transaction.recipientEmail)
                    .font(.headline)
                
                Text("\(String(format: "%.2f", transaction.amount)) \(transaction.currency)")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                
                Text(formatDate(transaction.timestamp))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
            
            HStack(spacing: 4) {
                Circle()
                    .fill(statusColor(for: transaction.status))
                    .frame(width: 8, height: 8)
                
                Text(statusText(for: transaction.status))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
        }
        .padding(.vertical, 4)
    }
    
    private func formatDate(_ date: Date) -> String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
    
    private func statusColor(for status: TransactionStatus) -> Color {
        switch status {
        case .completed: return .green
        case .pending: return .orange
        case .failed: return .red
        }
    }
    
    private func statusText(for status: TransactionStatus) -> String {
        switch status {
        case .completed: return "Completed"
        case .pending: return "Pending"
        case .failed: return "Failed"
        }
    }
}

