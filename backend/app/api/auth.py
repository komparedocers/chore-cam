from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.orm import Session
from pydantic import BaseModel, EmailStr
from app.db.database import get_db
from app.models.models import User
from app.core.security import verify_password, get_password_hash, create_access_token
import uuid

router = APIRouter()

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
    # Check if user exists
    existing_user = db.query(User).filter(User.email == request.email).first()
    if existing_user:
        raise HTTPException(status_code=400, detail="Email already registered")

    # Create new user
    user = User(
        user_id=str(uuid.uuid4()),
        email=request.email,
        username=request.username or request.email.split("@")[0],
        hashed_password=get_password_hash(request.password),
        is_pro=False
    )

    db.add(user)
    db.commit()
    db.refresh(user)

    # Create access token
    token = create_access_token(data={"sub": user.email})

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

@router.post("/login", response_model=AuthResponse)
def login(request: AuthRequest, db: Session = Depends(get_db)):
    user = db.query(User).filter(User.email == request.email).first()

    if not user or not verify_password(request.password, user.hashed_password):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password"
        )

    # Create access token
    token = create_access_token(data={"sub": user.email})

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
