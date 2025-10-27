# Cashi Mobile App - Development Setup Script

## Quick Start Script

This script helps you set up the Cashi Mobile App development environment quickly.

### Prerequisites Check
```bash
# Check if required tools are installed
echo "Checking prerequisites..."

# Check Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found. Please install JDK 8 or later."
    exit 1
else
    echo "✅ Java found: $(java -version 2>&1 | head -n 1)"
fi

# Check Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js not found. Please install Node.js 16 or later."
    exit 1
else
    echo "✅ Node.js found: $(node --version)"
fi

# Check npm
if ! command -v npm &> /dev/null; then
    echo "❌ npm not found. Please install npm."
    exit 1
else
    echo "✅ npm found: $(npm --version)"
fi

echo "✅ All prerequisites met!"
```

### Backend Setup
```bash
echo "Setting up backend..."

# Navigate to backend directory
cd backend

# Install dependencies
echo "Installing backend dependencies..."
npm install

# Start backend server
echo "Starting backend server..."
npm start &

# Wait for server to start
sleep 5

# Test backend
echo "Testing backend..."
curl -s http://localhost:3000/health || echo "❌ Backend not responding"

echo "✅ Backend setup complete!"
```

### Android App Setup
```bash
echo "Setting up Android app..."

# Navigate to project root
cd ..

# Check if Android Studio is available
if ! command -v studio &> /dev/null; then
    echo "⚠️  Android Studio not found in PATH. Please open the project manually."
else
    echo "✅ Android Studio found"
fi

# Sync Gradle
echo "Syncing Gradle..."
./gradlew --version

echo "✅ Android app setup complete!"
```

### Firebase Setup
```bash
echo "Setting up Firebase..."

# Check if google-services.json exists
if [ ! -f "androidApp/google-services.json" ]; then
    echo "⚠️  google-services.json not found. Please:"
    echo "   1. Create a Firebase project"
    echo "   2. Add Android app with package: com.cashi.androidapp"
    echo "   3. Download google-services.json"
    echo "   4. Place it in androidApp/ directory"
    echo "   5. Enable Firestore Database"
else
    echo "✅ Firebase configuration found"
fi
```

### Running Tests
```bash
echo "Running tests..."

# Unit tests
echo "Running unit tests..."
./gradlew :shared:test

# Backend tests
echo "Running backend tests..."
cd backend
npm test

echo "✅ All tests completed!"
```

### Development Commands
```bash
echo "Development commands:"
echo ""
echo "Backend:"
echo "  npm start          - Start backend server"
echo "  npm test           - Run backend tests"
echo "  npm run dev        - Start with nodemon"
echo ""
echo "Android:"
echo "  ./gradlew :shared:test                    - Run shared module tests"
echo "  ./gradlew :androidApp:assembleDebug       - Build debug APK"
echo "  ./gradlew :androidApp:installDebug        - Install on device"
echo ""
echo "Testing:"
echo "  ./gradlew :shared:test --tests \"*CucumberTest\"  - Run BDD tests"
echo "  jmeter -t backend/jmeter/Cashi_Payment_API_Performance_Test.jmx  - Performance tests"
echo ""
echo "Firebase:"
echo "  firebase login     - Login to Firebase"
echo "  firebase init      - Initialize Firebase"
echo "  firebase deploy    - Deploy to Firebase"
```

### Troubleshooting
```bash
echo "Common issues and solutions:"
echo ""
echo "1. Backend not starting:"
echo "   - Check if port 3000 is available"
echo "   - Run: lsof -ti:3000 | xargs kill -9"
echo ""
echo "2. Android build failing:"
echo "   - Clean project: ./gradlew clean"
echo "   - Sync Gradle files in Android Studio"
echo ""
echo "3. Firebase connection issues:"
echo "   - Verify google-services.json is in androidApp/"
echo "   - Check Firebase project configuration"
echo "   - Ensure Firestore is enabled"
echo ""
echo "4. Tests failing:"
echo "   - Check backend is running"
echo "   - Verify Firebase configuration"
echo "   - Check network connectivity"
```

### Environment Variables
```bash
echo "Environment variables for production:"
echo ""
echo "Backend:"
echo "  PORT=3000"
echo "  NODE_ENV=production"
echo ""
echo "Firebase:"
echo "  FIREBASE_PROJECT_ID=your-project-id"
echo "  FIREBASE_API_KEY=your-api-key"
echo ""
echo "Android:"
echo "  API_BASE_URL=https://your-api-domain.com"
```

---

**Usage**: Save this as `setup.sh`, make it executable (`chmod +x setup.sh`), and run it (`./setup.sh`) to set up your development environment.
