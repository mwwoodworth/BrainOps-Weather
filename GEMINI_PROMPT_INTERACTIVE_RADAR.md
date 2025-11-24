# 🎯 COMPREHENSIVE PROMPT FOR GEMINI 3.0 PRO
## Interactive Radar & Custom Icon System for BrainOps Weather

---

# PROJECT CONTEXT

You are enhancing **BrainOps Weather**, a forked Android weather app based on Breezy Weather, with cutting-edge interactive radar capabilities and a custom icon system.

## CURRENT TECH STACK

**Platform:** Android (Kotlin)
**UI Framework:** Jetpack Compose with Material 3 Expressive Design
**Minimum SDK:** Android 8.0 (API 26)
**Target SDK:** Android 14+ (API 34+)
**Architecture:** MVVM with Repository pattern
**Dependency Injection:** Hilt/Dagger
**Networking:** Retrofit2 + OkHttp3
**Async:** Kotlin Coroutines + RxJava3
**Maps:** (TO BE IMPLEMENTED - your choice of best solution)

## EXISTING CODEBASE STRUCTURE

```
app/src/main/
├── java/org/breezyweather/
│   ├── ui/
│   │   ├── main/
│   │   │   ├── adapters/main/holder/
│   │   │   │   ├── AbstractMainCardViewHolder.kt
│   │   │   │   ├── PrecipitationNowcastViewHolder.kt
│   │   │   │   └── [other card holders]
│   │   │   └── MainActivity.kt
│   │   ├── common/
│   │   │   ├── composables/
│   │   │   └── widgets/
│   │   │       ├── Material3Scaffold.kt
│   │   │       └── Material3ExpressiveCardListItem.kt
│   │   └── settings/
│   ├── sources/
│   │   ├── openweather/
│   │   │   ├── OpenWeatherService.kt
│   │   │   ├── OpenWeatherApi.kt
│   │   │   └── json/
│   │   └── [50+ other weather sources]
│   ├── domain/
│   │   ├── location/model/Location.kt
│   │   ├── weather/
│   │   │   ├── model/Weather.kt
│   │   │   └── wrappers/WeatherWrapper.kt
│   │   └── settings/SettingsManager.kt
│   └── common/
│       └── options/appearance/CardDisplay.kt
└── res/
    ├── drawable/ (Vector XML icons)
    ├── layout/
    └── values/
        ├── strings.xml
        ├── colors.xml
        └── themes.xml
```

## AVAILABLE API & CREDENTIALS

**OpenWeatherMap API Key (PRE-CONFIGURED):**
```
63adafcfb0c3318dac0e2f16031603ab
```

**API Capabilities:**
- Current weather
- 5-day/3-hour forecast
- Air quality
- **Weather map tiles** (various layers):
  - Precipitation layer: `https://tile.openweathermap.org/map/precipitation_new/{z}/{x}/{y}.png?appid={API key}`
  - Clouds layer: `https://tile.openweathermap.org/map/clouds_new/{z}/{x}/{y}.png?appid={API key}`
  - Temperature layer: `https://tile.openweathermap.org/map/temp_new/{z}/{x}/{y}.png?appid={API key}`
  - Pressure layer: `https://tile.openweathermap.org/map/pressure_new/{z}/{x}/{y}.png?appid={API key}`
  - Wind layer: `https://tile.openweathermap.org/map/wind_new/{z}/{x}/{y}.png?appid={API key}`

**Free Tier Limits:**
- 1,000 API calls/day
- 60 calls/minute
- All map layers included

## EXISTING CARD SYSTEM

**Card Registration:** `CardDisplay.kt`
```kotlin
enum class CardDisplay(
    val id: String,
    @StringRes val nameId: Int,
) {
    CARD_NOWCAST("nowcast", R.string.precipitation_nowcasting),
    CARD_DAILY_FORECAST("daily_forecast", R.string.daily_forecast),
    CARD_HOURLY_FORECAST("hourly_forecast", R.string.hourly_forecast),
    // ... more cards
}
```

**Card Implementation Pattern:**
```kotlin
class ExampleCardViewHolder(
    parent: ViewGroup
) : AbstractMainCardViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.container_main_example_card, parent, false)
) {
    override fun onBindView(
        activity: MainActivity,
        location: Location,
        provider: ResourceProvider,
        listAnimationEnabled: Boolean,
        itemAnimationEnabled: Boolean,
        firstCard: Boolean
    ) {
        // Bind data to card
    }
}
```

---

# YOUR MISSION: BUILD 3 COMPONENTS

## COMPONENT 1: INTERACTIVE RADAR CARD 🗺️

### REQUIREMENTS

**1. Base Map Integration**

Choose and implement ONE of:
- **Option A:** Google Maps SDK for Android (preferred for polish)
- **Option B:** Mapbox SDK (preferred for customization)
- **Option C:** OSMDroid (preferred for open-source)

**Recommendation:** Mapbox or Google Maps for best UX

**2. Weather Layer Overlay System**

Implement multi-layer radar system with:

**Available Layers:**
- ✅ Precipitation (rain/snow intensity)
- ✅ Clouds (cloud cover)
- ✅ Temperature (heat map)
- ✅ Wind (direction & speed)
- ✅ Pressure (isobars)

**Layer Requirements:**
- Opacity control (0-100% for each layer)
- Multiple layers simultaneously (compositing)
- Smooth transitions when toggling
- Efficient tile loading/caching

**3. Interactive Controls**

**Gestures:**
- Pinch to zoom (levels 1-18)
- Drag to pan
- Two-finger rotation (optional)
- Double-tap to zoom in
- Two-finger tap to zoom out

**Controls UI:**
- Layer selector (bottom sheet or FAB menu)
- Opacity sliders for active layers
- Play/pause animation button
- Timeline scrubber
- Current location button
- Zoom in/out buttons
- Reset view button

**4. Animation System**

**Time-based Animation:**
- Show last 3 hours of radar data
- Show next 2 hours of forecast
- Smooth frame transitions (60 FPS target)
- Playback speed control (0.5x, 1x, 2x)
- Loop control

**Implementation:**
- Fetch historical tiles (if available)
- Interpolate between frames
- Efficient frame buffering
- Progress indicator

**5. Material 3 Expressive Design**

**Card Layout:**
```
┌────────────────────────────────────┐
│  🗺️ INTERACTIVE RADAR              │  ← Header with icon
├────────────────────────────────────┤
│                                    │
│         MAP VIEW                   │  ← Full-size interactive map
│      (600dp height)                │
│                                    │
├────────────────────────────────────┤
│  ⏯️  ◀━━━●━━━━▶  2x  🎚️           │  ← Controls: Play, Timeline, Speed, Layers
├────────────────────────────────────┤
│  🌧️ Precipitation   🌡️ Temp       │  ← Quick layer toggles
│  ☁️ Clouds          💨 Wind        │
└────────────────────────────────────┘
```

**Design Specifications:**
- Material 3 color scheme (dynamic colors support)
- Expressive rounded corners (28dp radius)
- Elevated surface (dp6 elevation)
- Glass morphism effects on controls
- Smooth state animations
- Haptic feedback on interactions
- Accessibility: TalkBack support, content descriptions

**6. Technical Implementation**

**File Structure:**
```kotlin
// New files to create:
app/src/main/java/org/breezyweather/ui/main/adapters/main/holder/
└── InteractiveRadarCardViewHolder.kt

app/src/main/java/org/breezyweather/ui/radar/
├── RadarMapView.kt (Composable)
├── RadarViewModel.kt
├── RadarRepository.kt
├── RadarLayerManager.kt
├── RadarAnimationController.kt
└── composables/
    ├── RadarControls.kt
    ├── LayerSelector.kt
    ├── TimelineSlider.kt
    └── RadarLegend.kt

app/src/main/java/org/breezyweather/sources/radar/
├── RadarTileProvider.kt
└── RadarTileCache.kt

app/src/main/res/layout/
└── container_main_interactive_radar_card.xml
```

**Key Classes to Implement:**

```kotlin
// 1. Radar Card Holder
class InteractiveRadarCardViewHolder(parent: ViewGroup) :
    AbstractMainCardViewHolder(/* ... */) {

    override fun onBindView(/* ... */) {
        // Initialize radar map
        // Set up controls
        // Load weather layers
    }
}

// 2. Radar Composable
@Composable
fun RadarMapView(
    location: Location,
    layers: List<RadarLayer>,
    animationState: RadarAnimationState,
    onLayerToggle: (RadarLayer) -> Unit,
    onTimeChange: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Map implementation
    // Layer overlays
    // Controls
}

// 3. View Model
class RadarViewModel @Inject constructor(
    private val radarRepository: RadarRepository
) : ViewModel() {

    val currentLocation: StateFlow<Location>
    val activeLayers: StateFlow<List<RadarLayer>>
    val animationState: StateFlow<RadarAnimationState>

    fun toggleLayer(layer: RadarLayer)
    fun setLayerOpacity(layer: RadarLayer, opacity: Float)
    fun playAnimation()
    fun pauseAnimation()
    fun seekToTime(timestamp: Long)
}

// 4. Tile Provider
class RadarTileProvider(
    private val apiKey: String
) {
    suspend fun getTile(
        layer: RadarLayer,
        z: Int,
        x: Int,
        y: Int,
        timestamp: Long? = null
    ): Bitmap?

    fun getTileUrl(/* ... */): String
}
```

**7. Performance Optimizations**

- ✅ Tile caching (LRU cache, 50MB limit)
- ✅ Lazy loading (load visible tiles only)
- ✅ Prefetching (preload adjacent tiles)
- ✅ Bitmap pooling (recycle bitmaps)
- ✅ Debounced API calls
- ✅ Animation frame interpolation
- ✅ GPU acceleration for overlays

---

## COMPONENT 2: CUSTOM ICON SYSTEM 🎨

### DESIGN PHILOSOPHY

**Theme:** Futuristic Material Design 3 with AI/Tech Aesthetic

**Style Guidelines:**
- **Stroke Weight:** 2dp (slightly heavier than Material Icons)
- **Corner Radius:** 2dp (rounded but sharp)
- **Style:** Outlined with gradient accents
- **Color:** Support for dynamic theming
- **Size:** 24dp × 24dp base size
- **Scaling:** Vector (XML) for crisp rendering at all sizes

**Visual Identity:**
- Clean, minimal, futuristic
- AI/neural network motifs where appropriate
- Weather phenomena represented abstractly
- Consistent geometric language
- Subtle animation potential

### ICON SET TO DESIGN

**Weather Condition Icons (50+):**

**Clear/Sun:**
- `ic_weather_clear_day` - Stylized sun with neural network rays
- `ic_weather_clear_night` - Crescent moon with star field
- `ic_weather_clear_day_gradient` - Gradient sun (dawn/dusk)

**Clouds:**
- `ic_weather_cloudy` - Abstract cloud formation
- `ic_weather_partly_cloudy_day` - Sun behind cloud
- `ic_weather_partly_cloudy_night` - Moon behind cloud
- `ic_weather_mostly_cloudy` - Dense cloud layer
- `ic_weather_overcast` - Full cloud cover

**Precipitation:**
- `ic_weather_rain_light` - Light rain drops (geometric)
- `ic_weather_rain_moderate` - Medium rain
- `ic_weather_rain_heavy` - Heavy rain with wind lines
- `ic_weather_drizzle` - Misty rain particles
- `ic_weather_shower` - Rain shower with sun rays

**Snow:**
- `ic_weather_snow_light` - Delicate snowflakes
- `ic_weather_snow_moderate` - Snowfall
- `ic_weather_snow_heavy` - Blizzard effect
- `ic_weather_sleet` - Mixed precipitation
- `ic_weather_flurries` - Light snow

**Storms:**
- `ic_weather_thunderstorm` - Lightning bolt (futuristic)
- `ic_weather_thunder` - Thunder cloud with energy
- `ic_weather_lightning` - Stylized lightning
- `ic_weather_severe_thunderstorm` - Intense storm

**Special Conditions:**
- `ic_weather_fog` - Layered fog waves
- `ic_weather_mist` - Light mist particles
- `ic_weather_haze` - Hazy atmosphere
- `ic_weather_smoke` - Smoke particles
- `ic_weather_dust` - Dust storm
- `ic_weather_sand` - Sandstorm
- `ic_weather_tornado` - Tornado funnel (abstract)
- `ic_weather_hurricane` - Hurricane spiral
- `ic_weather_tropical_storm` - Tropical system

**Wind:**
- `ic_wind_calm` - Still air indicator
- `ic_wind_light` - Gentle breeze
- `ic_wind_moderate` - Wind flow lines
- `ic_wind_strong` - Strong wind
- `ic_wind_gale` - Gale force wind

**BrainOps Specific Icons:**

**AI/Intelligence:**
- `ic_brainops_ai` - Neural network brain
- `ic_brainops_insight` - Lightbulb with neural connections
- `ic_brainops_prediction` - Crystal ball with data
- `ic_brainops_analysis` - Graph with AI overlay
- `ic_brainops_learning` - Book with neural network

**Data/Analytics:**
- `ic_brainops_data_flow` - Data stream visualization
- `ic_brainops_metrics` - Dashboard gauges
- `ic_brainops_trend` - Trend line with AI pattern
- `ic_brainops_forecast` - Future prediction icon

**Weather Tech:**
- `ic_radar_active` - Active radar waves
- `ic_radar_scanning` - Radar scan animation
- `ic_satellite` - Weather satellite
- `ic_weather_station` - Station icon
- `ic_sensor` - Environmental sensor

**UI/Navigation:**
- `ic_layer_toggle` - Layer stack icon
- `ic_timeline` - Timeline scrubber
- `ic_playback` - Play/pause combined
- `ic_animation_speed` - Speed indicator
- `ic_opacity` - Transparency control
- `ic_fullscreen` - Expand/collapse
- `ic_location_center` - Center on location
- `ic_zoom_in` - Zoom in (futuristic)
- `ic_zoom_out` - Zoom out (futuristic)

**Measurement Icons:**
- `ic_temperature_gradient` - Temperature with gradient
- `ic_humidity_droplet` - Humidity indicator
- `ic_pressure_gauge` - Pressure measurement
- `ic_wind_direction` - Compass with wind
- `ic_uv_index` - UV rays
- `ic_visibility` - Eye with distance
- `ic_air_quality` - Lungs with quality indicator
- `ic_pollen` - Pollen particles

**Time/Astronomy:**
- `ic_sunrise_tech` - Futuristic sunrise
- `ic_sunset_tech` - Futuristic sunset
- `ic_moonrise` - Moonrise indicator
- `ic_moonset` - Moonset indicator
- `ic_moon_phase_new` - New moon (tech style)
- `ic_moon_phase_full` - Full moon (tech style)
- [Moon phases 0-8]

### IMPLEMENTATION REQUIREMENTS

**File Format:**
- Vector XML (Android Vector Drawable)
- Support theme attributes
- Dynamic color compatibility

**Example Icon Template:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">

    <!-- Main icon path -->
    <path
        android:pathData="M..."
        android:strokeColor="?attr/colorOnSurface"
        android:strokeWidth="2"
        android:strokeLineCap="round"
        android:strokeLineJoin="round"
        android:fillColor="@android:color/transparent"/>

    <!-- Accent/gradient overlay (optional) -->
    <path
        android:pathData="M..."
        android:fillColor="?attr/colorPrimary"
        android:fillAlpha="0.3"/>

    <!-- Add subtle glow or tech elements -->
    <path
        android:pathData="M..."
        android:strokeColor="?attr/colorSecondary"
        android:strokeWidth="0.5"
        android:fillAlpha="0.6"/>
</vector>
```

**Design Tool Output:**
- Provide SVG source files
- Provide Android Vector XML
- Provide preview PNG (for documentation)
- Organize in categories

---

## COMPONENT 3: ENHANCED UX FEATURES 🌟

### 1. Radar Insights Panel

**AI-Powered Insights:**
Display intelligent analysis below the radar map:

```
┌────────────────────────────────────┐
│  🧠 BrainOps Radar Insights        │
├────────────────────────────────────┤
│  ⚡ Heavy rain approaching in 23min│
│     Peak intensity: 4:15 PM        │
│                                    │
│  🌡️ Temperature dropping 8°F       │
│     Cold front at 5:30 PM          │
│                                    │
│  💨 Wind gusts up to 35 mph        │
│     Secure outdoor items           │
└────────────────────────────────────┘
```

**Implementation:**
```kotlin
@Composable
fun RadarInsightsPanel(
    radarData: RadarData,
    location: Location
) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Analyze precipitation patterns
        // Detect temperature changes
        // Identify wind conditions
        // Generate actionable insights
    }
}
```

### 2. Layer Presets

**Quick Access Presets:**
- "Rain Tracker" (precipitation + clouds)
- "Temperature Check" (temp + clouds)
- "Wind Conditions" (wind + pressure)
- "Full Weather" (all layers)
- "Minimal" (precipitation only)

### 3. Location Markers

**Enhanced Markers:**
- Current location (pulsing blue dot)
- Saved locations (custom pins)
- Weather alerts (warning markers)
- Points of interest (optional)

### 4. Weather Alerts Overlay

**Alert Integration:**
- Show active weather alerts on map
- Polygon overlays for alert areas
- Tap alert for details
- Visual severity indicators

### 5. Screenshot & Share

**Export Features:**
- Screenshot radar view
- Share to social media
- Save to gallery
- Include timestamp and location

### 6. Accessibility

**Full Accessibility:**
- TalkBack descriptions for all controls
- High contrast mode support
- Large touch targets (48dp minimum)
- Descriptive labels
- Keyboard navigation support

---

# DESIGN SPECIFICATIONS

## Material 3 Expressive Theme

**Color System:**
```kotlin
// Use dynamic colors from Material You
val colorScheme = MaterialTheme.colorScheme

// Radar-specific colors
val radarPrimaryColor = colorScheme.primary
val radarSurfaceColor = colorScheme.surface
val radarOnSurfaceColor = colorScheme.onSurface

// Glass morphism
val glassBackground = colorScheme.surface.copy(alpha = 0.7f)
val glassBlur = 20.dp
```

**Typography:**
```kotlin
// Card headers
val radarHeaderStyle = MaterialTheme.typography.titleLarge

// Control labels
val radarControlLabel = MaterialTheme.typography.labelMedium

// Insights text
val radarInsightText = MaterialTheme.typography.bodyMedium
```

**Elevation & Shadows:**
```kotlin
// Card elevation
val radarCardElevation = 6.dp

// Control elevation
val radarControlElevation = 2.dp

// Floating controls
val radarFloatingElevation = 8.dp
```

**Animations:**
```kotlin
// State transitions
val radarStateTransition = spring(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

// Layer fade in/out
val layerFadeAnimation = tween<Float>(
    durationMillis = 300,
    easing = FastOutSlowInEasing
)
```

## Interaction Design

**Touch Targets:**
- Minimum size: 48dp × 48dp
- Spacing: 8dp between controls
- Ripple effects on all interactive elements

**Gestures:**
- Smooth, responsive
- Visual feedback
- Haptic feedback on significant actions
- Predictable behavior

**Loading States:**
- Shimmer effect for loading tiles
- Progress indicator for animation buffering
- Skeleton screens for initial load
- Error states with retry

---

# DELIVERABLES

## Code Files

**1. Radar System (Kotlin + Compose):**
```
✅ InteractiveRadarCardViewHolder.kt
✅ RadarMapView.kt (Composable)
✅ RadarViewModel.kt
✅ RadarRepository.kt
✅ RadarLayerManager.kt
✅ RadarAnimationController.kt
✅ RadarTileProvider.kt
✅ RadarTileCache.kt
✅ All composable components (controls, selectors, etc.)
```

**2. Icon System (Vector XML):**
```
✅ 50+ weather condition icons
✅ 20+ BrainOps-specific icons
✅ 15+ UI/control icons
✅ 10+ measurement icons
✅ All organized in categorized folders
```

**3. Integration:**
```
✅ CardDisplay.kt update (add CARD_RADAR)
✅ build.gradle.kts update (add map SDK dependency)
✅ AndroidManifest.xml update (permissions, API keys)
✅ strings.xml update (all labels)
✅ colors.xml update (radar colors)
```

**4. Documentation:**
```
✅ RADAR_IMPLEMENTATION.md (technical docs)
✅ ICON_DESIGN_SYSTEM.md (icon usage guide)
✅ USER_GUIDE_RADAR.md (user-facing guide)
✅ API_USAGE.md (OpenWeatherMap tile API docs)
```

## Design Assets

**1. Icon Source Files:**
- SVG sources (editable in Figma/Illustrator)
- Preview sheet (all icons in grid)
- Usage examples

**2. UI Mockups:**
- Radar card in light mode
- Radar card in dark mode
- Layer selector UI
- Animation controls
- Insights panel

---

# TECHNICAL CONSTRAINTS

## Performance Targets

- **Initial Load:** < 2 seconds
- **Tile Load:** < 500ms per tile
- **Animation:** 60 FPS minimum
- **Memory:** < 100MB for cached tiles
- **Battery:** Minimal impact (efficient rendering)

## Compatibility

- **Min SDK:** 26 (Android 8.0)
- **Target SDK:** 34+ (Android 14+)
- **Screen Sizes:** Phone, tablet, foldable
- **Orientations:** Portrait, landscape
- **Dark Mode:** Full support
- **Dynamic Colors:** Material You support

## Dependencies (Suggested)

```gradle
// Map SDK (choose ONE)
implementation "com.google.android.gms:play-services-maps:18.2.0"
// OR
implementation "com.mapbox.maps:android:11.0.0"
// OR
implementation "org.osmdroid:osmdroid-android:6.1.17"

// Tile loading
implementation "com.squareup.retrofit2:retrofit:2.9.0"
implementation "com.squareup.okhttp3:okhttp:4.12.0"
implementation "io.coil-kt:coil-compose:2.5.0"

// Animation
implementation "androidx.compose.animation:animation:1.6.0"

// Caching
implementation "androidx.room:room-runtime:2.6.1"
```

---

# SUCCESS CRITERIA

## Functional Requirements ✅

- ✅ Radar card appears in main feed
- ✅ Loads OpenWeatherMap tiles successfully
- ✅ Multiple layers can be toggled on/off
- ✅ Opacity controls work smoothly
- ✅ Animation plays forward/backward
- ✅ All gestures (zoom, pan) work
- ✅ Insights panel shows relevant info
- ✅ Location tracking works
- ✅ No crashes or ANRs
- ✅ Works offline (shows cached tiles)

## Design Requirements ✅

- ✅ Follows Material 3 Expressive guidelines
- ✅ Futuristic aesthetic achieved
- ✅ Icons are gorgeous and consistent
- ✅ Colors adapt to system theme
- ✅ Smooth animations everywhere
- ✅ Glass morphism effects polished
- ✅ Typography is legible and beautiful
- ✅ Accessibility features complete

## User Experience ✅

- ✅ Intuitive controls
- ✅ Fast and responsive
- ✅ Minimal learning curve
- ✅ Delightful interactions
- ✅ Useful insights provided
- ✅ Reliable and stable
- ✅ Beautiful and impressive
- ✅ Users say "WOW!"

---

# CREATIVE FREEDOM & INNOVATION

## You Have Freedom To:

1. **Choose the best map SDK** (Google Maps, Mapbox, or OSM)
2. **Enhance the design** beyond specifications
3. **Add delightful micro-interactions**
4. **Implement smart defaults** for layer combinations
5. **Create unique icon interpretations**
6. **Add easter eggs** (subtle, tasteful)
7. **Optimize beyond requirements**
8. **Suggest improvements** to the prompt

## Innovation Challenges:

1. **Make it the BEST radar in any weather app**
2. **Design icons that are instantly recognizable**
3. **Create a futuristic UI that feels natural**
4. **Implement smooth animations that wow users**
5. **Build something you'd be proud to show**

---

# ADDITIONAL CONTEXT

## Existing Features to Leverage

**Already Available:**
- Location services
- Weather data fetching
- Dark mode support
- Material 3 theming
- Card system architecture
- Settings infrastructure

**Can Be Used:**
- Existing weather models
- Location repository
- Network clients
- Image loading (Coil)
- State management patterns

## BrainOps Brand Identity

**Philosophy:**
- AI-enhanced weather intelligence
- Proactive insights
- Beautiful, functional design
- User empowerment
- Cutting-edge technology

**Voice:**
- Intelligent but approachable
- Precise but not robotic
- Helpful but not intrusive
- Modern but timeless

---

# EXAMPLE CODE SNIPPETS

## Radar Composable Structure

```kotlin
@Composable
fun InteractiveRadarCard(
    location: Location,
    modifier: Modifier = Modifier,
    viewModel: RadarViewModel = hiltViewModel()
) {
    val layers by viewModel.activeLayers.collectAsState()
    val animationState by viewModel.animationState.collectAsState()
    val insights by viewModel.insights.collectAsState()

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(6.dp),
        shape = RoundedCornerShape(28.dp)
    ) {
        Column {
            // Header
            RadarCardHeader(
                title = stringResource(R.string.interactive_radar),
                icon = Icons.Custom.RadarActive
            )

            // Map View
            Box(modifier = Modifier.height(600.dp)) {
                RadarMapView(
                    location = location,
                    layers = layers,
                    animationState = animationState,
                    onLayerToggle = viewModel::toggleLayer
                )

                // Floating controls
                RadarControls(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    animationState = animationState,
                    onPlayPause = viewModel::toggleAnimation,
                    onTimeSeek = viewModel::seekToTime
                )
            }

            // Quick layer toggles
            LayerQuickToggles(
                layers = layers,
                onToggle = viewModel::toggleLayer
            )

            // AI Insights
            if (insights.isNotEmpty()) {
                RadarInsightsPanel(
                    insights = insights,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
```

## Tile Provider Example

```kotlin
class RadarTileProvider @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val cache: RadarTileCache
) {
    suspend fun getTile(
        layer: RadarLayer,
        z: Int,
        x: Int,
        y: Int,
        timestamp: Long? = null
    ): Bitmap? = withContext(Dispatchers.IO) {
        // Check cache first
        cache.get(layer, z, x, y, timestamp)?.let { return@withContext it }

        // Fetch from API
        val url = buildTileUrl(layer, z, x, y, timestamp)
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request).execute().use { response ->
            if (response.isSuccessful) {
                response.body?.bytes()?.let { bytes ->
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.also {
                        cache.put(layer, z, x, y, timestamp, it)
                    }
                }
            } else {
                null
            }
        }
    }

    private fun buildTileUrl(
        layer: RadarLayer,
        z: Int,
        x: Int,
        y: Int,
        timestamp: Long?
    ): String {
        val layerPath = when (layer) {
            RadarLayer.PRECIPITATION -> "precipitation_new"
            RadarLayer.CLOUDS -> "clouds_new"
            RadarLayer.TEMPERATURE -> "temp_new"
            RadarLayer.WIND -> "wind_new"
            RadarLayer.PRESSURE -> "pressure_new"
        }

        return "https://tile.openweathermap.org/map/$layerPath/$z/$x/$y.png" +
            "?appid=${BuildConfig.OPEN_WEATHER_KEY}" +
            if (timestamp != null) "&time=$timestamp" else ""
    }
}
```

---

# QUALITY CHECKLIST

Before submitting, ensure:

## Code Quality
- [ ] All code compiles without errors
- [ ] No compiler warnings
- [ ] Follows Kotlin coding conventions
- [ ] Proper null safety
- [ ] No hardcoded strings (use strings.xml)
- [ ] No memory leaks
- [ ] Proper error handling
- [ ] Commented where necessary

## Design Quality
- [ ] Icons are pixel-perfect
- [ ] Consistent stroke weights
- [ ] Proper theming attributes
- [ ] Works in light AND dark mode
- [ ] Smooth animations (no jank)
- [ ] Proper spacing and alignment
- [ ] Accessible color contrast
- [ ] Professional polish

## User Experience
- [ ] Intuitive first use
- [ ] Fast performance
- [ ] Reliable functionality
- [ ] Clear visual feedback
- [ ] Helpful error messages
- [ ] Offline capability
- [ ] Battery efficient
- [ ] Delightful interactions

---

# FINAL NOTES

**Priority:** Make this SPECTACULAR. This is the signature feature of BrainOps Weather.

**Timeline:** Take your time to get it right. Quality over speed.

**Questions:** If anything is unclear, ask for clarification.

**Vision:** Create the most beautiful, functional, and innovative weather radar experience on Android. Make it so good that users show it to their friends. Make it worthy of the BrainOps brand.

**Remember:** You're not just implementing features. You're crafting an experience. Every pixel, every animation, every interaction should feel intentional and delightful.

**Go build something fucking incredible!** 🚀🌟🗺️

---

*End of Prompt*
*Version: 1.0*
*Target: Gemini 3.0 Pro*
*Generated: 2025-11-23*
