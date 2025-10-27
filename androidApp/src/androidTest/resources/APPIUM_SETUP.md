# Appium Test Configuration for Cashi Mobile App

## Prerequisites

1. **Android SDK** installed and configured
2. **Android Emulator** running or physical device connected
3. **Appium Server** installed globally: `npm install -g appium`
4. **UiAutomator2 Driver**: `appium driver install uiautomator2`
5. **Cashi App** built and installed on device/emulator

## Setup Instructions

### 1. Install Appium and Dependencies
```bash
# Install Appium globally
npm install -g appium

# Install UiAutomator2 driver
appium driver install uiautomator2

# Install Appium Doctor to verify setup
npm install -g appium-doctor
appium-doctor --android
```

### 2. Start Android Emulator
```bash
# List available AVDs
emulator -list-avds

# Start emulator (replace with your AVD name)
emulator -avd Pixel_7_API_34
```

### 3. Build and Install App
```bash
# Build debug APK
./gradlew :androidApp:assembleDebug

# Install on emulator
adb install androidApp/build/outputs/apk/debug/androidApp-debug.apk
```

### 4. Start Appium Server
```bash
# Start Appium server
appium --port 4723

# Or with specific port
appium --port 4723 --log-level debug
```

### 5. Run Tests
```bash
# Run Appium tests
./gradlew :androidApp:connectedAndroidTest

# Or run specific test class
./gradlew :androidApp:connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.cashi.androidapp.ui.tests.CashiAppiumUITest
```

## Test Configuration

### Appium Capabilities
- **Platform**: Android
- **Device**: Android Emulator
- **App Package**: com.cashi.androidapp
- **App Activity**: com.cashi.androidapp.MainActivity
- **Automation**: UiAutomator2
- **Server Port**: 4723

### Test Scenarios Covered
1. **Send Payment - Valid Details**
   - Enter valid email, amount, currency
   - Submit payment
   - Verify success message
   - Verify form is cleared

2. **Send Payment - Invalid Email**
   - Enter invalid email format
   - Submit payment
   - Verify error message

3. **Send Payment - Negative Amount**
   - Enter negative amount
   - Submit payment
   - Verify error message

4. **View Transaction History**
   - Navigate to history screen
   - Verify transactions or empty state
   - Navigate back

5. **Complete Payment Flow**
   - Send payment
   - Navigate to history
   - Verify transaction appears
   - Navigate back

6. **Currency Selection**
   - Test USD, EUR, GBP selection
   - Verify currency changes

7. **Form Validation**
   - Test empty field validation
   - Verify error messages

## Troubleshooting

### Common Issues

1. **"Device not found"**
   - Ensure emulator is running: `adb devices`
   - Check device name in capabilities

2. **"App not installed"**
   - Build and install app: `./gradlew :androidApp:installDebug`
   - Check package name in capabilities

3. **"Element not found"**
   - Increase wait timeouts
   - Check element locators
   - Use Appium Inspector to verify elements

4. **"Appium server not responding"**
   - Restart Appium server
   - Check port availability
   - Verify server logs

### Debug Commands
```bash
# Check connected devices
adb devices

# Check installed packages
adb shell pm list packages | grep cashi

# View app logs
adb logcat | grep -i cashi

# Take screenshot
adb shell screencap /sdcard/screenshot.png
adb pull /sdcard/screenshot.png

# Start Appium Inspector
appium --port 4723 --log-level debug
```

## Test Data

### Test Emails
- Valid: `test@example.com`, `user.name@domain.co.uk`
- Invalid: `invalid-email`, `@example.com`, `test@`

### Test Amounts
- Valid: `100.0`, `0.01`, `999999.99`
- Invalid: `-100.0`, `0.0`

### Test Currencies
- Supported: `USD`, `EUR`, `GBP`
- Unsupported: `INVALID`, `CAD`, `JPY`

## Performance Considerations

- **Wait Timeouts**: 10 seconds for element waits
- **Command Timeout**: 60 seconds
- **Screenshot**: Enabled for debugging
- **Logging**: Debug level for troubleshooting

## CI/CD Integration

### GitHub Actions Example
```yaml
- name: Setup Android SDK
  uses: android-actions/setup-android@v2

- name: Start Android Emulator
  uses: reactivecircus/android-emulator-runner@v2
  with:
    api-level: 34
    target: google_apis
    arch: x86_64
    profile: Nexus 6

- name: Install Appium
  run: |
    npm install -g appium
    appium driver install uiautomator2

- name: Run Appium Tests
  run: ./gradlew :androidApp:connectedAndroidTest
```

## Best Practices

1. **Element Locators**: Use stable locators (ID, accessibility ID)
2. **Wait Strategies**: Use explicit waits instead of sleep
3. **Test Data**: Use consistent test data across tests
4. **Cleanup**: Ensure proper test cleanup
5. **Screenshots**: Take screenshots on test failures
6. **Logging**: Enable detailed logging for debugging

---

**Note**: These tests require the backend server to be running on `http://localhost:3000` for successful payment processing.
