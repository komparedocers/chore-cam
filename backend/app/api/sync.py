from fastapi import APIRouter, Depends
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import List, Optional
from app.db.database import get_db
from app.models.models import User, Project
from datetime import datetime

router = APIRouter()

class UserData(BaseModel):
    userId: Optional[str] = None
    email: str
    username: str
    isPro: bool = False

class ProjectData(BaseModel):
    projectId: str
    userId: int
    title: str
    description: Optional[str] = None
    clipsMetaJson: Optional[str] = None
    chosenPresetId: Optional[int] = None
    musicId: Optional[int] = None
    status: str
    outputFilePath: Optional[str] = None
    duration: Optional[int] = None
    resolution: Optional[str] = None

class SyncRequest(BaseModel):
    user: Optional[UserData] = None
    projects: Optional[List[ProjectData]] = None
    lastSyncTimestamp: int

class SyncResponse(BaseModel):
    success: bool
    message: str
    usersSynced: int = 0
    projectsSynced: int = 0
    serverTimestamp: int

@router.post("", response_model=SyncResponse)
def sync_data(request: SyncRequest, db: Session = Depends(get_db)):
    users_synced = 0
    projects_synced = 0

    # Sync user
    if request.user:
        existing_user = db.query(User).filter(User.email == request.user.email).first()

        if not existing_user and request.user.userId:
            # Create new user from offline data
            new_user = User(
                user_id=request.user.userId,
                email=request.user.email,
                username=request.user.username,
                hashed_password="",  # Would be set during registration
                is_pro=request.user.isPro
            )
            db.add(new_user)
            db.commit()
            users_synced += 1

    # Sync projects
    if request.projects:
        for project_data in request.projects:
            existing_project = db.query(Project).filter(
                Project.project_id == project_data.projectId
            ).first()

            if not existing_project:
                new_project = Project(
                    project_id=project_data.projectId,
                    user_id=project_data.userId,
                    title=project_data.title,
                    description=project_data.description,
                    clips_meta_json=project_data.clipsMetaJson,
                    chosen_preset_id=project_data.chosenPresetId,
                    music_id=project_data.musicId,
                    status=project_data.status,
                    output_file_path=project_data.outputFilePath,
                    duration=project_data.duration,
                    resolution=project_data.resolution
                )
                db.add(new_project)
                projects_synced += 1

        db.commit()

    return SyncResponse(
        success=True,
        message="Sync completed successfully",
        usersSynced=users_synced,
        projectsSynced=projects_synced,
        serverTimestamp=int(datetime.utcnow().timestamp() * 1000)
    )
