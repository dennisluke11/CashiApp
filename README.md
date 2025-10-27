# Cashi Mobile App - Kotlin Multiplatform Payment Application

A comprehensive FinTech mobile application built with Kotlin Multiplatform (KMP) that allows users to send payments to recipients. The app demonstrates modern mobile development practices including MVVM architecture, dependency injection with Koin, Firebase integration, and comprehensive testing.

## 🏗️ Architecture Overview

The application follows a clean architecture pattern with the following layers:

- **Presentation Layer**: Jetpack Compose UI with ViewModels
- **Domain Layer**: Use cases and business logic
- **Data Layer**: Repositories, API clients, and Firebase integration
- **Shared Module**: Cross-platform business logic using KMP

## 🚀 Features

### Core Functionality
- **Send Payment**: Users can send payments by entering recipient email, amount, and currency
- **Transaction History**: View all past transactions with real-time updates
- **Payment Validation**: Client-side and server-side validation
- **Multi-Currency Support**: USD, EUR, GBP currencies

### Technical Features
- **Kotlin Multiplatform**: Shared business logic between platforms
- **MVVM Architecture**: Clean separation of concerns
- **Dependency Injection**: Koin for dependency management
- **Firebase Integration**: Firestore for transaction storage
- **REST API**: Mock backend for payment processing
- **Comprehensive Testing**: Unit, BDD, and performance tests

## 📱 Screenshots

### Payment Screen
Send payments with:
- Recipient email input
- Amount input
- Currency selection (USD, EUR, GBP)
- Real-time validation
- Success/error feedback
<img width="350" height="500" alt="Screenshot 2025-10-27 at 15 18 53" src="https://github.com/user-attachments/assets/6cef7fe4-659b-4bea-8ef8-da4c5c7cc168" />

### Transaction History Screen
View all past transactions:
- Recipient email
- Amount and currency
- Timestamp
- Status (Completed, Pending, Failed)
- Real-time updates from Firestore
<img width="308" height="500" alt="Screenshot 2025-10-27 at 15 20 56" src="https://github.com/user-attachments/assets/353441a5-b7be-4f60-8957-ba02d2075b25" />

## 🛠️ Tech Stack

### Frontend (Android)
- **Kotlin Multiplatform**: Cross-platform shared code
- **Jetpack Compose**: Modern Android UI toolkit
- **Navigation Compose**: Screen navigation
- **Material Design 3**: Modern design system

### Backend & Data
- **Retrofit**: HTTP client for API calls with OkHttp
- **Firebase Firestore**: Cloud database for transactions
- **Node.js/Express**: Mock backend API server
- **JSON**: Data serialization with Kotlinx Serialization

### Dependency Injection & Architecture
- **Koin**: Dependency injection framework
- **MVVM**: Model-View-ViewModel pattern
- **Repository Pattern**: Data access abstraction
- **Use Cases**: Business logic encapsulation

### Testing
- **Kotest**: Unit testing framework
- **Cucumber**: Behavior-driven development
- **MockK**: Mocking framework
- **JMeter**: Performance testing
- **Appium**: UI automation (optional)

## 📋 Prerequisites

- Android Studio Hedgehog or later
- JDK 8 or later
- Node.js 16+ (for backend)
- Firebase project setup
- Android device/emulator

## 🔧 Setup Instructions

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Cashi_Mobile_App
```

### 2. Backend Setup
```bash
cd backend
npm install
npm start
```
The backend will run on `http://localhost:3000`

**Note**: For physical devices, update the IP address in `shared/src/androidMain/kotlin/com/cashi/shared/data/remote/PaymentApiService.android.kt` (line 71)

### 3. Firebase Setup
1. Create a new Firebase project at [Firebase Console](https://console.firebase.google.com/)
2. Enable Firestore Database
3. Download `google-services.json` and place it in `androidApp/` folder
4. The app will connect automatically

### 4. Android App Setup
1. Open the project in Android Studio
2. Sync Gradle files
3. Run the app on an Android device or emulator

### 📖 For Detailed Setup Instructions
See [SETUP_GUIDE.md](SETUP_GUIDE.md) for complete step-by-step instructions

## 🏃‍♂️ Running the Application

### Start Backend Server
```bash
cd backend
npm start
```

### Run Android App
1. Open Android Studio
2. Select the `androidApp` run configuration
3. Click Run or use `Ctrl+R`

### Test the App
1. Enter a valid email address
2. Enter an amount (e.g., 100.0)
3. Select a currency (USD, EUR, GBP)
4. Tap "Send Payment"
5. View transaction history by tapping the history icon

## 🧪 Testing

### Unit Tests
```bash
./gradlew :shared:test
```

### BDD Tests (Cucumber)
```bash
./gradlew :shared:test --tests "*CucumberTest"
```

### Performance Tests (JMeter)
1. Install JMeter
2. Open `backend/jmeter/Cashi_Payment_API_Performance_Test.jmx`
3. Run the test plan
4. View results in the Summary Report

### UI Tests (Appium) - Optional
```bash
# Setup Appium and run UI tests
appium --port 4723
# Run Appium tests
```

## 📊 API Endpoints

### Payment Processing
- **POST** `/payments` - Process a payment
- **POST** `/validate` - Validate payment request

### Testing & Monitoring
- **GET** `/health` - Health check
- **GET** `/payments` - Get all payments (testing)
- **GET** `/transactions` - Get all transactions (testing)

## 🏗️ Project Structure

```
Cashi_Mobile_App/
├── shared/                          # KMP shared module
│   ├── src/
│   │   ├── commonMain/kotlin/
│   │   │   ├── data/                 # Models, API client, Firebase
│   │   │   ├── domain/               # Use cases, repositories
│   │   │   ├── presentation/         # ViewModels, UI state
│   │   │   └── di/                   # Koin modules
│   │   ├── androidMain/kotlin/       # Android-specific implementations
│   │   └── commonTest/kotlin/        # Shared tests
├── androidApp/                       # Android UI module
│   ├── src/main/java/
│   │   ├── ui/screens/               # Compose screens
│   │   └── ui/theme/                 # Material Design theme
│   └── src/main/res/                 # Android resources
├── backend/                          # Mock API server
│   ├── server.js                     # Express server
│   ├── package.json                  # Node.js dependencies
│   └── jmeter/                       # Performance tests
└── docs/                             # Documentation
```

## 🔄 KMP Architecture Benefits

### Cross-Platform Potential
The shared module contains:
- **Data Models**: Payment, Transaction, PaymentRequest
- **Business Logic**: Payment validation, use cases
- **API Client**: HTTP communication with Ktor
- **Repository Pattern**: Data access abstraction
- **ViewModels**: State management

### Platform-Specific Implementations
- **Android**: Firebase SDK, Android HTTP client
- **iOS** (future): iOS Firebase SDK, iOS HTTP client
- **Desktop** (future): Desktop-specific implementations

## 🧪 Testing Strategy

### 1. Unit Tests
- **Use Cases**: Business logic testing
- **Repositories**: Data access testing
- **API Client**: HTTP communication testing
- **ViewModels**: State management testing

### 2. BDD Tests (Cucumber)
- **Payment Flow**: End-to-end payment scenarios
- **Validation**: Input validation scenarios
- **Transaction History**: Data retrieval scenarios

### 3. Integration Tests
- **API Integration**: Backend communication
- **Firebase Integration**: Database operations
- **End-to-End**: Complete user flows

### 4. Performance Tests (JMeter)
- **Load Testing**: 5 concurrent users
- **Response Time**: API performance metrics
- **Throughput**: Transactions per second

## 🚀 Deployment

### Backend Deployment
The backend can be deployed to:
- **Heroku**: `git push heroku main`
- **Render**: Connect GitHub repository
- **Railway**: Deploy with one click
- **AWS/GCP**: Container deployment

### Android App Deployment
1. Generate signed APK/AAB
2. Upload to Google Play Store
3. Configure Firebase for production

## 🔧 Configuration

### Environment Variables
```bash
# Backend
PORT=3000
NODE_ENV=development

# Firebase
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_API_KEY=your-api-key
```

### API Configuration
Update the base URL in `PaymentApiClient.kt`:
```kotlin
private const val BASE_URL = "https://your-api-domain.com"
```

## 📈 Performance Metrics

### Target Metrics
- **Response Time**: < 2 seconds for payment processing
- **Throughput**: 100+ transactions per minute
- **Availability**: 99.9% uptime
- **Error Rate**: < 1%

### Monitoring
- **Health Checks**: `/health` endpoint
- **Logging**: Structured logging with timestamps
- **Metrics**: Response time, error rate, throughput

## 🆘 Troubleshooting

### Common Issues

1. **Firebase Connection Issues**
   - Verify `google-services.json` is in the correct location
   - Check Firebase project configuration
   - Ensure Firestore is enabled

2. **Backend Connection Issues**
   - Verify backend is running on port 3000
   - Check network connectivity
   - Verify API endpoints are accessible

3. **Build Issues**
   - Clean and rebuild the project
   - Verify all dependencies are installed
   - Check Android SDK version compatibility

### Support
For issues and questions:
- Create an issue in the repository
- Check the troubleshooting section
- Review the API documentation

## 🎯 Future Enhancements

- **iOS Support**: Complete iOS implementation
- **Desktop Support**: Windows/Mac/Linux apps
- **Real Payment Integration**: Stripe/PayPal integration
- **Push Notifications**: Real-time payment updates
- **Biometric Authentication**: Fingerprint/Face ID
- **Offline Support**: Local data caching
- **Multi-language Support**: Internationalization
