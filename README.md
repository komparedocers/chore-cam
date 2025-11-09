# ChoreoCam

**Automatic beat-synced video editing on Android with server-assisted templates, music licensing, and personal style learning.**

One-tap, music-synced reels from your daily clips. Auto-detects beats, picks highlights, applies transitions, and exports a share-ready video.

## Features

- **Beat-Synced Editing**: Automatic beat/onset detection and smart cut selection
- **Highlight Scoring**: AI-powered analysis for faces, sharpness, and motion
- **Templates & Transitions**: Professional presets and caption styles
- **HD/4K Export**: Background render queue with GPU acceleration
- **Offline Mode**: Full functionality without internet connection
- **Auto Sync**: Seamless data synchronization when online
- **Monetization**: AdMob integration and in-app purchases
- **Royalty-Safe Music**: Built-in music library with licensing

## Architecture

### Android App (Java)
- **Modules**: media, audio, vision, templates, render, export
- **Storage**: Room database for offline-first architecture
- **Background Processing**: WorkManager for rendering
- **Permissions**: Scoped storage (READ_MEDIA_VIDEO/AUDIO)
- **Revenue**: AdMob (banner + interstitial) + In-App Purchases

### Backend Server (Python/FastAPI)
- **Services**: Presets, Music, Style Learning, Sync
- **Database**: PostgreSQL with SQLAlchemy ORM
- **Storage**: MinIO/S3 for media assets
- **Authentication**: OAuth2/JWT
- **Caching**: Redis

## Quick Start

### Android App

#### Prerequisites
- Android Studio Arctic Fox or newer
- JDK 11 or higher
- Android SDK API 24+ (Android 7.0)

#### Setup
1. Open the `android` folder in Android Studio
2. Sync Gradle dependencies
3. Configure ad IDs in `app.config.json`:
   ```json
   {
     "ads": {
       "admob_app_id": "your-app-id",
       "banner_ad_unit_id": "your-banner-id",
       "interstitial_ad_unit_id": "your-interstitial-id"
     }
   }
   ```
4. Update backend URL in `app.config.json`:
   ```json
   {
     "backend": {
       "base_url": "http://your-backend-url"
     }
   }
   ```
5. Run on device or emulator

#### Key Configuration (app.config.json)
Located at the root of the project. This file controls:
- **AdMob IDs**: Configure your ad unit IDs
- **Backend URL**: API endpoint configuration
- **Feature Flags**: Enable/disable ads, IAP, sync
- **App Behavior**: Ad frequency, sync intervals, etc.

### Backend Server

#### Prerequisites
- Docker & Docker Compose
- Python 3.11+ (for local development)

#### Quick Start with Docker
```bash
cd docker
docker-compose up -d --build
```

This will start:
- PostgreSQL (port 5432)
- Redis (port 6379)
- MinIO (ports 9000, 9001)
- FastAPI Backend (port 8000)
- Nginx (port 80)

#### Initialize Database
```bash
docker exec -it choreocam-backend python init_db.py
```

#### Access Services
- API: http://localhost:8000
- API Docs: http://localhost:8000/docs
- MinIO Console: http://localhost:9001

#### Local Development
```bash
cd backend
pip install -r requirements.txt
cp .env.example .env
# Edit .env with your configuration
uvicorn app.main:app --reload
```

## API Endpoints

### Authentication
- `POST /v1/auth/register` - Register new user
- `POST /v1/auth/login` - User login

### Presets
- `GET /v1/presets` - Get all presets
- `GET /v1/presets/{id}` - Get preset by ID

### Music
- `GET /v1/music` - Get all music tracks
- `GET /v1/music/{id}` - Get track by ID

### Style Learning
- `POST /v1/style/learn` - Submit editing metadata
- `GET /v1/style/suggestions` - Get personalized suggestions

### Sync
- `POST /v1/sync` - Sync offline data

## Monetization Strategy

### Free Tier
- SD (720p) exports
- Basic presets and transitions
- Free music library
- Watermark on exports
- Banner and interstitial ads

### Pro Tier (In-App Purchase)
- 1080p/4K exports
- Premium presets and transitions
- Commercial-safe music tracks
- No watermark
- Ad-free experience
- Brand kits
- Priority rendering

### Ad Configuration
Ads are configured in `app.config.json`:
- **Banner Ads**: Bottom of each screen (non-intrusive)
- **Interstitial Ads**: Between major actions (configurable frequency)
- **Test Mode**: Enable during development

## Offline Mode

The app works completely offline:
1. **Local Database**: All user data stored in Room
2. **Auto Sync**: Data syncs when connection is restored
3. **Queue System**: Actions queued and processed when online
4. **No Crashes**: App handles offline state gracefully

## Project Structure

```
chore-cam/
├── android/                    # Android app
│   ├── app/
│   │   ├── src/main/
│   │   │   ├── java/com/choreocam/app/
│   │   │   │   ├── activities/     # UI Activities
│   │   │   │   ├── adapters/       # RecyclerView adapters
│   │   │   │   ├── api/            # API client
│   │   │   │   ├── database/       # Room database
│   │   │   │   ├── models/         # Data models
│   │   │   │   ├── services/       # Background services
│   │   │   │   ├── utils/          # Utilities
│   │   │   │   └── workers/        # WorkManager workers
│   │   │   └── res/                # Resources
│   │   └── build.gradle
│   └── build.gradle
├── backend/                    # FastAPI backend
│   ├── app/
│   │   ├── api/                # API routes
│   │   ├── core/               # Core functionality
│   │   ├── db/                 # Database
│   │   ├── models/             # SQLAlchemy models
│   │   └── main.py             # FastAPI app
│   ├── requirements.txt
│   └── init_db.py
├── docker/                     # Docker configuration
│   ├── docker-compose.yml
│   ├── Dockerfile
│   └── nginx.conf
└── app.config.json            # App configuration
```

## Building for Production

### Android App
1. Update `app.config.json` with production values
2. Configure signing in Android Studio
3. Build release APK/AAB:
   ```bash
   cd android
   ./gradlew assembleRelease
   ```
4. APK location: `android/app/build/outputs/apk/release/`

### Backend Server
1. Update `.env` with production credentials
2. Set strong `SECRET_KEY`
3. Configure production database
4. Deploy with Docker:
   ```bash
   docker-compose -f docker-compose.yml up -d
   ```

## Security Considerations

- **API Keys**: Never commit real API keys to version control
- **JWT Secret**: Use strong, random secret keys in production
- **HTTPS**: Always use HTTPS in production
- **Database**: Secure PostgreSQL with strong passwords
- **Permissions**: Request only necessary Android permissions

## Privacy

- Raw media stays on device
- Only opt-in metadata sent for style learning (no frames/audio)
- User data encrypted at rest
- GDPR compliant

## Troubleshooting

### Android App
- **Export slow**: Enable Background Rendering in settings
- **Audio mismatch**: Re-run beat detection (Settings → Re-analyze)
- **Permissions**: Grant storage permissions when prompted
- **Ads not showing**: Check test mode in config

### Backend
- **Database connection**: Check PostgreSQL is running
- **Port conflicts**: Ensure ports 8000, 5432, 6379, 9000 are available
- **CORS errors**: Update `CORS_ORIGINS` in config

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

Proprietary - All rights reserved

## Support

For issues and questions:
- Check documentation
- Review existing issues
- Contact support

## Version

- **App Version**: 1.0.0
- **API Version**: v1
- **Build Date**: 2024

---

Built with ❤️ for creators everywhere
