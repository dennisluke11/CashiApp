#!/bin/bash

# Cleanup temporary iOS documentation files
echo "🧹 Cleaning up temporary files..."

# Remove temporary iOS documentation
rm -f BUILD_IOS_FRAMEWORK.md
rm -f FIREBASE_IOS_SETUP.md  
rm -f HOW_TO_USE_SHARED_KOTLIN_IN_IOS.md
rm -f KMP_IOS_EXPLANATION.md
rm -f RUN_IOS_NOW.md
rm -f iOS_QUICK_START.md
rm -f iOS_XCODE_SETUP.md
rm -f XCODE_KMP_INTEGRATION.md

# Remove temporary Swift files
rm -f SWIFT_USING_KOTLIN_EXAMPLE.swift

# Remove temporary iOS source files
rm -rf iosApp_swift_files/
rm -rf iosApp_backup/

# Clean build artifacts (but keep them for now - uncomment if you want full clean)
# ./gradlew clean

echo "✅ Cleanup complete!"
echo ""
echo "Remaining files ready for commit:"
ls -1 *.md *.sh 2>/dev/null

