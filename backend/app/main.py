from fastapi import FastAPI, Request, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from app.core.config import settings
from app.core.logging_config import get_logger
from app.api import auth, presets, music, style, sync
from app.db.database import engine
from app.models import models
import time
import traceback

# Initialize logging
logger = get_logger("main")

logger.info("Starting ChoreoCam API")

# Create database tables
try:
    models.Base.metadata.create_all(bind=engine)
    logger.info("Database tables created successfully")
except Exception as e:
    logger.error(f"Error creating database tables: {str(e)}")
    logger.debug(traceback.format_exc())

app = FastAPI(
    title="ChoreoCam API",
    description="Backend API for ChoreoCam - Music-synced video editing app",
    version="1.0.0"
)

# Request logging middleware
@app.middleware("http")
async def log_requests(request: Request, call_next):
    request_id = str(time.time())
    logger.info(f"[{request_id}] {request.method} {request.url.path} - Client: {request.client.host}")

    start_time = time.time()

    try:
        response = await call_next(request)
        process_time = time.time() - start_time

        logger.info(
            f"[{request_id}] Completed in {process_time:.3f}s - "
            f"Status: {response.status_code}"
        )

        response.headers["X-Process-Time"] = str(process_time)
        return response

    except Exception as e:
        process_time = time.time() - start_time
        logger.error(
            f"[{request_id}] Request failed after {process_time:.3f}s - "
            f"Error: {str(e)}"
        )
        logger.debug(traceback.format_exc())

        return JSONResponse(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            content={
                "success": False,
                "message": "Internal server error",
                "error": str(e)
            }
        )

# CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

logger.debug(f"CORS origins configured: {settings.CORS_ORIGINS}")

# Include routers
app.include_router(auth.router, prefix=f"{settings.API_V1_PREFIX}/auth", tags=["auth"])
app.include_router(presets.router, prefix=f"{settings.API_V1_PREFIX}/presets", tags=["presets"])
app.include_router(music.router, prefix=f"{settings.API_V1_PREFIX}/music", tags=["music"])
app.include_router(style.router, prefix=f"{settings.API_V1_PREFIX}/style", tags=["style"])
app.include_router(sync.router, prefix=f"{settings.API_V1_PREFIX}/sync", tags=["sync"])

logger.info("All routes registered successfully")

@app.get("/")
def root():
    logger.debug("Root endpoint called")
    return {
        "message": "ChoreoCam API",
        "version": "1.0.0",
        "status": "running"
    }

@app.get("/health")
def health_check():
    logger.debug("Health check endpoint called")
    try:
        # Test database connection
        from app.db.database import SessionLocal
        db = SessionLocal()
        db.execute("SELECT 1")
        db.close()
        return {"status": "healthy", "database": "connected"}
    except Exception as e:
        logger.error(f"Health check failed: {str(e)}")
        return JSONResponse(
            status_code=status.HTTP_503_SERVICE_UNAVAILABLE,
            content={"status": "unhealthy", "error": str(e)}
        )

@app.on_event("startup")
async def startup_event():
    logger.info("=" * 50)
    logger.info("ChoreoCam API Started Successfully")
    logger.info(f"API Version: 1.0.0")
    logger.info(f"Base URL: {settings.API_V1_PREFIX}")
    logger.info("=" * 50)

@app.on_event("shutdown")
async def shutdown_event():
    logger.info("ChoreoCam API Shutting Down...")

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
