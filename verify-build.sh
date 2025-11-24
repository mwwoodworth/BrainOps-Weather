#!/bin/bash
# Verify BrainOps Weather Build & Update Flow
# Used in CI/CD to ensure release readiness

set -e

echo "🔍 Verifying Build Environment..."
./gradlew --version

echo "🏗️  Building Release APK (Basic Flavor)..."
# Using assembleBasicRelease as seen in release.sh
./gradlew assembleBasicRelease

APK_PATH="app/build/outputs/apk/basic/release/app-basic-universal-release-unsigned.apk"

if [ ! -f "$APK_PATH" ]; then
    echo "❌ APK build failed. File not found: $APK_PATH"
    exit 1
fi

echo "✅ APK built successfully at $APK_PATH"

# Check if we can sign it (mock run or check for apksigner)
if command -v apksigner &> /dev/null; then
    echo "🔐 apksigner found. Ready for signing."
else
    echo "⚠️  apksigner not found. Skipping signing verification (CI environment?)"
fi

echo "📦 Build Verification Complete."
