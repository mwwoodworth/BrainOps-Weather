# BrainOps Weather - Update Mechanism

## ✅ How to Update Right Now

### Option 1: Install Over Existing (RECOMMENDED - NO UNINSTALL NEEDED)

**This preserves all your settings, locations, and preferences!**

1. **Download latest APK** from: https://github.com/mwwoodworth/BrainOps-Weather/releases/latest
2. **Tap the APK** in Downloads
3. **Tap "Install"** - Android will update over existing app
4. **Settings preserved**: Locations, preferences, BrainOps login all kept
5. **Launch** - same app, new version!

### Option 2: Uninstall/Reinstall (NOT RECOMMENDED)

Only if you have issues with Option 1:
1. Uninstall old version
2. Install new version
3. ⚠️ **Lose settings** - must reconfigure

**Always use Option 1 unless troubleshooting!**

---

## 🔄 GitHub "Latest" Release URL

GitHub automatically provides a `:latest` redirect:

### Latest Release Page:
```
https://github.com/mwwoodworth/BrainOps-Weather/releases/latest
```

This ALWAYS redirects to newest release (currently v1.0.1, will auto-update when v1.0.2 released).

### Direct APK Download (Latest):
```
https://github.com/mwwoodworth/BrainOps-Weather/releases/download/v1.0.1/app-basic-universal-release-signed-v1.0.1.apk
```

**Note**: This URL changes with each version. Use GitHub API for programmatic access.

---

## 🤖 Automatic Update Checking (To Be Implemented)

### GitHub API Endpoint:
```
https://api.github.com/repos/mwwoodworth/BrainOps-Weather/releases/latest
```

**Returns JSON with:**
```json
{
  "tag_name": "v1.0.1",
  "name": "BrainOps Weather v1.0.1 - Rebranded",
  "published_at": "2025-11-24T02:02:09Z",
  "assets": [{
    "name": "app-basic-universal-release-signed-v1.0.1.apk",
    "browser_download_url": "https://github.com/mwwoodworth/BrainOps-Weather/releases/download/v1.0.1/app-basic-universal-release-signed-v1.0.1.apk",
    "size": 21823456
  }]
}
```

### Implementation Plan:

**1. Add Update Checker to App** (Kotlin)
```kotlin
// In BrainOpsConfig.kt or UpdateChecker.kt

data class GitHubRelease(
    val tag_name: String,
    val name: String,
    val published_at: String,
    val assets: List<GitHubAsset>
)

data class GitHubAsset(
    val name: String,
    val browser_download_url: String,
    val size: Long
)

suspend fun checkForUpdates(): GitHubRelease? {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url("https://api.github.com/repos/mwwoodworth/BrainOps-Weather/releases/latest")
        .header("Accept", "application/vnd.github.v3+json")
        .build()

    return withContext(Dispatchers.IO) {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = response.body?.string()
                // Parse JSON to GitHubRelease
                parseGitHubRelease(json)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UpdateChecker", "Failed to check for updates", e)
            null
        }
    }
}

fun compareVersions(current: String, latest: String): Boolean {
    // Returns true if latest > current
    val currentParts = current.removePrefix("v").split(".")
    val latestParts = latest.removePrefix("v").split(".")

    for (i in 0 until maxOf(currentParts.size, latestParts.size)) {
        val currentPart = currentParts.getOrNull(i)?.toIntOrNull() ?: 0
        val latestPart = latestParts.getOrNull(i)?.toIntOrNull() ?: 0

        if (latestPart > currentPart) return true
        if (latestPart < currentPart) return false
    }

    return false
}

suspend fun downloadAndInstallUpdate(url: String) {
    // Download APK to Downloads folder
    // Prompt user to install
    // Android will handle installation
}
```

**2. Add Settings Option**
```
Settings → About → Check for Updates
Settings → Auto-update → Check on launch / Daily / Weekly / Never
```

**3. Notification**
```
When update available:
- Show notification: "BrainOps Weather v1.0.2 available"
- Tap to download
- Install automatically (with permission)
```

---

## 📋 Implementation Checklist

### Phase 1: Manual Updates (CURRENT)
- [x] GitHub releases created
- [x] `:latest` URL available
- [x] Users can install over existing
- [x] Documentation provided

### Phase 2: In-App Checker (NEXT RELEASE)
- [ ] Add GitHub API client
- [ ] Add version comparison logic
- [ ] Add "Check for Updates" menu item
- [ ] Show update dialog when available
- [ ] Link to download page

### Phase 3: Auto-Download (FUTURE)
- [ ] Download APK in background
- [ ] Show install prompt
- [ ] Handle installation flow
- [ ] Add auto-check on launch option
- [ ] Add update settings

### Phase 4: Full Auto-Update (IDEAL)
- [ ] Background update service
- [ ] Silent download
- [ ] Install on next launch
- [ ] Rollback capability
- [ ] Update changelog display

---

## 🔒 Security Considerations

### APK Signing
- All releases signed with same key
- SHA-256 checksums provided
- Users can verify before installing

### HTTPS Only
- All downloads over HTTPS
- GitHub CDN trusted
- No man-in-the-middle risk

### User Control
- Never auto-install without prompt
- Show changelog before update
- Allow update deferral
- Respect user preferences

---

## 🎯 Current Recommendation

**For Now (Until In-App Checker Added):**

1. **Bookmark**: https://github.com/mwwoodworth/BrainOps-Weather/releases
2. **Check periodically** for new versions
3. **Download latest APK** when available
4. **Install over existing** (preserves settings)

**Soon (Next Release):**

1. **App will notify** when update available
2. **Tap notification** to download
3. **Install automatically** with one tap
4. **Settings preserved** across updates

---

## 📊 Update Frequency

**Current Plan:**
- **Bug fixes**: As needed (v1.0.x)
- **Minor features**: Monthly (v1.x.0)
- **Major updates**: Quarterly (vx.0.0)

**Example:**
- v1.0.1 (2025-11-23): Rebranding
- v1.0.2 (planned): In-app update checker
- v1.1.0 (planned): Full AI features enabled
- v2.0.0 (planned): Major redesign

---

## 🚀 For Developers

### How to Create a Release:

```bash
# Build release APK
./gradlew assembleBasicRelease

# Sign APK
apksigner sign --ks ~/.android/debug.keystore \
  --ks-key-alias androiddebugkey \
  --ks-pass pass:android \
  --key-pass pass:android \
  --out app-basic-universal-release-signed-v1.0.x.apk \
  app/build/outputs/apk/basic/release/app-basic-universal-release-unsigned.apk

# Get checksum
sha256sum app-basic-universal-release-signed-v1.0.x.apk

# Create release
gh release create v1.0.x \
  app-basic-universal-release-signed-v1.0.x.apk \
  --title "BrainOps Weather v1.0.x - Description" \
  --notes "Release notes here"
```

### Version Numbering:

**Format**: `MAJOR.MINOR.PATCH`

- **MAJOR**: Breaking changes, redesigns (v2.0.0)
- **MINOR**: New features, backwards compatible (v1.1.0)
- **PATCH**: Bug fixes, minor updates (v1.0.2)

**Examples:**
- v1.0.0 → v1.0.1: Rebranding (patch)
- v1.0.1 → v1.1.0: Add update checker (minor)
- v1.1.0 → v2.0.0: Complete rewrite (major)

---

## ❓ FAQ

**Q: Do I need to uninstall before updating?**
A: No! Just install the new APK over the existing one. Settings preserved.

**Q: Will I lose my locations/settings?**
A: Not if you install over existing (Option 1). Only uninstall loses data.

**Q: How often should I check for updates?**
A: Check monthly, or wait for in-app checker (coming soon).

**Q: Can I roll back to older version?**
A: Yes! Download old APK from releases page and install over current.

**Q: What if download fails?**
A: Try again, or download on computer and transfer via USB.

**Q: How do I know update is legit?**
A: Only download from your GitHub repo. Check SHA-256 checksum.

---

**TL;DR: Install new APK over existing app. No uninstall needed. Settings preserved. GitHub `:latest` always has newest version. In-app update checker coming soon!**

---

*Last Updated: 2025-11-23*
*Current Version: v1.0.1*
*Next: v1.0.2 with in-app update checker*
