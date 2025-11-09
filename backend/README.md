# ChoreoCam Backend

FastAPI backend server for ChoreoCam with PostgreSQL, Redis, and MinIO.

## Features

- RESTful API with FastAPI
- JWT authentication
- PostgreSQL database
- Redis caching
- MinIO/S3 file storage
- Docker containerization
- Automatic sync support

## Requirements

- Docker & Docker Compose (recommended)
- Python 3.11+ (for local development)
- PostgreSQL 15+
- Redis 7+

## Quick Start (Docker)

1. **Start all services**
   ```bash
   cd docker
   docker-compose up -d --build
   ```

2. **Initialize database**
   ```bash
   docker exec -it choreocam-backend python init_db.py
   ```

3. **Check status**
   ```bash
   docker-compose ps
   ```

4. **Access services**
   - API: http://localhost:8000
   - API Docs: http://localhost:8000/docs
   - MinIO Console: http://localhost:9001

## Local Development

1. **Install dependencies**
   ```bash
   cd backend
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   pip install -r requirements.txt
   ```

2. **Configure environment**
   ```bash
   cp .env.example .env
   # Edit .env with your settings
   ```

3. **Start PostgreSQL & Redis**
   ```bash
   # Using Docker
   docker-compose up postgres redis -d
   ```

4. **Run server**
   ```bash
   uvicorn app.main:app --reload
   ```

## API Endpoints

### Authentication
```
POST /v1/auth/register
POST /v1/auth/login
```

### Presets
```
GET  /v1/presets
GET  /v1/presets/{id}
```

### Music
```
GET  /v1/music
GET  /v1/music/{id}
```

### Style Learning
```
POST /v1/style/learn
GET  /v1/style/suggestions
```

### Sync
```
POST /v1/sync
```

### Health Check
```
GET  /
GET  /health
```

## Database Schema

### Users
- id (primary key)
- user_id (unique UUID)
- email (unique)
- username (unique)
- hashed_password
- is_pro (boolean)
- created_at, updated_at

### Projects
- id (primary key)
- project_id (unique UUID)
- user_id (foreign key)
- title, description
- clips_meta_json (JSON)
- chosen_preset_id, music_id
- status (draft/rendering/completed)
- output_file_path
- duration, resolution
- created_at, updated_at

### Presets
- id (primary key)
- preset_id (unique UUID)
- name, description
- thumbnail_url
- category (basic/premium)
- is_pro (boolean)
- transitions_json, effects_json, caption_style_json
- created_at

### MusicTracks
- id (primary key)
- track_id (unique UUID)
- title, artist, genre
- bpm, duration_ms
- file_url
- is_pro (boolean)
- cue_points_json (JSON)
- created_at

### StyleVectors
- id (primary key)
- user_id (foreign key, unique)
- vector (JSON)
- updated_at

## Configuration

### Environment Variables (.env)
```bash
# Database
DATABASE_URL=postgresql://user:password@localhost:5432/choreocam

# JWT
SECRET_KEY=your-secret-key-here
ALGORITHM=HS256
ACCESS_TOKEN_EXPIRE_MINUTES=30

# Redis
REDIS_URL=redis://localhost:6379

# MinIO
MINIO_ENDPOINT=localhost:9000
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=choreocam

# API
API_V1_PREFIX=/v1
CORS_ORIGINS=["*"]
```

## Sync Endpoint Logic

The `/v1/sync` endpoint handles offline data synchronization:

1. **User Sync**: Creates user from offline registration
2. **Project Sync**: Syncs all offline-created projects
3. **Timestamp**: Returns server timestamp for client
4. **Conflict Resolution**: Server data takes precedence

Example request:
```json
{
  "user": {
    "userId": "uuid",
    "email": "user@example.com",
    "username": "username"
  },
  "projects": [
    {
      "projectId": "uuid",
      "title": "My Project",
      "status": "draft"
    }
  ],
  "lastSyncTimestamp": 1234567890
}
```

## Docker Services

### PostgreSQL
- Port: 5432
- Database: choreocam
- User: choreocam
- Volume: postgres_data

### Redis
- Port: 6379
- Volume: redis_data

### MinIO
- API Port: 9000
- Console Port: 9001
- Credentials: minioadmin/minioadmin
- Volume: minio_data

### Backend
- Port: 8000
- Auto-reload in development
- Depends on: postgres, redis, minio

### Nginx
- Port: 80
- Reverse proxy for backend
- WebSocket support

## Database Management

### Initialize with sample data
```bash
python init_db.py
```

### Create migration
```bash
alembic revision --autogenerate -m "description"
```

### Apply migrations
```bash
alembic upgrade head
```

### Rollback migration
```bash
alembic downgrade -1
```

## Testing

### Run tests
```bash
pytest
```

### Test coverage
```bash
pytest --cov=app
```

## Production Deployment

1. **Update .env**
   - Set strong SECRET_KEY
   - Configure production database
   - Set CORS_ORIGINS to specific domains

2. **Build and deploy**
   ```bash
   docker-compose -f docker-compose.yml up -d --build
   ```

3. **Initialize database**
   ```bash
   docker exec -it choreocam-backend python init_db.py
   ```

4. **Enable HTTPS**
   - Configure SSL certificates
   - Update nginx.conf

## Monitoring

### View logs
```bash
docker-compose logs -f backend
```

### Database connection
```bash
docker exec -it choreocam-postgres psql -U choreocam
```

### Redis CLI
```bash
docker exec -it choreocam-redis redis-cli
```

## Security

- JWT tokens for authentication
- Password hashing with bcrypt
- CORS configuration
- SQL injection protection (SQLAlchemy)
- Rate limiting (add middleware)

## Troubleshooting

### Database connection issues
```bash
docker-compose restart postgres
docker-compose logs postgres
```

### Port conflicts
Change ports in docker-compose.yml

### Reset everything
```bash
docker-compose down -v
docker-compose up -d --build
```

## API Documentation

Interactive API docs available at:
- Swagger UI: http://localhost:8000/docs
- ReDoc: http://localhost:8000/redoc
