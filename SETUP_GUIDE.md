# 🚀 Complete Setup Guide - Run Cashi App with Backend

This guide helps anyone run the complete Cashi app with backend integration.

---

## 📋 Prerequisites

1. **Android Studio** - Latest version
2. **JDK 8+** - Java Development Kit
3. **Node.js 16+** - For backend
4. **npm** - Node package manager
5. **Firebase Project** - For Firestore database

---

## Step 1: Clone the Repository

```bash
git clone <repository-url>
cd Cashi_Mobile_App
```

---

## Step 2: Backend Setup

### 2.1 Install Dependencies
```bash
cd backend
npm install
```

### 2.2 Start Backend Server
```bash
npm start
```

**Backend will run on:**
- Local: http://localhost:3000
- Android Emulator: http://10.0.2.2:3000
- Your Network IP: http://YOUR_IP:3000

### 2.3 Verify Backend is Running
```bash
curl http://localhost:3000/health
# Should return: {"status":"healthy","timestamp":"..."}
```

---

## Step 3: Configure Backend IP for Android App

### Option A: For Android Emulator
The app is already configured for emulator (10.0.2.2).

**No changes needed!**

### Option B: For Physical Device
Update the backend URL in the app:

**File**: `shared/src/androidMain/kotlin/com/cashi/shared/data/remote/PaymentApiService.android.kt`

```kotlin
// Line 71 - Change this:
val retrofit = Retrofit.Builder()
    .baseUrl("http://YOUR_COMPUTER_IP:3000/")  // ← YOUR IP HERE
    
// How to find your IP:
// Mac/Linux: ifconfig | grep "inet "
// Windows: ipconfig
```

---

## Step 4: Firebase Setup

### 4.1 Create Firebase Project
1. Go to https://console.firebase.google.com/
2. Click "Add Project"
3. Name: `cashi-mobile-app`
4. Enable Google Analytics (optional)

### 4.2 Enable Firestore
1. In Firebase Console → **Build** → **Firestore Database**
2. Click **Create Database**
3. Choose **Production Mode** (for development)
4. Select a location

### 4.3 Download `google-services.json`
1. Firebase Console → **Project Settings** (gear icon)
2. Scroll to **Your apps**
3. Click **Add app** → **Android**
4. Package name: `com.cashi.androidapp`
5. Download `google-services.json`

### 4.4 Add to Android App
```bash
# Copy the downloaded file
cp ~/Downloads/google-services.json androidApp/
```

---

## Step 5: Build and Run Android App

### 5.1 Open in Android Studio
```bash
# Open Android Studio
# File → Open → Select Cashi_Mobile_App folder
```

### 5.2 Sync Gradle
- Wait for Gradle sync to complete
- Click **Sync Now** if prompted

### 5.3 Run the App
- Connect Android device or start emulator
- Click **Run** button (▶️) or press `Ctrl+R` / `Cmd+R`

---

## Step 6: Test the App

### Test Payment Sending
1. Enter recipient email: `test@example.com`
2. Enter amount: `100`
3. Select currency: `USD`
4. Tap **Send Payment**
5. Should see success message

### Test Transaction History
1. Tap the list icon (☰) in top right
2. Should see your transaction
3. Shows recipient, amount, currency, timestamp

---

## 🔍 Troubleshooting

### Backend Connection Issues

**Error**: "Failed to connect to localhost:3000"

**Solutions**:
1. **Check backend is running**:
   ```bash
   curl http://localhost:3000/health
   ```

2. **For Physical Device**:
   - Update IP in `PaymentApiService.android.kt`
   - Make sure device and computer are on same WiFi
   - Check firewall isn't blocking port 3000

3. **For Emulator**:
   - Use `10.0.2.2:3000` (already configured)
   - Make sure backend is running on your computer

### Firebase Connection Issues

**Error**: "Firebase not initialized"

**Solutions**:
1. Verify `google-services.json` is in `androidApp/` folder
2. Clean and rebuild: `./gradlew clean`
3. Rebuild: `./gradlew :androidApp:assembleDebug`

### Build Errors

```bash
# Clean build
./gradlew clean

# Rebuild
./gradlew :androidApp:assembleDebug
```

---

## 📊 Network Configuration Summary

| Device Type | Backend URL | Configuration |
|------------|-------------|---------------|
| Emulator | `http://10.0.2.2:3000/` | ✅ Already configured |
| Physical Device | `http://YOUR_IP:3000/` | Update IP in code |
| Local (same machine) | `http://localhost:3000/` | Not for app |

---

## 🎯 Quick Start for Third-Party

```bash
# 1. Clone repo
git clone <repo>
cd Cashi_Mobile_App

# 2. Start backend
cd backend
npm install
npm start
# Keep this terminal open!

# 3. Get your IP (for physical device)
# Mac/Linux: ifconfig | grep "inet "
# Windows: ipconfig

# 4. Update app IP (if using physical device)
# Edit: shared/src/androidMain/.../PaymentApiService.android.kt
# Change baseUrl to your IP

# 5. Add Firebase config
# Download google-services.json from Firebase Console
# Copy to: androidApp/

# 6. Open in Android Studio and Run!
```

---

## ✅ Verification Checklist

- [ ] Backend server running on port 3000
- [ ] Health check working (`curl http://localhost:3000/health`)
- [ ] `google-services.json` in `androidApp/` folder
- [ ] Firebase project created
- [ ] Firestore enabled
- [ ] Gradle sync successful
- [ ] App builds successfully
- [ ] App runs on device/emulator
- [ ] Can send payments
- [ ] Can view transaction history
- [ ] Firebase shows transactions

---

## 🎉 Success!

Once all steps are complete:
- ✅ App connects to backend
- ✅ Payments process successfully
- ✅ Transactions save to Firestore
- ✅ Transaction history displays
- ✅ Real-time updates work

**The app is fully functional!** 🚀

