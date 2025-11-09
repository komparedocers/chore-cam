from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import List
from app.db.database import get_db
from app.models.models import MusicTrack

router = APIRouter()

class MusicTrackData(BaseModel):
    id: int
    trackId: str
    title: str
    artist: str
    genre: str = None
    bpm: int
    durationMs: int
    fileUrl: str
    isPro: bool
    cuePointsJson: str = None

    class Config:
        from_attributes = True

class MusicResponse(BaseModel):
    success: bool
    message: str = None
    tracks: List[MusicTrackData] = None
    track: MusicTrackData = None

@router.get("", response_model=MusicResponse)
def get_music_tracks(db: Session = Depends(get_db)):
    tracks = db.query(MusicTrack).all()

    track_list = [
        MusicTrackData(
            id=t.id,
            trackId=t.track_id,
            title=t.title,
            artist=t.artist,
            genre=t.genre,
            bpm=t.bpm,
            durationMs=t.duration_ms,
            fileUrl=t.file_url,
            isPro=t.is_pro,
            cuePointsJson=str(t.cue_points_json) if t.cue_points_json else None
        )
        for t in tracks
    ]

    return MusicResponse(
        success=True,
        message="Music tracks retrieved successfully",
        tracks=track_list
    )

@router.get("/{track_id}", response_model=MusicResponse)
def get_music_track(track_id: str, db: Session = Depends(get_db)):
    track = db.query(MusicTrack).filter(MusicTrack.track_id == track_id).first()

    if not track:
        return MusicResponse(success=False, message="Track not found")

    track_data = MusicTrackData(
        id=track.id,
        trackId=track.track_id,
        title=track.title,
        artist=track.artist,
        genre=track.genre,
        bpm=track.bpm,
        durationMs=track.duration_ms,
        fileUrl=track.file_url,
        isPro=track.is_pro,
        cuePointsJson=str(track.cue_points_json) if track.cue_points_json else None
    )

    return MusicResponse(
        success=True,
        message="Track retrieved successfully",
        track=track_data
    )
