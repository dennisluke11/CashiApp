# Starting the Backend Server

The app connects to the backend API at `http://10.0.2.2:3000` (Android emulator localhost).

## Start the Backend

```bash
cd backend
npm install  # If you haven't already
npm start
```

You should see:
```
🚀 Cashi Backend API server running on port 3000
📊 Health check: http://localhost:3000/health
💳 Payments endpoint: http://localhost:3000/payments
✅ Validation endpoint: http://localhost:3000/validate
```

## Test the Connection

Visit these URLs in your browser to verify:
- http://localhost:3000/health
- http://localhost:3000/payments (should return empty array)

## Run the App

After the backend is running:

```bash
# Build the app
./gradlew :androidApp:assembleDebug

# Install on emulator
adb install androidApp/build/outputs/apk/debug/androidApp-debug.apk

# Or run from Android Studio
```

Now the app can communicate with the backend API!

## Port Configuration

The app is configured to connect to:
- **Emulator**: `http://10.0.2.2:3000` (10.0.2.2 is emulator's localhost)
- **Physical Device**: Update to your computer's IP address

### For Physical Device

If testing on a physical device, update `RetrofitClientFactory.android.kt`:

```kotlin
// In shared/src/androidMain/kotlin/.../RetrofitClientFactory.android.kt
val retrofit = Retrofit.Builder()
    .baseUrl("http://YOUR_COMPUTER_IP:3000/") // e.g., "http://192.168.1.100:3000/"
    .client(okHttpClient)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
```

Find your computer's IP:
```bash
# macOS/Linux
ifconfig | grep "inet "

# Windows
ipconfig
```


