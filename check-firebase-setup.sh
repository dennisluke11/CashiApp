#!/bin/bash

# Firebase Setup Checker for Cashi Mobile App
# This script checks if Firebase is properly configured

echo "🔍 Checking Firebase Configuration..."
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Check if google-services.json exists
echo "1. Checking for google-services.json..."
if [ -f "androidApp/google-services.json" ]; then
    if grep -q "project_id" androidApp/google-services.json && ! grep -q "_comment" androidApp/google-services.json; then
        echo -e "${GREEN}✓${NC} google-services.json exists and appears to be configured"
    else
        echo -e "${RED}✗${NC} google-services.json is still a template. Please download your actual config from Firebase Console."
        echo "  📖 See FIREBASE_CONNECTION_SETUP.md for instructions"
    fi
else
    echo -e "${RED}✗${NC} google-services.json not found!"
    echo "  📖 See FIREBASE_CONNECTION_SETUP.md for instructions"
fi

echo ""

# Check if CashiApplication exists
echo "2. Checking for CashiApplication.kt..."
if [ -f "androidApp/src/main/java/com/cashi/androidapp/CashiApplication.kt" ]; then
    echo -e "${GREEN}✓${NC} CashiApplication.kt exists"
else
    echo -e "${RED}✗${NC} CashiApplication.kt not found!"
fi

echo ""

# Check AndroidManifest for Application class
echo "3. Checking AndroidManifest.xml..."
if grep -q "android:name=\".CashiApplication\"" androidApp/src/main/AndroidManifest.xml; then
    echo -e "${GREEN}✓${NC} AndroidManifest.xml references CashiApplication"
else
    echo -e "${RED}✗${NC} AndroidManifest.xml doesn't reference CashiApplication"
fi

echo ""

# Check for Firebase dependencies in build.gradle
echo "4. Checking for Firebase dependencies..."
if grep -q "firebase.firestore\|firebase.bom" androidApp/build.gradle.kts; then
    echo -e "${GREEN}✓${NC} Firebase Firestore dependency found"
else
    echo -e "${RED}✗${NC} Firebase Firestore dependency not found in androidApp/build.gradle.kts"
fi

echo ""

# Check for google-services plugin
echo "5. Checking for Google Services plugin..."
if grep -q "google.services" androidApp/build.gradle.kts; then
    echo -e "${GREEN}✓${NC} Google Services plugin configured"
else
    echo -e "${RED}✗${NC} Google Services plugin not found in androidApp/build.gradle.kts"
fi

echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

# Summary
echo "📋 Summary:"
echo "  • If all items are green ✓, your Firebase setup is complete!"
echo "  • If any items are red ✗, please follow the instructions above"
echo "  • Read FIREBASE_CONNECTION_SETUP.md for detailed setup guide"
echo ""
echo "🚀 Next Steps:"
echo "  1. Go to https://console.firebase.google.com/"
echo "  2. Create a project or select existing one"
echo "  3. Add Android app with package: com.cashi.androidapp"
echo "  4. Download google-services.json"
echo "  5. Replace the template file in androidApp/"
echo "  6. Enable Firestore Database in Firebase Console"
echo "  7. Run the app and start sending payments!"
echo ""

