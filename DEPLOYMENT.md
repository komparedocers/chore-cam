# ChoreoCam Deployment Guide

Complete deployment guide for ChoreoCam Android app and backend server.

## Prerequisites

### For Android App
- Android Studio
- Java Development Kit (JDK) 11+
- Google Play Console account (for publishing)
- AdMob account
- Google Play Billing setup

### For Backend
- VPS or cloud hosting (AWS, DigitalOcean, etc.)
- Docker & Docker Compose
- Domain name
- SSL certificate

## Android App Deployment

### 1. Configure Production Settings

Edit `app.config.json`:
```json
{
  "backend": {
    "base_url": "https://api.yourapp.com",
    "fallback_to_local": true
  },
  "ads": {
    "enabled": true,
    "admob_app_id": "ca-app-pub-REAL-ID-HERE",
    "banner_ad_unit_id": "ca-app-pub-REAL-BANNER-ID",
    "interstitial_ad_unit_id": "ca-app-pub-REAL-INTERSTITIAL-ID",
    "test_mode": false
  },
  "iap": {
    "enabled": true,
    "pro_monthly_sku": "choreocam_pro_monthly",
    "pro_yearly_sku": "choreocam_pro_yearly"
  }
}
```

### 2. Setup Signing

Create keystore:
```bash
keytool -genkey -v -keystore choreocam.keystore -alias choreocam -keyalg RSA -keysize 2048 -validity 10000
```

Add to `android/app/build.gradle`:
```gradle
android {
    signingConfigs {
        release {
            storeFile file("../choreocam.keystore")
            storePassword "your-password"
            keyAlias "choreocam"
            keyPassword "your-password"
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

### 3. Build Release

```bash
cd android
./gradlew assembleRelease
# or for App Bundle (recommended)
./gradlew bundleRelease
```

Output:
- APK: `app/build/outputs/apk/release/app-release.apk`
- AAB: `app/build/outputs/bundle/release/app-release.aab`

### 4. Setup AdMob

1. Create app in AdMob console
2. Create ad units (banner, interstitial)
3. Update IDs in `app.config.json`
4. Test with test mode enabled first

### 5. Setup In-App Billing

1. Create products in Google Play Console:
   - `choreocam_pro_monthly` - Monthly subscription
   - `choreocam_pro_yearly` - Yearly subscription

2. Set pricing and trial periods

3. Test with license testers

### 6. Publish to Google Play

1. Create app in Google Play Console
2. Upload AAB
3. Fill in store listing
4. Set content rating
5. Submit for review

## Backend Deployment

### 1. Server Setup

#### Option A: DigitalOcean Droplet

```bash
# Create droplet with Docker pre-installed
# SSH into server
ssh root@your-server-ip

# Clone repository
git clone https://github.com/yourusername/choreocam.git
cd choreocam
```

#### Option B: AWS EC2

```bash
# Launch EC2 instance (Ubuntu 22.04)
# SSH into instance
ssh -i your-key.pem ubuntu@your-ec2-ip

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Clone repository
git clone https://github.com/yourusername/choreocam.git
cd choreocam
```

### 2. Configure Production Environment

```bash
cd backend
cp .env.example .env
nano .env
```

Update `.env`:
```bash
DATABASE_URL=postgresql://choreocam:STRONG-PASSWORD@postgres:5432/choreocam
SECRET_KEY=GENERATE-STRONG-RANDOM-KEY-HERE
REDIS_URL=redis://redis:6379
MINIO_ENDPOINT=minio:9000
MINIO_ACCESS_KEY=STRONG-ACCESS-KEY
MINIO_SECRET_KEY=STRONG-SECRET-KEY
CORS_ORIGINS=["https://yourapp.com"]
```

Generate strong SECRET_KEY:
```bash
python -c "import secrets; print(secrets.token_urlsafe(32))"
```

### 3. Setup SSL Certificate

Using Let's Encrypt:
```bash
sudo apt-get install certbot
sudo certbot certonly --standalone -d api.yourapp.com
```

Update `docker/nginx.conf`:
```nginx
server {
    listen 443 ssl;
    server_name api.yourapp.com;

    ssl_certificate /etc/letsencrypt/live/api.yourapp.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/api.yourapp.com/privkey.pem;

    location / {
        proxy_pass http://backend:8000;
        # ... rest of config
    }
}

server {
    listen 80;
    server_name api.yourapp.com;
    return 301 https://$server_name$request_uri;
}
```

### 4. Deploy with Docker

```bash
cd docker

# Start all services
docker-compose up -d --build

# Initialize database
docker exec -it choreocam-backend python init_db.py

# Check status
docker-compose ps

# View logs
docker-compose logs -f
```

### 5. Setup Firewall

```bash
# UFW (Ubuntu)
sudo ufw allow 22/tcp    # SSH
sudo ufw allow 80/tcp    # HTTP
sudo ufw allow 443/tcp   # HTTPS
sudo ufw enable
```

### 6. Setup Auto-restart

Create systemd service `/etc/systemd/system/choreocam.service`:
```ini
[Unit]
Description=ChoreoCam Backend
After=docker.service
Requires=docker.service

[Service]
Type=oneshot
RemainAfterExit=yes
WorkingDirectory=/root/choreocam/docker
ExecStart=/usr/local/bin/docker-compose up -d
ExecStop=/usr/local/bin/docker-compose down
TimeoutStartSec=0

[Install]
WantedBy=multi-user.target
```

Enable:
```bash
sudo systemctl enable choreocam
sudo systemctl start choreocam
```

## DNS Configuration

Point your domain to the server:
```
A Record:  api.yourapp.com  →  YOUR-SERVER-IP
```

## Monitoring & Maintenance

### Monitor Logs
```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f backend
```

### Database Backup
```bash
# Backup
docker exec choreocam-postgres pg_dump -U choreocam choreocam > backup.sql

# Restore
docker exec -i choreocam-postgres psql -U choreocam choreocam < backup.sql
```

### Update Application
```bash
# Pull latest code
git pull origin main

# Rebuild and restart
cd docker
docker-compose down
docker-compose up -d --build
```

### Monitor Resources
```bash
# Disk usage
docker system df

# Container stats
docker stats

# Clean up
docker system prune -a
```

## Security Checklist

- [ ] Strong database passwords
- [ ] Strong JWT secret key
- [ ] HTTPS enabled
- [ ] Firewall configured
- [ ] Regular backups scheduled
- [ ] Security updates automated
- [ ] Rate limiting enabled
- [ ] CORS properly configured
- [ ] API keys not in code
- [ ] SSH key-based auth only

## Performance Optimization

### Database
```sql
-- Add indexes
CREATE INDEX idx_projects_user_id ON projects(user_id);
CREATE INDEX idx_projects_status ON projects(status);
```

### Redis Caching
Implement caching for:
- Presets list
- Music tracks
- User profiles

### CDN
Use CDN for:
- Static assets
- Music files
- Preset thumbnails

## Scaling

### Horizontal Scaling
1. Load balancer (nginx, AWS ELB)
2. Multiple backend instances
3. Shared database
4. Shared Redis
5. Shared MinIO/S3

### Vertical Scaling
- Increase server resources
- Optimize database queries
- Add indexes
- Enable query caching

## Troubleshooting

### Backend not responding
```bash
docker-compose logs backend
docker-compose restart backend
```

### Database connection issues
```bash
docker-compose logs postgres
docker exec -it choreocam-postgres psql -U choreocam
```

### Out of disk space
```bash
docker system prune -a
docker volume prune
```

### SSL certificate renewal
```bash
sudo certbot renew
docker-compose restart nginx
```

## Cost Estimation

### Infrastructure (Monthly)
- **VPS (2GB RAM, 2 CPU)**: $10-20
- **Database backup**: $5
- **SSL certificate**: Free (Let's Encrypt)
- **Total**: ~$15-25/month

### Scalability
- Add load balancer: +$10/month
- Increase server size: +$20/month per tier
- S3 storage: $0.02/GB

### Android
- Google Play Console: $25 one-time
- AdMob: Revenue generating
- In-app purchases: 15% Google fee
