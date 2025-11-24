# BrainOps Weather - S25 Ultra Installation Guide

## 🎯 Quick Install (Ready to Go!)

Your S25 Ultra is already configured (dev mode + unknown sources enabled). Here's the fastest path to installation:

---

## 📥 **METHOD 1: Direct Download from GitHub (RECOMMENDED)**

### Step 1: Download APK on Your Phone

1. **Open Chrome/Samsung Internet on your S25 Ultra**
2. **Navigate to**: https://github.com/mwwoodworth/BrainOps-Weather/releases/tag/v1.0.0
3. **Download** one of these APKs:

   **Option A: Release APK (Recommended for daily use)**
   - File: `app-basic-universal-release-signed.apk`
   - Size: 21 MB
   - Package: `org.breezyweather`
   - Best performance, production-ready

   **Option B: Debug APK (For testing alongside release)**
   - File: `app-basic-universal-debug.apk`
   - Size: 48 MB
   - Package: `org.breezyweather.debug`
   - Can install both versions simultaneously

### Step 2: Install APK

1. **Open Files app** or swipe down notification
2. **Tap the downloaded APK** (should be in Downloads folder)
3. **Tap "Install"**
4. **Wait 5-10 seconds** for installation
5. **Tap "Open"** to launch immediately

### Step 3: Grant Permissions

When BrainOps Weather launches, grant these permissions:

1. **Location** → Allow (for local weather)
   - Choose "While using the app" or "Always" for background updates
2. **Notifications** → Allow (for weather alerts)
3. **Background services** → Allow (for auto-refresh)

---

## 📥 **METHOD 2: Transfer via USB (Alternative)**

### If GitHub download doesn't work:

1. **On your computer**, download APK from:
   - https://github.com/mwwoodworth/BrainOps-Weather/releases/tag/v1.0.0

2. **Connect S25 Ultra to computer via USB**

3. **Copy APK to phone**:
   - Windows: Open phone in File Explorer → Internal Storage → Download
   - Mac: Use Android File Transfer
   - Linux: Use MTP mount

4. **On S25 Ultra**:
   - Open Files app
   - Navigate to Downloads
   - Tap APK file
   - Install

---

## 📥 **METHOD 3: QR Code (Fastest)**

If you have this doc on your computer:

1. **Generate QR code** for: https://github.com/mwwoodworth/BrainOps-Weather/releases/tag/v1.0.0
2. **Scan with S25 Ultra camera**
3. **Download and install** as in Method 1

---

## ✅ Post-Installation Setup

### First Launch Configuration

1. **Welcome Screen**
   - Tap "Get Started"

2. **Choose Weather Source**
   - **Recommended**: Open-Meteo (free, no API key needed)
   - Or select your preferred provider

3. **Add Location**
   - **Option A**: Tap "+" → Use Current Location
   - **Option B**: Tap "+" → Search by city name

4. **Customize (Optional)**
   - Settings → Appearance → Choose theme
   - Settings → Units → Select preferred units (°F, mph, etc.)
   - Settings → Widgets → Configure home screen widgets

### BrainOps Integration (Optional)

To enable full BrainOps AI features:

1. **Open Menu** (tap ☰ or swipe from left)
2. **Tap "BrainOps"**
3. **Tap "Login"**
4. **Enter BrainOps credentials**:
   - Backend URL: `https://brainops-ai-agents.onrender.com`
   - Tenant ID: [Your tenant ID]
   - Or use dev credentials (pre-configured)

5. **Features unlocked**:
   - AI agents integration
   - Ops impact analysis
   - Weather-aware job planning
   - Dynamic configuration

---

## 🔍 Verification

### Confirm Installation Success

1. **App appears in launcher**: Look for "Breezy Weather" icon
2. **Weather data loads**: Should show forecast within 5 seconds
3. **Permissions granted**: Check Settings → Apps → Breezy Weather → Permissions

### Test Core Features

- [ ] Weather forecast displays correctly
- [ ] Hourly forecast shows 24+ hours
- [ ] Daily forecast shows 7+ days
- [ ] Alerts section loads (if any active)
- [ ] Location search works
- [ ] Settings menu accessible
- [ ] Widgets can be added to home screen

### Test BrainOps Features (After Login)

- [ ] BrainOps dashboard accessible
- [ ] Auth state displayed
- [ ] Ops impact section shows data
- [ ] AI agents connect successfully

---

## 🎨 Customization Tips

### Widgets

1. **Long-press home screen** → Widgets
2. **Find "Breezy Weather"**
3. **Choose widget type**:
   - Daily forecast card
   - Hourly forecast
   - Current conditions
   - Text widget
4. **Drag to home screen**

### Live Wallpaper

1. **Settings → Wallpaper**
2. **Select "Breezy Weather Live Wallpaper"**
3. **Customize appearance**
4. **Apply**

### Material You Theming

The app automatically adapts to S25 Ultra's Material You colors based on your wallpaper!

---

## 🆚 Comparison: Debug vs Release APK

| Feature | Release APK | Debug APK |
|---------|-------------|-----------|
| **Size** | 21 MB ✅ | 48 MB |
| **Package** | org.breezyweather | org.breezyweather.debug |
| **Performance** | Optimized (R8) ✅ | Standard |
| **Debug Info** | None | Full symbols |
| **Installation** | Single install | Can run both |
| **Recommended** | Daily use ✅ | Testing only |

**For S25 Ultra**: Use Release APK unless you need debug logging.

---

## 🐛 Troubleshooting

### "App not installed" error

**Solution**: Uninstall any previous version first
```
Settings → Apps → Breezy Weather → Uninstall
Then reinstall
```

### "Unknown sources blocked"

**Solution**: Enable for Chrome/Files app
```
Settings → Apps → Chrome → Install unknown apps → Allow
```

### Weather data not loading

**Solution**: Check permissions
```
Settings → Apps → Breezy Weather → Permissions
- Location: Allow
- Internet: Allow (should be default)
```

### Widgets not updating

**Solution**: Disable battery optimization
```
Settings → Apps → Breezy Weather → Battery → Unrestricted
```

---

## 🔒 Security Verification

### Verify APK Integrity

If you want to verify the APK hasn't been tampered with:

**Release APK SHA-256**:
```
5f1082d4282671bedee694a5c32189255033879934d4107d7ac81690348d0eb0
```

**Debug APK SHA-256**:
```
cc6524e96b1b96f80991a2aeb336dc87da97b958317133fde3daf58b0aec03e2
```

**Verify on phone**:
1. Install "Hash Droid" from Play Store
2. Open Hash Droid
3. Select downloaded APK
4. Compare SHA-256 hash with above

---

## 📊 What You're Installing

### Base Features (Breezy Weather 6.0.13)
- ✅ 50+ weather sources
- ✅ Material 3 Expressive design
- ✅ Widgets and live wallpaper
- ✅ Multi-language support (100+ languages)
- ✅ Privacy-focused (no trackers)
- ✅ Offline capable

### BrainOps AI Features (v1.0.0)
- 🧠 67 AI integration points
- 🔐 Supabase authentication
- ⚙️ Dynamic configuration
- 🌧️ Ops impact analysis
- 📊 Dashboard integration
- 🗺️ Radar embed

### Storage Used
- Release APK: 21 MB download → ~35 MB installed
- Debug APK: 48 MB download → ~80 MB installed
- Weather cache: ~5-10 MB (grows over time)
- **Total**: ~40-50 MB (release) or ~85-90 MB (debug)

---

## 🚀 Next Steps After Installation

### Immediate
1. ✅ Add your locations
2. ✅ Configure preferred units
3. ✅ Add widget to home screen
4. ✅ Enable weather alerts

### Optional
1. ⚙️ Explore weather sources (Settings → Weather Sources)
2. 🎨 Customize appearance (Settings → Appearance)
3. 🔔 Configure alert preferences (Settings → Alerts)
4. 🧠 Login to BrainOps for AI features
5. 🗺️ Enable weather radar (Settings → Radar)

### Advanced
1. 📱 Set up multiple widgets for different locations
2. 🌈 Enable live wallpaper
3. 🔄 Configure auto-refresh intervals
4. 🧪 Test BrainOps AI agent integration
5. 📊 Explore ops impact dashboard

---

## 📱 S25 Ultra Specific Tips

### Optimize for S25 Ultra Display
- **Settings → Appearance → Text size**: Adjust for 6.8" screen
- **Dark Mode**: Looks amazing on AMOLED display
- **Refresh Rate**: App supports 120Hz smoothly

### One UI 7 Integration
- **Good Lock compatibility**: Works with all One UI mods
- **Edge Panel**: Add Breezy Weather shortcut
- **Bixby Routines**: Auto-open weather when unlocking in morning

### Battery Optimization
- **Settings → Battery → Background usage limits**: Set to "Unrestricted"
- This ensures weather updates even when screen off

### S Pen Integration
- Hover over widgets to see detailed info (Air View)
- Use S Pen to quickly navigate forecast

---

## 🎉 You're Ready!

**Download Link**: https://github.com/mwwoodworth/BrainOps-Weather/releases/tag/v1.0.0

Choose either:
- 📦 `app-basic-universal-release-signed.apk` (21 MB, recommended)
- 🔧 `app-basic-universal-debug.apk` (48 MB, debug version)

**Installation**: 30 seconds
**Setup**: 2 minutes
**Full features**: Immediately available

---

**Questions?** Open an issue on GitHub: https://github.com/mwwoodworth/BrainOps-Weather/issues

**Enjoy your AI-powered weather experience! ⛅🧠**
