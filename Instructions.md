# Popcorn Palace - Setup, Build and Run Instructions

## Prerequisites
- **Java 17** or higher
- **Docker** and **Docker Compose**
- **Gradle** (optional, wrapper is included)

## Environment Setup

### 1. Create Environment File
Create a `.env` file in the root directory with the following content:
```bash
# Database Configuration
POSTGRES_DB=popcorn_palace
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password123
POSTGRES_PORT=5432

# Application Configuration
APP_PORT=10001
```

### 2. Alternative: Set Environment Variables
If you prefer to set environment variables directly:
```bash
export POSTGRES_DB=popcorn_palace
export POSTGRES_USER=postgres
export POSTGRES_PASSWORD=password123
export POSTGRES_PORT=5432
export APP_PORT=10001
```

## Running with Docker (Recommended)

### 1. Start the Application
```bash
# Start both database and application
docker-compose up -d

# Check if services are running
docker-compose ps

# View logs
docker-compose logs -f app
```

### 2. Stop the Application
```bash
docker-compose down
```

### 3. Clean Up (if needed)
```bash
# Remove volumes (this will delete all data)
docker-compose down -v

# Remove images
docker-compose down --rmi all
```

## Running Locally (Development)

### 1. Start Database Only
```bash
# Start only the database
docker-compose up -d db

# Wait for database to be ready
docker-compose logs db
```

### 2. Run Application
```bash
# Using Gradle wrapper
./gradlew bootRun

# Or using Java directly
./gradlew bootJar
java -jar build/libs/*.jar
```

## Building the Project

### 1. Build JAR File
```bash
# Using Gradle wrapper
./gradlew bootJar

# The JAR file will be created in build/libs/
```

### 2. Build Docker Image
```bash
# Build the Docker image
docker build -t popcorn-palace:latest .

# Run the built image
docker run -p 10001:10001 \
  -e POSTGRES_DB=popcorn_palace \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=password123 \
  -e APP_PORT=10001 \
  popcorn-palace:latest
```

## Testing the Application

### 1. Health Check
```bash
# Check if application is running
curl http://localhost:10001/actuator/health

# Or visit in browser
http://localhost:10001/actuator/health
```

### 2. API Documentation
```bash
# Swagger UI
http://localhost:10001/swagger-ui.html

# OpenAPI JSON
http://localhost:10001/v3/api-docs
```

### 3. Test API Endpoints

#### Create a Movie
```bash
curl -X POST http://localhost:10001/api/movies \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Matrix",
    "genre": "Sci-Fi",
    "duration": 136,
    "rating": 8.7,
    "releaseYear": 1999
  }'
```

#### Get All Movies
```bash
curl http://localhost:10001/api/movies
```

## Troubleshooting

### Common Issues

#### 1. Database Connection Issues
```bash
# Check if database is running
docker-compose ps db

# Check database logs
docker-compose logs db

# Test database connection
docker exec -it popcorn_palace_db psql -U postgres -d popcorn_palace
```

#### 2. Port Already in Use
```bash
# Check what's using port 10001
lsof -i :10001

# Kill the process or change the port in .env file
```

#### 3. Application Won't Start
```bash
# Check application logs
docker-compose logs app

# Check if all environment variables are set
docker-compose config
```

### Reset Everything
```bash
# Stop all containers
docker-compose down

# Remove all containers and volumes
docker-compose down -v

# Remove all images
docker system prune -a

# Start fresh
docker-compose up -d
```

## Development Workflow

### 1. Code Changes
- Make changes to Java files
- Rebuild and restart:
  ```bash
  ./gradlew bootJar
  docker-compose restart app
  ```

### 2. Database Changes
- Modify entity classes
- Restart application (JPA will auto-update schema)
- Or use Flyway migrations for production

### 3. Adding Dependencies
- Modify `build.gradle`
- Rebuild Docker image:
  ```bash
  docker-compose build app
  docker-compose up -d
  ```

## Production Considerations

### 1. Environment Variables
- Use strong passwords
- Change default ports
- Use environment-specific configurations

### 2. Database
- Use external PostgreSQL instance
- Configure connection pooling
- Set up backups

### 3. Security
- Enable HTTPS
- Configure CORS properly
- Add authentication if needed

## Support
For any questions or issues:
- Check the logs: `docker-compose logs`
- Review the README.md for API details
- Contact: st198j@intl.att.com, driechlinger.daniel@intl.att.com
