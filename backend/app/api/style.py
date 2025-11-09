from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from pydantic import BaseModel
from app.db.database import get_db
from app.models.models import StyleVector, User

router = APIRouter()

class StyleLearnRequest(BaseModel):
    projectId: str
    cutStyle: str
    averageClipLength: int
    transitionPreferences: str
    musicGenre: str

@router.post("/learn")
def learn_style(request: StyleLearnRequest, db: Session = Depends(get_db)):
    # In a real implementation, this would use ML to learn user preferences
    # For now, just store the metadata

    return {
        "success": True,
        "message": "Style learning data received"
    }

@router.get("/suggestions")
def get_style_suggestions(db: Session = Depends(get_db)):
    # Would return personalized preset suggestions based on learned style

    return {
        "success": True,
        "message": "Style suggestions",
        "presets": []
    }
