const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { v4: uuidv4 } = require('uuid');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware
app.use(cors());
app.use(bodyParser.json());

// In-memory storage for demo purposes
const payments = [];
const transactions = [];

// Helper function to validate email
function isValidEmail(email) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
}

// Helper function to validate amount
function isValidAmount(amount) {
    return typeof amount === 'number' && amount > 0;
}

// Helper function to validate currency
function isValidCurrency(currency) {
    const supportedCurrencies = ['USD', 'EUR', 'GBP'];
    return supportedCurrencies.includes(currency);
}

// POST /payments - Process a payment
app.post('/payments', (req, res) => {
    try {
        const { recipientEmail, amount, currency } = req.body;
        
        // Validation
        if (!recipientEmail || !amount || !currency) {
            return res.status(400).json({
                success: false,
                message: 'Missing required fields: recipientEmail, amount, currency'
            });
        }
        
        if (!isValidEmail(recipientEmail)) {
            return res.status(400).json({
                success: false,
                message: 'Invalid email format'
            });
        }
        
        if (!isValidAmount(amount)) {
            return res.status(400).json({
                success: false,
                message: 'Amount must be a positive number'
            });
        }
        
        if (!isValidCurrency(currency)) {
            return res.status(400).json({
                success: false,
                message: 'Unsupported currency. Supported currencies: USD, EUR, GBP'
            });
        }
        
        // Simulate payment processing
        const transactionId = uuidv4();
        const timestamp = new Date().toISOString();
        
        // Create payment record
        const payment = {
            id: transactionId,
            recipientEmail,
            amount,
            currency,
            timestamp,
            status: 'completed'
        };
        
        payments.push(payment);
        
        // Create transaction record
        const transaction = {
            id: transactionId,
            recipientEmail,
            amount,
            currency,
            timestamp,
            status: 'completed',
            description: `Payment sent to ${recipientEmail}`
        };
        
        transactions.push(transaction);
        
        // Simulate random failures (5% chance)
        if (Math.random() < 0.05) {
            return res.status(500).json({
                success: false,
                message: 'Payment processing failed due to network error'
            });
        }
        
        res.status(200).json({
            success: true,
            message: 'Payment processed successfully',
            transactionId: transactionId,
            timestamp: timestamp
        });
        
    } catch (error) {
        console.error('Payment processing error:', error);
        res.status(500).json({
            success: false,
            message: 'Internal server error'
        });
    }
});

// POST /validate - Validate payment request
app.post('/validate', (req, res) => {
    try {
        const { recipientEmail, amount, currency } = req.body;
        
        const isValid = recipientEmail && 
                       amount && 
                       currency && 
                       isValidEmail(recipientEmail) && 
                       isValidAmount(amount) && 
                       isValidCurrency(currency);
        
        res.status(200).json(isValid);
        
    } catch (error) {
        console.error('Validation error:', error);
        res.status(500).json(false);
    }
});

// GET /payments - Get all payments (for testing)
app.get('/payments', (req, res) => {
    res.status(200).json({
        payments: payments,
        count: payments.length
    });
});

// GET /transactions - Get all transactions (for testing)
app.get('/transactions', (req, res) => {
    res.status(200).json({
        transactions: transactions,
        count: transactions.length
    });
});

// GET /health - Health check
app.get('/health', (req, res) => {
    res.status(200).json({
        status: 'healthy',
        timestamp: new Date().toISOString(),
        uptime: process.uptime()
    });
});

// Error handling middleware
app.use((err, req, res, next) => {
    console.error('Unhandled error:', err);
    res.status(500).json({
        success: false,
        message: 'Internal server error'
    });
});

// 404 handler
app.use('*', (req, res) => {
    res.status(404).json({
        success: false,
        message: 'Endpoint not found'
    });
});

// Start server
app.listen(PORT, () => {
    console.log(`🚀 Cashi Backend API server running on port ${PORT}`);
    console.log(`📊 Health check: http://localhost:${PORT}/health`);
    console.log(`💳 Payments endpoint: http://localhost:${PORT}/payments`);
    console.log(`✅ Validation endpoint: http://localhost:${PORT}/validate`);
});

module.exports = app;
