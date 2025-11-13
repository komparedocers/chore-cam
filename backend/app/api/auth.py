from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel, EmailStr
from app.db.database import get_db
from app.models.models import User
from app.core.security import verify_password, get_password_hash, create_access_token
from app.core.logging_config import get_logger
import uuid
import traceback

router = APIRouter()
logger = get_logger("auth")

class AuthRequest(BaseModel):
    email: EmailStr
    password: str
    username: str = None

class AuthResponse(BaseModel):
    success: bool
    message: str
    user: dict = None
    token: str = None

@router.post("/register", response_model=AuthResponse)
def register(request: AuthRequest, db: Session = Depends(get_db)):
    logger.info(f"Registration attempt for email: {request.email}")

    try:
        # Check if user exists
        existing_user = db.query(User).filter(User.email == request.email).first()
        if existing_user:
            logger.warning(f"Registration failed: Email already exists - {request.email}")
            raise HTTPException(status_code=400, detail="Email already registered")

        # Create new user
        user_id = str(uuid.uuid4())
        username = request.username or request.email.split("@")[0]
        logger.debug(f"Creating new user: email={request.email}, user_id={user_id}, username={username}")

        user = User(
            user_id=user_id,
            email=request.email,
            username=username,
            hashed_password=get_password_hash(request.password),
            is_pro=False
        )

        db.add(user)
        db.commit()
        db.refresh(user)

        logger.info(f"User registered successfully: {user.email} (ID: {user.user_id})")

        # Create access token
        token = create_access_token(data={"sub": user.email})
        logger.debug(f"Access token generated for {user.email}")

        return AuthResponse(
            success=True,
            message="User registered successfully",
            user={
                "userId": user.user_id,
                "email": user.email,
                "username": user.username,
                "isPro": user.is_pro
            },
            token=token
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Registration error for {request.email}: {str(e)}")
        logger.debug(traceback.format_exc())
        db.rollback()
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Registration failed due to server error"
        )

@router.post("/login", response_model=AuthResponse)
def login(request: AuthRequest, db: Session = Depends(get_db)):
    logger.info(f"Login attempt for email: {request.email}")

    try:
        user = db.query(User).filter(User.email == request.email).first()

        if not user:
            logger.warning(f"Login failed: User not found - {request.email}")
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Incorrect email or password"
            )

        if not verify_password(request.password, user.hashed_password):
            logger.warning(f"Login failed: Invalid password - {request.email}")
            raise HTTPException(
                status_code=status.HTTP_401_UNAUTHORIZED,
                detail="Incorrect email or password"
            )

        # Create access token
        token = create_access_token(data={"sub": user.email})
        logger.info(f"Login successful for user: {user.email} (ID: {user.user_id})")

        return AuthResponse(
            success=True,
            message="Login successful",
            user={
                "userId": user.user_id,
                "email": user.email,
                "username": user.username,
                "isPro": user.is_pro
            },
            token=token
        )

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Login error for {request.email}: {str(e)}")
        logger.debug(traceback.format_exc())
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Login failed due to server error"
        )
