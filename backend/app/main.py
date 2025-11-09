from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.core.config import settings
from app.api import auth, presets, music, style, sync
from app.db.database import engine
from app.models import models

# Create database tables
models.Base.metadata.create_all(bind=engine)

app = FastAPI(
    title="ChoreoCam API",
    description="Backend API for ChoreoCam - Music-synced video editing app",
    version="1.0.0"
)

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include routers
app.include_router(auth.router, prefix=f"{settings.API_V1_PREFIX}/auth", tags=["auth"])
app.include_router(presets.router, prefix=f"{settings.API_V1_PREFIX}/presets", tags=["presets"])
app.include_router(music.router, prefix=f"{settings.API_V1_PREFIX}/music", tags=["music"])
app.include_router(style.router, prefix=f"{settings.API_V1_PREFIX}/style", tags=["style"])
app.include_router(sync.router, prefix=f"{settings.API_V1_PREFIX}/sync", tags=["sync"])

@app.get("/")
def root():
    return {
        "message": "ChoreoCam API",
        "version": "1.0.0",
        "status": "running"
    }

@app.get("/health")
def health_check():
    return {"status": "healthy"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
