# ChoreoCam

One-tap, music-synced reels from your daily clips. Auto-detects beats, picks highlights, applies transitions, and exports a share-ready video. Learns your “signature cut style.”

## Features
- Beat/onset detection; smart cut selection
- Highlight scoring (faces, sharpness, motion)
- Templates & transitions; caption styles
- HD/4K export; background render queue
- Optional scheduled posting helpers
- Royalty-safe music packs

## Stack
- **Android:** Kotlin, CameraX (optional), MediaCodec/MediaExtractor, OpenGL/Vulkan effects, WorkManager, Room, Retrofit
- **Backend:** FastAPI, Postgres, MinIO/S3, Redis, Nginx
- **AI:** On-device beat detection + lightweight vision scoring; server presets & personal style vector

## Get Started

### Android
- Import project, grant storage/media permissions.
- Add sample clips and choose a music pack in-app.
- Tap **Auto Edit** → preview → export.

### Backend
```bash
cd server
cp ../docker/.env.example .env
docker compose -f ../docker/docker-compose.yml up -d --build
```

## API (excerpt)
```
GET  /v1/presets
GET  /v1/music
POST /v1/style/learn           # upload minimal edit metadata only
```

## Monetization
- Free: SD exports, basic presets/music
- Pro: 1080p/4K, premium transitions, commercial-safe tracks, brand kits

## Privacy
- Raw media stays on device; only opt-in metadata for style learning (no frames/audio).

## Troubleshooting
- Export slow: enable **Background Rendering** and keep app foreground locked.
- Audio mismatch: re-run beat detection (Settings → Re-analyze).
