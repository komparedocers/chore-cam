# ChoreoCam Architecture

## Brief
Automatic beat-synced video editing on device with server-assisted templates, music licensing, and personal style learning.

## Client
- Pipelines:
  - Media ingest (URIs → decoded streams)
  - Audio analysis: onset/tempo/beat grid
  - Vision scoring: per-frame sharpness, face presence, motion magnitude
  - Cut list generation: align highlights to beat grid with min/avg clip length constraints
  - Effects: transitions, overlays, captions
  - Render: MediaCodec pipeline with GPU effects (OpenGL/Vulkan)
- Modules: `media`, `audio`, `vision`, `templates`, `render`, `export`
- Storage: Room (projects, presets cache, exports)
- Background: WorkManager render queue; progress notifications
- Permissions: READ_MEDIA_VIDEO/AUDIO (scoped), FOREGROUND_SERVICE

## Server
- Services:
  - `styles`: learn/update user style vector
  - `presets`: templates, transitions, captions
  - `music`: license-safe packs, cue points, BPM metadata
  - `schedule`: reminders/FCM for posting
- Storage: Postgres (Users, StyleVectors, Presets, MusicTracks, Exports)
- Files: MinIO/S3 for music/template assets
- Auth: OAuth2/JWT

## Data Model (simplified)
- **Project**(id, user_id, clips_meta_json, chosen_preset_id, music_id, created_at)
- **StyleVector**(user_id, vector, updated_at)
- **Export**(id, project_id, duration, resolution, file_url)

## Flows
1. Ingest media → analyze audio/vision → generate cut list → preview
2. Apply preset/transitions → render → export
3. (Opt-in) Upload minimal metadata for style learning → receive preset suggestions

## NFRs
- Export 15–30s clip in under device-appropriate time (GPU accel)
- No raw media leaves device by default
- Deterministic render pipeline; reproducible presets
