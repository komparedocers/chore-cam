"""Initialize database with sample data"""
from app.db.database import SessionLocal, engine
from app.models.models import Base, Preset, MusicTrack
import uuid

# Create tables
Base.metadata.create_all(bind=engine)

db = SessionLocal()

# Sample Presets
presets = [
    Preset(
        preset_id=str(uuid.uuid4()),
        name="Quick Cuts",
        description="Fast-paced transitions synced to the beat",
        category="basic",
        is_pro=False,
        transitions_json={"type": "cut", "duration": 0},
        effects_json={"filters": []},
        caption_style_json={"font": "Arial", "size": 24}
    ),
    Preset(
        preset_id=str(uuid.uuid4()),
        name="Smooth Flow",
        description="Elegant cross-dissolves and smooth transitions",
        category="basic",
        is_pro=False,
        transitions_json={"type": "dissolve", "duration": 500},
        effects_json={"filters": ["blur"]},
        caption_style_json={"font": "Helvetica", "size": 20}
    ),
    Preset(
        preset_id=str(uuid.uuid4()),
        name="Cinematic",
        description="Professional-grade effects and color grading",
        category="premium",
        is_pro=True,
        transitions_json={"type": "wipe", "duration": 700},
        effects_json={"filters": ["cinematic_lut", "vignette"]},
        caption_style_json={"font": "Futura", "size": 28}
    )
]

# Sample Music Tracks
tracks = [
    MusicTrack(
        track_id=str(uuid.uuid4()),
        title="Upbeat Summer",
        artist="ChoreoCam Studios",
        genre="Pop",
        bpm=128,
        duration_ms=180000,
        file_url="https://example.com/music/upbeat-summer.mp3",
        is_pro=False,
        cue_points_json=[0, 1000, 2000, 3000]
    ),
    MusicTrack(
        track_id=str(uuid.uuid4()),
        title="Chill Vibes",
        artist="ChoreoCam Studios",
        genre="Electronic",
        bpm=90,
        duration_ms=200000,
        file_url="https://example.com/music/chill-vibes.mp3",
        is_pro=False,
        cue_points_json=[0, 1500, 3000, 4500]
    ),
    MusicTrack(
        track_id=str(uuid.uuid4()),
        title="Epic Orchestral",
        artist="ChoreoCam Premium",
        genre="Orchestral",
        bpm=140,
        duration_ms=240000,
        file_url="https://example.com/music/epic-orchestral.mp3",
        is_pro=True,
        cue_points_json=[0, 800, 1600, 2400]
    )
]

# Add to database
for preset in presets:
    db.add(preset)

for track in tracks:
    db.add(track)

db.commit()
db.close()

print("Database initialized with sample data!")
