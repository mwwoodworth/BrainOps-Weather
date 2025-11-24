# BrainOps Weather - Complete Usage Guide

## 🎉 Welcome to BrainOps Weather!

You've successfully installed BrainOps Weather on your S25 Ultra. This guide will show you how to use all features, including BrainOps AI integration.

---

## 📱 First Launch Setup

### 1. Choose Your Weather Source

On first launch, you'll be prompted to select a weather source:

**Recommended: Open-Meteo**
- ✅ Free, no API key needed
- ✅ Privacy-focused
- ✅ Global coverage
- ✅ Reliable forecasts

**Other Options:**
- AccuWeather (requires free API key)
- Met.no (Europe)
- SMHI (Sweden)
- DMI (Denmark)
- 50+ other sources

**How to choose:**
1. Tap "+" to add location
2. Select "Open-Meteo" from the list
3. Grant location permission when prompted

### 2. Add Your Locations

You can add multiple locations:

**Method 1: Current Location (GPS)**
1. Tap "+" button
2. Tap "Current Location"
3. Grant location permission
4. Weather loads automatically

**Method 2: Manual Search**
1. Tap "+" button
2. Tap search bar
3. Type city name (e.g., "Denver, CO")
4. Select from results

**Pro Tip**: Add home, work, and project sites for quick switching

---

## 🧠 BrainOps AI Integration

### What is BrainOps Integration?

BrainOps Weather connects to your BrainOps AI OS for:
- 🌧️ **Ops Impact Analysis** - Weather impact on jobs/crews
- 📊 **AI-Powered Insights** - Intelligent recommendations
- 🔄 **Dynamic Configuration** - Auto-updates from backend
- 📍 **Location Intelligence** - Weather-aware planning
- 🎯 **Agent Communication** - Real-time AI coordination

### How to Login to BrainOps

#### Step 1: Open BrainOps Menu
1. Tap **☰ menu** (top-left) or swipe from left edge
2. Look for **"BrainOps"** section
3. Tap **"Login"** or **"BrainOps Settings"**

#### Step 2: Configure Backend URL

**Production Backend:**
```
https://brainops-ai-agents.onrender.com
```

**Enter this in the "Backend URL" field**

#### Step 3: Enter Credentials

**For Development/Testing:**
- **API Key**: `brainops_dev_key_2025` (pre-configured in debug builds)
- **Tenant ID**: `51e728c5-94e8-4ae0-8a0a-6a08d1fb3457` (pre-configured in debug builds)

**For Production:**
- Ask your BrainOps admin for:
  - Your personal API key
  - Your tenant ID
  - Any custom backend URL

#### Step 4: Verify Connection

After login, you should see:
- ✅ **Green checkmark** or "Connected" status
- ✅ **BrainOps Dashboard** accessible from menu
- ✅ **Ops Impact** section showing data
- ✅ **AI Agent** status indicators

---

## 🎯 Core Weather Features

### Main Screen Overview

**Header Section:**
- Current temperature
- Weather condition
- "Feels like" temperature
- Location name

**Daily Cards:**
- 7-16 day forecast
- High/Low temperatures
- Precipitation chance
- Swipe left/right to see more days

**Hourly Timeline:**
- 24-48 hour forecast
- Scroll horizontally
- Temperature curve
- Precipitation bars

**Detail Blocks:**
- UV Index
- Humidity
- Wind speed & direction
- Pressure
- Visibility
- Sunrise/Sunset
- Moon phase
- Air quality (if available)
- Pollen (if available)

### Refresh Weather Data

**Manual Refresh:**
- Pull down on main screen
- Or tap refresh icon (↻)

**Auto Refresh:**
- Settings → Background → Auto-refresh interval
- Options: 30 min, 1 hour, 2 hours, 4 hours, 6 hours, Never
- **Recommendation**: 1-2 hours for balance

---

## 🔔 Alerts & Notifications

### Weather Alerts

BrainOps Weather shows real-time severe weather alerts:

**Alert Types:**
- ⛈️ Thunderstorms
- 🌪️ Tornado warnings
- ❄️ Winter weather
- 🌊 Flood warnings
- 🔥 Fire weather
- 🌡️ Extreme temperature

**How to View:**
1. Alerts badge appears on main screen
2. Tap alert to see full details
3. Swipe to dismiss or tap "Remind me later"

**Alert Settings:**
- Settings → Alerts → Configure which types to receive
- Enable push notifications for critical alerts

### BrainOps Ops Impact Alerts

When logged into BrainOps, you'll also see:
- 🏗️ Job weather impacts
- 👷 Crew safety alerts
- 📅 Schedule recommendations
- ⚠️ Material delivery warnings

---

## 🏠 Widgets (Home Screen)

### Add a Widget

1. **Long-press** empty space on home screen
2. Tap **"Widgets"**
3. Find **"BrainOps Weather"** (or "Breezy Weather" if name not updated yet)
4. **Drag** your preferred widget to home screen

### Widget Types Available

**1. Daily Forecast Card**
- Shows 5-7 day forecast
- Compact view
- Tap to open app

**2. Current Conditions**
- Large temperature
- Current weather icon
- Feels like, high/low
- Updates every 30-60 min

**3. Hourly Forecast**
- Next 12-24 hours
- Scroll horizontally
- Temperature + precipitation

**4. Text Widget**
- Minimal design
- Just temp + condition
- Perfect for minimalists

**5. Clock + Weather**
- Time + temperature
- Combines two widgets in one

### Widget Customization

**Tap widget to configure:**
- Background transparency
- Text color (light/dark)
- Data displayed
- Update frequency
- Tap action (open app, forecast, alerts)

---

## 🎨 Appearance & Themes

### Material You Integration

BrainOps Weather automatically adapts to your S25 Ultra's color scheme:
- Changes accent colors based on wallpaper
- Follows system dark/light mode
- Looks native to One UI 7

### Dark Mode

**Auto Dark Mode (Recommended):**
- Settings → Appearance → Theme
- Select "Follow System"
- Switches with phone's dark mode

**Manual:**
- Settings → Appearance → Theme
- Select "Light" or "Dark"

### Custom Themes

Settings → Appearance:
- **Icon Pack**: Choose weather icon style
  - Material 3
  - Minimal
  - Pixel
  - Custom (download from GitHub)
- **Background**: Gradient, solid, image
- **Card Style**: Rounded, square, minimal

---

## 📊 BrainOps Dashboard (After Login)

### Accessing the Dashboard

1. Tap **☰ menu**
2. Tap **"BrainOps Dashboard"**
3. View AI-powered insights

### Dashboard Sections

**1. Ops Impact Overview**
- Jobs affected by weather
- Crew safety status
- Schedule conflicts
- Risk ratings

**2. AI Agent Status**
- Connected agents (14+ active)
- Agent capabilities
- Last activity timestamps
- Communication status

**3. Weather Intelligence**
- Forecast accuracy
- Historical patterns
- Predictive insights
- Recommended actions

**4. System Health**
- Backend connection status
- Data sync status
- API call metrics
- Error logs (if any)

---

## ⚙️ Settings Deep Dive

### Location Settings

**Settings → Location:**
- **GPS Precision**: High (recommended for S25 Ultra)
- **Background Location**: Allow for auto-updates
- **Location Provider**:
  - GPS (most accurate)
  - IP-based (less accurate, more private)
  - Manual only

### Weather Sources

**Settings → Weather Sources:**
- **Primary Source**: Your main weather provider
- **Secondary Source**: Fallback if primary fails
- **Alert Source**: Where to get severe weather alerts
- **Nowcast Source**: Next-hour precipitation

**How to Switch:**
1. Settings → Weather Sources
2. Tap current source
3. Select new source from list
4. Grant API key if required (some sources)

### Units & Format

**Settings → Units:**
- **Temperature**: °F, °C, K
- **Precipitation**: in, mm, cm
- **Wind Speed**: mph, km/h, m/s, knots
- **Pressure**: inHg, hPa, mbar, mmHg, kPa
- **Distance**: mi, km
- **First Day of Week**: Sunday, Monday, Saturday

### Notifications

**Settings → Notifications:**
- **Forecast Notifications**: Daily summary at chosen time
- **Alert Notifications**: Severe weather (high priority)
- **Precipitation Notifications**: When rain/snow expected
- **Update Notifications**: New version available

**Quiet Hours:**
- Set times when notifications are silenced
- Except critical alerts (tornado, etc.)

---

## 🌧️ Advanced Features

### Precipitation Nowcast

See minute-by-minute precipitation for the next hour:
1. Main screen → Scroll to "Nowcast" section
2. Shows when rain/snow starts/stops
3. Intensity graph
4. Powered by radar data

### Radar View

**Access:**
1. Main screen → Tap "Radar" card
2. Or Settings → Features → Enable radar

**Controls:**
- Pinch to zoom
- Play button for animation
- Layer toggles (radar, clouds, temp)

**Note**: Radar requires data connection

### Air Quality Index (AQI)

If available from your weather source:
- Main screen → AQI card
- Shows pollutant levels
- Health recommendations
- Pollen count (seasonal)

### Historical Weather

**View past weather:**
1. Main screen → Tap date
2. Select past date
3. See what weather was like
4. Useful for job documentation

---

## 🔄 Auto-Updates from GitHub

### How Updates Work

BrainOps Weather can check for updates from your personal GitHub repo:

**Current Setup:**
- ✅ Repo: github.com/mwwoodworth/BrainOps-Weather
- ⏳ Auto-update: Will be configured once feature is enabled

### Manual Update Check

**Until auto-update is ready:**
1. Visit: https://github.com/mwwoodworth/BrainOps-Weather/releases
2. Check for new versions
3. Download latest APK
4. Install (will update over existing)

**Your data is preserved:**
- Locations saved
- Settings retained
- BrainOps login persists

### Update Notifications

When enabled, you'll see:
- 🔔 "New version available" notification
- Tap to download
- Installs automatically (with permission)

---

## 🐛 Troubleshooting

### Weather Not Loading

**Symptoms:** Blank screen, "No data" message

**Solutions:**
1. **Check internet**: WiFi or mobile data on
2. **Check permissions**: Settings → Apps → BrainOps Weather → Permissions → Location (Allow)
3. **Try different source**: Settings → Weather Sources → Switch provider
4. **Refresh manually**: Pull down on main screen
5. **Clear cache**: Settings → Apps → BrainOps Weather → Storage → Clear Cache

### BrainOps Login Failing

**Symptoms:** "Connection failed" or "Unauthorized"

**Solutions:**
1. **Check backend URL**: Must be `https://brainops-ai-agents.onrender.com`
2. **Verify API key**: Use `brainops_dev_key_2025` for testing
3. **Check internet**: Backend is online, ensure good connection
4. **Check backend status**: Visit https://brainops-ai-agents.onrender.com/health
5. **Try logging out and back in**

### Widgets Not Updating

**Symptoms:** Widget shows old data

**Solutions:**
1. **Check battery optimization**: Settings → Apps → BrainOps Weather → Battery → Unrestricted
2. **Check background data**: Settings → Apps → BrainOps Weather → Mobile data → Allow background
3. **Increase update frequency**: Settings → Background → Auto-refresh interval (lower value)
4. **Remove and re-add widget**

### Notifications Not Working

**Symptoms:** No alert notifications

**Solutions:**
1. **Check app notifications**: Settings → Apps → BrainOps Weather → Notifications → Allow
2. **Check alert types**: In-app Settings → Alerts → Enable types you want
3. **Check Do Not Disturb**: Make sure it's not blocking
4. **Check quiet hours**: In-app Settings → Notifications → Quiet Hours

### GPS Location Inaccurate

**Symptoms:** Wrong location detected

**Solutions:**
1. **Improve GPS accuracy**: Settings → Location → Google Location Accuracy → On
2. **Go outside**: Better GPS signal
3. **Use manual location**: Add location by name instead of GPS
4. **Check location permission**: Must be "Allow all the time" for background

---

## 💡 Pro Tips for S25 Ultra

### 1. Use Edge Panel

Add BrainOps Weather to edge panel for quick access:
- Settings → Display → Edge panels
- Add "BrainOps Weather" shortcut

### 2. One-Handed Mode

Enable easy reach on 6.8" screen:
- Swipe down from center of screen
- Pulls UI down for thumb access

### 3. S Pen Features

- **Hover over widgets**: See detailed info without opening app
- **Screen write**: Capture forecast for notes
- **Smart select**: Extract weather info to share

### 4. Bixby Routines

Automate based on weather:
- IF heavy rain THEN turn on "Driving Mode"
- IF cold weather THEN reminder to wear jacket
- IF UV high THEN apply sunscreen reminder

### 5. Always-On Display

Show current temp on AOD:
- Settings → Lock screen → Always On Display
- Add BrainOps Weather widget

### 6. DeX Mode

When connected to monitor:
- BrainOps Weather shows in desktop layout
- Multi-column forecast view
- Perfect for project planning

---

## 📞 Getting Help

### In-App Help

- Menu → Help
- Menu → About → Version info
- Menu → Feedback

### BrainOps Support

- GitHub Issues: https://github.com/mwwoodworth/BrainOps-Weather/issues
- Check documentation in repo

### Weather Source Issues

If specific source not working:
- Try alternative source first
- Check source's website for status
- Some require API key (free signup)

---

## 🎯 Next Steps

Now that you're set up:

1. ✅ **Add your locations** (home, work, project sites)
2. ✅ **Configure widgets** on home screen
3. ✅ **Login to BrainOps** for AI features
4. ✅ **Enable alerts** for severe weather
5. ✅ **Customize appearance** to your liking
6. ✅ **Set auto-refresh** interval (1-2 hours recommended)

### Once BrainOps Integration is Live:

You'll be able to:
- 📊 See weather impact on specific jobs
- 👷 Get crew safety recommendations
- 📅 Receive schedule optimization suggestions
- 🎯 View AI-powered weather insights
- 🔄 Auto-sync with WeatherCraft ERP

---

## 🌟 What Makes BrainOps Weather Special

Unlike standard weather apps, BrainOps Weather:

1. **Understands Your Business**
   - Not just "it's raining"
   - "This rain affects 3 active jobs"

2. **Powered by AI Agents**
   - 14+ active AI systems analyzing weather
   - Predictive insights, not just forecasts

3. **Integrated with Your ERP**
   - Connects to WeatherCraft, MyRoofGenius
   - Real-time job/crew awareness

4. **Privacy-Focused**
   - No tracking, no ads
   - Your data stays yours

5. **Fully Customizable**
   - Open source (your personal fork)
   - Add features as needed
   - Connect to your own backends

---

**Enjoy your AI-powered weather experience! ⛅🧠**

*Last Updated: 2025-11-23*
*Version: 1.0.0*
*App: BrainOps Weather (formerly Breezy Weather)*
