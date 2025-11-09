# ChoreoCam Android App

Professional music-synced video editing for Android.

## Features

- Offline-first architecture with Room database
- Professional UI with Material Design
- AdMob integration (banner + interstitial)
- In-app purchases for Pro features
- Automatic sync when online
- Background rendering with WorkManager
- Error-free operation even when server is down

## Requirements

- Android 7.0 (API 24) or higher
- Android Studio Arctic Fox or newer
- JDK 11+

## Setup

1. **Open in Android Studio**
   ```bash
   cd android
   # Open this directory in Android Studio
   ```

2. **Configure App**

   Edit `app.config.json` in the project root:
   ```json
   {
     "ads": {
       "admob_app_id": "ca-app-pub-XXXXX~XXXXX",
       "banner_ad_unit_id": "ca-app-pub-XXXXX/XXXXX",
       "interstitial_ad_unit_id": "ca-app-pub-XXXXX/XXXXX"
     },
     "backend": {
       "base_url": "https://your-api-url.com"
     }
   }
   ```

3. **Sync Gradle**

   Android Studio will automatically download dependencies.

4. **Run**

   Select a device/emulator and click Run.

## Configuration File

The `app.config.json` file can be placed:
1. In project root (bundled with app)
2. In app's external storage (for runtime changes)

### Configuration Options

```json
{
  "app": {
    "name": "ChoreoCam",
    "version": "1.0.0"
  },
  "backend": {
    "base_url": "https://api.choreocam.com",
    "timeout_seconds": 30,
    "fallback_to_local": true
  },
  "ads": {
    "enabled": true,
    "banner_ad_unit_id": "...",
    "interstitial_ad_unit_id": "...",
    "interstitial_frequency_minutes": 5,
    "test_mode": true
  },
  "iap": {
    "enabled": true,
    "pro_monthly_sku": "choreocam_pro_monthly",
    "pro_yearly_sku": "choreocam_pro_yearly"
  },
  "sync": {
    "auto_sync_enabled": true,
    "sync_interval_minutes": 15
  }
}
```

## Architecture

### Database (Room)
- **Users**: Local user accounts with sync flag
- **Projects**: Video projects with metadata
- **Presets**: Cached editing templates
- **MusicTracks**: Cached music library

### API Client (Retrofit)
- Automatic token authentication
- Network error handling
- Offline fallback

### Background Tasks (WorkManager)
- Periodic sync (every 15 minutes)
- Render queue
- Failed request retry

### Ad Integration
- **Banner Ads**: Bottom of screens (non-intrusive)
- **Interstitial Ads**: Between major actions (max once per 5 min)
- **Smart Display**: Only shown to free users

### In-App Purchases
- Monthly subscription
- Yearly subscription (discounted)
- Purchase restoration
- Offline purchase caching

## Building

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
1. Configure signing in `app/build.gradle`
2. Build:
   ```bash
   ./gradlew assembleRelease
   ```
3. Output: `app/build/outputs/apk/release/app-release.apk`

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Offline Mode

The app is designed to work completely offline:

1. **User Creation**: Users created locally, synced when online
2. **Project Editing**: All editing happens locally
3. **Presets & Music**: Cached from server, available offline
4. **Sync Queue**: Actions queued and synced when connection restored

### Sync Logic
```java
// User creates account offline
User user = new User();
user.setNeedsSync(true);
database.userDao().insert(user);

// When online, SyncWorker automatically syncs
WorkManager schedules sync every 15 minutes
```

## Permissions

The app requests minimal permissions:
- `READ_MEDIA_VIDEO` - Access user videos (Android 13+)
- `READ_MEDIA_AUDIO` - Access user audio (Android 13+)
- `READ_EXTERNAL_STORAGE` - Legacy storage access
- `INTERNET` - API communication
- `ACCESS_NETWORK_STATE` - Check connectivity
- `FOREGROUND_SERVICE` - Background rendering

## ProGuard

ProGuard rules are configured in `proguard-rules.pro`:
- Keeps database models
- Preserves API models
- Maintains Retrofit/Gson classes

## Troubleshooting

### Gradle Sync Issues
```bash
./gradlew clean
./gradlew build --refresh-dependencies
```

### Database Issues
Clear app data or uninstall/reinstall during development.

### Ad Test Mode
Set `test_mode: true` in config to see test ads.

## Key Files

- `ChoreoCamApplication.java` - App initialization
- `MainActivity.java` - Main screen
- `ProjectEditorActivity.java` - Video editing
- `ProUpgradeActivity.java` - In-app purchases
- `AdManager.java` - Ad management
- `SyncWorker.java` - Background sync
- `AppDatabase.java` - Room database

## Package Structure

```
com.choreocam.app/
├── activities/      # UI screens
├── adapters/        # RecyclerView adapters
├── api/             # Network layer
│   └── models/      # API request/response
├── database/        # Room database
│   └── dao/         # Data access objects
├── models/          # Data models
├── services/        # Background services
├── utils/           # Utility classes
└── workers/         # WorkManager workers
```
