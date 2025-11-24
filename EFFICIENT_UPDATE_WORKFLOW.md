# BrainOps Weather - Efficient Update Workflow

## 🚀 Quick Start: Making Updates

You can now efficiently iterate on BrainOps Weather improvements with a streamlined workflow.

---

## One-Command Release

```bash
cd /home/matt-woodworth/dev/BrainOps-Weather
./release.sh
```

**That's it!** The script will:
1. ✅ Ask for version number (e.g., 1.0.2, 1.1.0, 2.0.0)
2. ✅ Ask for release notes
3. ✅ Build and sign the APK
4. ✅ Commit version change
5. ✅ Create git tag
6. ✅ Push to GitHub
7. ✅ Create GitHub release
8. ✅ Mark as "latest"

**Time: ~2 minutes**

---

## Workflow for Improvements

### 1. Make Your Changes
```bash
cd /home/matt-woodworth/dev/BrainOps-Weather

# Edit code, add features, fix bugs
# Example: Update branding, add features, fix issues
```

### 2. Test Locally (Optional)
```bash
# Build debug version
./gradlew assembleBasicDebug

# Install on device via USB
adb install app/build/outputs/apk/basic/debug/app-basic-universal-debug.apk
```

### 3. Release
```bash
./release.sh
```

Follow prompts:
- **Version**: `1.0.2` (or whatever increment makes sense)
- **Notes**: "Fixed weather radar loading, improved performance"

### 4. Install on Device

**On your S25 Ultra:**
1. Open browser: https://github.com/mwwoodworth/BrainOps-Weather/releases/latest
2. Download the APK
3. Tap to install
4. ✅ Updates over existing app (settings preserved!)

---

## Version Numbering

Use semantic versioning: `MAJOR.MINOR.PATCH`

### Examples:

**Patch (1.0.1 → 1.0.2):**
- Bug fixes
- Small improvements
- Performance tweaks
- UI polish

**Minor (1.0.2 → 1.1.0):**
- New features
- Significant improvements
- New capabilities
- Backwards compatible

**Major (1.1.0 → 2.0.0):**
- Major redesign
- Breaking changes
- Complete rewrites
- Fundamentally different

---

## Common Update Scenarios

### Scenario 1: Quick Bug Fix

```bash
# Fix the bug in code
vim app/src/main/java/...

# Test locally (optional)
./gradlew assembleBasicDebug
adb install -r app/build/outputs/apk/basic/debug/app-basic-universal-debug.apk

# Release
./release.sh
# Version: 1.0.2
# Notes: Fixed crash on weather data refresh
```

**Time: 5 minutes**

### Scenario 2: Add New Feature

```bash
# Implement feature
# ... code changes ...

# Test thoroughly
./gradlew assembleBasicDebug
adb install -r app/build/outputs/apk/basic/debug/app-basic-universal-debug.apk

# Release
./release.sh
# Version: 1.1.0
# Notes: Added weather alerts customization and notification preferences
```

**Time: 15-30 minutes**

### Scenario 3: Major Update

```bash
# Implement multiple changes
# ... extensive code changes ...

# Test extensively
# ... testing ...

# Release
./release.sh
# Version: 2.0.0
# Notes: Complete UI redesign with Material You 3.0 and enhanced AI integration
```

**Time: 1-2 hours**

---

## Update on Your S25 Ultra

### Method 1: Direct Download (Easiest)

1. **Open browser** on phone
2. **Navigate to**: https://github.com/mwwoodworth/BrainOps-Weather/releases/latest
3. **Tap** the APK file to download
4. **Tap** the downloaded APK in notifications
5. **Tap "Install"**
6. **Done!** Settings and locations preserved

### Method 2: QR Code

```bash
# Generate QR code for latest release
qrencode -t ANSIUTF8 "https://github.com/mwwoodworth/BrainOps-Weather/releases/latest"

# Or use online: https://www.qr-code-generator.com/
```

Scan with phone camera → Download → Install

### Method 3: USB Transfer

```bash
# Copy to phone via USB
adb push app-basic-universal-release-signed-v1.0.2.apk /sdcard/Download/

# On phone: Files app → Downloads → Tap APK → Install
```

---

## Automation Features

### The `release.sh` Script Does:

✅ **Version Management**
- Automatically updates `app_version` in strings.xml
- Creates git tag
- Maintains version history

✅ **Building**
- Cleans previous builds
- Builds release APK
- Signs with keystore
- Verifies signature

✅ **Quality Checks**
- Generates SHA-256 checksum
- Verifies APK integrity
- Shows file size

✅ **Distribution**
- Commits version change
- Pushes to GitHub
- Creates release
- Marks as "latest"
- Generates release notes

✅ **Documentation**
- Auto-generates release notes
- Includes checksums
- Provides install instructions

---

## Future Enhancements (Roadmap)

### Phase 1: Manual (Current) ✅
- ✅ One-command release script
- ✅ GitHub releases
- ✅ Manual download and install
- ✅ Settings preserved on update

### Phase 2: In-App Checker (Next)
- ⏳ App checks GitHub API for updates
- ⏳ Notification when update available
- ⏳ One-tap download
- ⏳ Auto-install prompt

### Phase 3: Auto-Update (Future)
- ⏳ Background update checks
- ⏳ Silent download
- ⏳ Install on next launch
- ⏳ Update changelog in-app

---

## Iteration Best Practices

### 1. Make Small, Frequent Updates
- Better than large, infrequent releases
- Easier to test and debug
- Faster user feedback
- Lower risk

### 2. Test Before Releasing
```bash
# Always test debug build first
./gradlew assembleBasicDebug
adb install -r app/build/outputs/apk/basic/debug/app-basic-universal-debug.apk
# Use the app, verify changes work
```

### 3. Write Clear Release Notes
Good:
```
Fixed weather radar not loading in offline mode
Improved battery usage by 15%
Added support for custom alert sounds
```

Bad:
```
Bug fixes and improvements
```

### 4. Use Version Numbers Meaningfully
- 1.0.x = Patches and fixes
- 1.x.0 = New features
- x.0.0 = Major changes

### 5. Keep Releases Focused
- One release = One purpose
- Bug fix release? Just bugs
- Feature release? Just features
- Don't mix major changes with bug fixes

---

## Troubleshooting

### "APK not found" Error

```bash
# Make sure you're in the right directory
cd /home/matt-woodworth/dev/BrainOps-Weather

# Clean and rebuild
./gradlew clean
./gradlew assembleBasicRelease
```

### "Signature verification failed"

```bash
# Check keystore exists
ls -la ~/.android/debug.keystore

# If missing, create it:
keytool -genkey -v -keystore ~/.android/debug.keystore \
  -alias androiddebugkey -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass android -keypass android
```

### "Permission denied" on release.sh

```bash
chmod +x release.sh
```

### Update Not Installing on Phone

Make sure:
1. ✅ "Install from unknown sources" enabled for your browser
2. ✅ Previous version was signed with same key
3. ✅ Package name hasn't changed (org.breezyweather)

---

## Quick Reference Commands

```bash
# Make improvements
cd /home/matt-woodworth/dev/BrainOps-Weather
# ... edit code ...

# Test debug build
./gradlew assembleBasicDebug
adb install -r app/build/outputs/apk/basic/debug/app-basic-universal-debug.apk

# Release new version
./release.sh

# Check latest release
gh release list --limit 1

# View releases on GitHub
open https://github.com/mwwoodworth/BrainOps-Weather/releases
```

---

## Update Frequency Recommendations

### Bug Fixes
- **When**: As soon as critical bugs are found
- **Frequency**: Immediately for crashes, within days for minor bugs
- **Version**: Patch increment (1.0.1 → 1.0.2)

### Features
- **When**: After testing and validation
- **Frequency**: Weekly or bi-weekly
- **Version**: Minor increment (1.0.2 → 1.1.0)

### Major Updates
- **When**: After significant development and extensive testing
- **Frequency**: Monthly or quarterly
- **Version**: Major increment (1.1.0 → 2.0.0)

---

## Summary: Your Efficient Workflow

**Before (Complex):**
1. Edit gradle files
2. Run gradlew commands manually
3. Find APK in nested folders
4. Sign with long command
5. Manually create git tag
6. Push to GitHub
7. Manually create release
8. Write release notes
9. Upload APK
10. Update documentation

**After (Simple):**
1. Make changes
2. Run `./release.sh`
3. Answer 2 prompts
4. Done! ✅

**Time Saved:** 15 minutes → 2 minutes (87% faster)

---

## You're Ready to Iterate!

Your BrainOps Weather app now has an **efficient update workflow**:

✅ One-command releases
✅ Automatic versioning
✅ GitHub integration
✅ Install-over-existing (settings preserved)
✅ Clear documentation

**To make your first update:**

```bash
cd /home/matt-woodworth/dev/BrainOps-Weather
# Make some improvements
./release.sh
```

That's it! Efficient iteration enabled. 🚀

---

*Last Updated: 2025-11-23*
*Current Version: 1.0.1*
*Next: Make improvements and release 1.0.2!*
