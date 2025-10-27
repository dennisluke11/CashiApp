#!/bin/bash

# Verify that google-services.json is a real Firebase config

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "🔍 Verifying google-services.json..."
echo ""

FILE="androidApp/google-services.json"

if [ ! -f "$FILE" ]; then
    echo -e "${RED}✗${NC} File not found: $FILE"
    echo "📥 Download it from: https://console.firebase.google.com/"
    exit 1
fi

# Check if it's still the template
if grep -q "_comment" "$FILE" 2>/dev/null; then
    echo -e "${RED}✗${NC} This is still the template file!"
    echo ""
    echo "📋 You need to:"
    echo "   1. Go to https://console.firebase.google.com/"
    echo "   2. Create/select your Firebase project"
    echo "   3. Add Android app with package: com.cashi.androidapp"
    echo "   4. Download google-services.json"
    echo "   5. Replace the file at: $FILE"
    echo ""
    echo "📖 See GET_GOOGLE_SERVICES.md for detailed instructions"
    exit 1
fi

# Check if it has real Firebase structure
if grep -q "project_info" "$FILE" && grep -q "client_info" "$FILE"; then
    echo -e "${GREEN}✓${NC} File exists and has proper structure"
    
    # Extract project info
    PROJECT_ID=$(grep -A 2 "project_info" "$FILE" | grep "project_id" | cut -d'"' -f4 | head -1)
    PROJECT_NUMBER=$(grep -A 3 "project_info" "$FILE" | grep "project_number" | cut -d'"' -f4 | head -1)
    PACKAGE_NAME=$(grep -A 3 "android_client_info" "$FILE" | grep "package_name" | cut -d'"' -f4 | head -1)
    
    echo -e "${GREEN}✓${NC} Project ID: $PROJECT_ID"
    echo -e "${GREEN}✓${NC} Project Number: $PROJECT_NUMBER"
    echo -e "${GREEN}✓${NC} Package: $PACKAGE_NAME"
    
    if [ "$PACKAGE_NAME" = "com.cashi.androidapp" ]; then
        echo ""
        echo -e "${GREEN}✅ All good! Your Firebase config is valid.${NC}"
        echo ""
        echo "🚀 Next steps:"
        echo "   1. Enable Firestore Database in Firebase Console"
        echo "   2. Run: ./gradlew :androidApp:build"
        echo "   3. Run the app and test!"
    else
        echo -e "${YELLOW}⚠️${NC} Package name mismatch!"
        echo "   Expected: com.cashi.androidapp"
        echo "   Found: $PACKAGE_NAME"
    fi
else
    echo -e "${RED}✗${NC} File doesn't have valid Firebase structure"
    echo "This doesn't look like a real google-services.json file"
    exit 1
fi


