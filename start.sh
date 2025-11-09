#!/bin/bash

echo "==================================="
echo "ChoreoCam - Starting Backend Server"
echo "==================================="
echo ""

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed. Please install Docker first."
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Error: Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

echo "Starting all services with Docker Compose..."
cd docker

# Start services
docker-compose up -d --build

echo ""
echo "Waiting for services to be ready..."
sleep 10

# Initialize database
echo ""
echo "Initializing database with sample data..."
docker exec -it choreocam-backend python init_db.py

echo ""
echo "==================================="
echo "ChoreoCam Backend Started!"
echo "==================================="
echo ""
echo "Services:"
echo "  - API:           http://localhost:8000"
echo "  - API Docs:      http://localhost:8000/docs"
echo "  - MinIO Console: http://localhost:9001"
echo ""
echo "To view logs:"
echo "  docker-compose logs -f"
echo ""
echo "To stop:"
echo "  docker-compose down"
echo ""
