#!/bin/bash
# BrainOps Weather - Quick Release Script
# Makes it easy to iterate and release new versions

set -e  # Exit on error

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 BrainOps Weather Quick Release${NC}"
echo "=================================="

# Get current version from build.gradle.kts
CURRENT_VERSION=$(grep "versionName = " app/build.gradle.kts | sed 's/.*"\(.*\)".*/\1/')
echo -e "${GREEN}Current version: ${CURRENT_VERSION}${NC}"

# Ask for new version
read -p "Enter new version (e.g., 1.0.2): " NEW_VERSION

if [ -z "$NEW_VERSION" ]; then
    echo -e "${RED}❌ Version required${NC}"
    exit 1
fi

# Ask for release notes
read -p "Release notes (brief description): " RELEASE_NOTES

if [ -z "$RELEASE_NOTES" ]; then
    RELEASE_NOTES="BrainOps Weather v${NEW_VERSION}"
fi

echo ""
echo -e "${BLUE}📝 Release Plan:${NC}"
echo "   Version: v${NEW_VERSION}"
echo "   Notes: ${RELEASE_NOTES}"
echo ""
read -p "Continue? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cancelled."
    exit 1
fi

# Update version in strings.xml
echo -e "${BLUE}1. Updating version in strings.xml...${NC}"
sed -i "s/<string name=\"app_version\">.*<\/string>/<string name=\"app_version\">${NEW_VERSION}<\/string>/" app/src/main/res/values/strings.xml

# Clean and build release APK
echo -e "${BLUE}2. Building release APK...${NC}"
./gradlew clean
./gradlew assembleBasicRelease

# Find the built APK
APK_PATH="app/build/outputs/apk/basic/release/app-basic-universal-release-unsigned.apk"
SIGNED_APK="app-basic-universal-release-signed-v${NEW_VERSION}.apk"

if [ ! -f "$APK_PATH" ]; then
    echo -e "${RED}❌ APK not found at ${APK_PATH}${NC}"
    exit 1
fi

# Sign the APK with release keystore (brainops-release.jks)
echo -e "${BLUE}3. Signing APK...${NC}"

# Load keystore credentials
KEYSTORE_PATH="./brainops-release.jks"
KEYSTORE_PASS=$(grep -i "Password" KEYSTORE_PASSWORD.txt 2>/dev/null | awk '{print $2}')
KEY_ALIAS=$(grep -i "Alias" KEYSTORE_PASSWORD.txt 2>/dev/null | awk '{print $2}')

if [ -z "$KEYSTORE_PASS" ] || [ -z "$KEY_ALIAS" ]; then
    echo -e "${RED}❌ Keystore credentials not found in KEYSTORE_PASSWORD.txt${NC}"
    exit 1
fi

apksigner sign \
    --ks "$KEYSTORE_PATH" \
    --ks-key-alias "$KEY_ALIAS" \
    --ks-pass "pass:${KEYSTORE_PASS}" \
    --key-pass "pass:${KEYSTORE_PASS}" \
    --out "$SIGNED_APK" \
    "$APK_PATH"

# Verify signature
echo -e "${BLUE}4. Verifying signature...${NC}"
apksigner verify "$SIGNED_APK"

# Get file size and checksum
FILE_SIZE=$(ls -lh "$SIGNED_APK" | awk '{print $5}')
SHA256=$(sha256sum "$SIGNED_APK" | awk '{print $1}')

echo ""
echo -e "${GREEN}✅ APK built and signed successfully${NC}"
echo "   File: ${SIGNED_APK}"
echo "   Size: ${FILE_SIZE}"
echo "   SHA-256: ${SHA256}"
echo ""

# Commit version change
echo -e "${BLUE}5. Committing version update...${NC}"
git add app/src/main/res/values/strings.xml
git commit -m "chore: Bump version to ${NEW_VERSION}" || echo "No changes to commit"

# Create git tag
echo -e "${BLUE}6. Creating git tag...${NC}"
git tag -a "v${NEW_VERSION}" -m "${RELEASE_NOTES}"

# Push to GitHub
echo -e "${BLUE}7. Pushing to GitHub...${NC}"
git push origin main
git push origin "v${NEW_VERSION}"

# Create GitHub release
echo -e "${BLUE}8. Creating GitHub release...${NC}"
gh release create "v${NEW_VERSION}" \
    "$SIGNED_APK" \
    --title "BrainOps Weather v${NEW_VERSION}" \
    --notes "${RELEASE_NOTES}

## Installation

Download the APK and install directly on your device. This will update over your existing installation while preserving all settings.

## Checksums

- **SHA-256**: \`${SHA256}\`
- **Size**: ${FILE_SIZE}

## What's New

${RELEASE_NOTES}

---

🧠 Generated with BrainOps AI" \
    --latest

echo ""
echo -e "${GREEN}🎉 Release v${NEW_VERSION} completed successfully!${NC}"
echo ""
echo "📥 Download: https://github.com/mwwoodworth/BrainOps-Weather/releases/latest"
echo "📱 Install: Tap the APK on your device to update"
echo ""
echo -e "${BLUE}Next steps:${NC}"
echo "1. Download the APK on your S25 Ultra"
echo "2. Tap to install (will update over existing)"
echo "3. Settings and locations will be preserved"
echo ""
