from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import List, Optional
from app.db.database import get_db
from app.models.models import User, Project
from app.core.logging_config import get_logger
from datetime import datetime
import traceback

router = APIRouter()
logger = get_logger("sync")

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
    logger.info(f"Sync request received. Last sync: {request.lastSyncTimestamp}")

    users_synced = 0
    projects_synced = 0

    try:
        # Sync user
        if request.user:
            logger.debug(f"Syncing user data for: {request.user.email}")
            existing_user = db.query(User).filter(User.email == request.user.email).first()

            if not existing_user and request.user.userId:
                logger.info(f"Creating new user from offline data: {request.user.email}")
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
                logger.info(f"User created successfully: {new_user.email} (ID: {new_user.user_id})")
            else:
                logger.debug(f"User already exists: {request.user.email}")

        # Sync projects
        if request.projects:
            logger.debug(f"Syncing {len(request.projects)} projects")

            for project_data in request.projects:
                existing_project = db.query(Project).filter(
                    Project.project_id == project_data.projectId
                ).first()

                if not existing_project:
                    logger.info(f"Creating new project: {project_data.title} (ID: {project_data.projectId})")
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
                    logger.debug(f"Project added: {project_data.title}")
                else:
                    logger.debug(f"Project already exists: {project_data.projectId}")

            db.commit()
            logger.info(f"Projects committed to database: {projects_synced} new projects")

        server_timestamp = int(datetime.utcnow().timestamp() * 1000)
        logger.info(f"Sync completed successfully. Users: {users_synced}, Projects: {projects_synced}")

        return SyncResponse(
            success=True,
            message="Sync completed successfully",
            usersSynced=users_synced,
            projectsSynced=projects_synced,
            serverTimestamp=server_timestamp
        )

    except Exception as e:
        logger.error(f"Sync operation failed: {str(e)}")
        logger.debug(traceback.format_exc())
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail=f"Sync failed: {str(e)}"
        )
