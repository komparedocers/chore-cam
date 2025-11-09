from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import List
from app.db.database import get_db
from app.models.models import Preset

router = APIRouter()

class PresetData(BaseModel):
    id: int
    presetId: str
    name: str
    description: str
    thumbnailUrl: str = None
    category: str
    isPro: bool
    transitionsJson: str = None
    effectsJson: str = None
    captionStyleJson: str = None

    class Config:
        from_attributes = True

class PresetResponse(BaseModel):
    success: bool
    message: str = None
    presets: List[PresetData] = None
    preset: PresetData = None

@router.get("", response_model=PresetResponse)
def get_presets(db: Session = Depends(get_db)):
    presets = db.query(Preset).all()

    preset_list = [
        PresetData(
            id=p.id,
            presetId=p.preset_id,
            name=p.name,
            description=p.description,
            thumbnailUrl=p.thumbnail_url,
            category=p.category,
            isPro=p.is_pro,
            transitionsJson=str(p.transitions_json) if p.transitions_json else None,
            effectsJson=str(p.effects_json) if p.effects_json else None,
            captionStyleJson=str(p.caption_style_json) if p.caption_style_json else None
        )
        for p in presets
    ]

    return PresetResponse(
        success=True,
        message="Presets retrieved successfully",
        presets=preset_list
    )

@router.get("/{preset_id}", response_model=PresetResponse)
def get_preset(preset_id: str, db: Session = Depends(get_db)):
    preset = db.query(Preset).filter(Preset.preset_id == preset_id).first()

    if not preset:
        return PresetResponse(success=False, message="Preset not found")

    preset_data = PresetData(
        id=preset.id,
        presetId=preset.preset_id,
        name=preset.name,
        description=preset.description,
        thumbnailUrl=preset.thumbnail_url,
        category=preset.category,
        isPro=preset.is_pro,
        transitionsJson=str(preset.transitions_json) if preset.transitions_json else None,
        effectsJson=str(preset.effects_json) if preset.effects_json else None,
        captionStyleJson=str(preset.caption_style_json) if preset.caption_style_json else None
    )

    return PresetResponse(
        success=True,
        message="Preset retrieved successfully",
        preset=preset_data
    )
