# Quick Setup Guide

## Prerequisites Check

Run these commands to verify you have all prerequisites:

```bash
# Check Java version (should be 21 or higher)
java -version

# Check Maven version (should be 3.8 or higher)
mvn -version

# Check MySQL
mysql --version

# Check RabbitMQ
rabbitmqctl status

# Check Redis
redis-cli ping
```

## Initial Setup (First Time Only)

### 1. Create MySQL Databases

```bash
mysql -u root -p
```

Then run:
```sql
CREATE DATABASE assessia_feedback CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE assessiabase CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SHOW DATABASES;
EXIT;
```

### 2. Update Configuration Files

Update database passwords in:
- `feedback-service/src/main/resources/application.yml`
- `scoring-service/src/main/resources/application.yaml`

Look for:
```yaml
datasource:
  password:   # Add your MySQL password here
```

### 3. Install Tesseract OCR

**macOS:**
```bash
brew install tesseract tesseract-lang
```

**Ubuntu/Debian:**
```bash
sudo apt-get update
sudo apt-get install tesseract-ocr tesseract-ocr-fra
```

### 4. Start Infrastructure Services

**RabbitMQ:**
```bash
# macOS
brew services start rabbitmq

# Or Docker
docker run -d --name rabbitmq -p 5673:5672 -p 15672:15672 rabbitmq:3-management
```

**Redis:**
```bash
# macOS
brew services start redis

# Or Docker
docker run -d --name redis -p 6379:6379 redis:latest
```

**MySQL:**
```bash
# macOS
brew services start mysql

# Or verify it's running
mysql.server status
```

### 5. Build and Start Services

```bash
# Make sure you're in the backend directory
cd /Users/hicham/Desktop/Assessia/backend

# Start all services automatically
./start-services.sh
```

## Manual Start (Alternative)

If you prefer to start services manually:

```bash
# Terminal 1 - Eureka Server
cd eureka-server && mvn spring-boot:run

# Terminal 2 - Gateway (wait for Eureka to start)
cd gateway && mvn spring-boot:run

# Terminal 3 - OCR Service
cd ocr-service && mvn spring-boot:run

# Terminal 4 - NLP Service
cd nlp-service && mvn spring-boot:run

# Terminal 5 - Scoring Service
cd scoring-service && mvn spring-boot:run

# Terminal 6 - Feedback Service
cd feedback-service && mvn spring-boot:run
```

## Verification

1. **Check Eureka Dashboard:**
   - Open: http://localhost:8761
   - You should see all 5 services registered (gateway, ocr-service, nlp-service, scoring-service, feedback-service)

2. **Check Gateway:**
   - Open: http://localhost:8081/actuator/health
   - Should return: `{"status":"UP"}`

3. **Check Individual Services:**
   ```bash
   curl http://localhost:8082/actuator/health  # OCR
   curl http://localhost:8083/actuator/health  # NLP
   curl http://localhost:8084/actuator/health  # Scoring
   curl http://localhost:8085/actuator/health  # Feedback
   ```

## Stopping Services

```bash
./stop-services.sh
```

Or manually kill the processes from each terminal window (Ctrl+C).

## Common Issues

### Port Already in Use
If you get "Address already in use" errors:
```bash
# Find process using port (e.g., 8761)
lsof -i :8761

# Kill the process
kill -9 <PID>
```

### Services Not Registering with Eureka
- Wait 30 seconds after Eureka starts
- Check logs in `./logs/` directory
- Verify Eureka is accessible at http://localhost:8761

### Database Connection Errors
- Verify MySQL is running: `mysql.server status`
- Check credentials in application.yaml files
- Ensure databases exist: `SHOW DATABASES;`

### RabbitMQ Connection Errors
- Verify RabbitMQ is running: `rabbitmqctl status`
- Check management console: http://localhost:15672
- Default credentials: guest/guest

## Development Tips

### View Logs
```bash
# Real-time logs
tail -f logs/eureka-server.log
tail -f logs/gateway.log
tail -f logs/ocr-service.log
# ... etc
```

### Rebuild Single Service
```bash
cd <service-directory>
mvn clean install -DskipTests
mvn spring-boot:run
```

### Run Tests
```bash
# All services
mvn clean test

# Single service
cd <service-directory>
mvn test
```

## Next Steps

After successful startup:
1. Review the main [README.md](README.md) for detailed documentation
2. Check API documentation for each service
3. Test the services using the provided test files
4. Configure any additional settings as needed

## Support

For issues:
1. Check logs in `./logs/` directory
2. Review the troubleshooting section in README.md
3. Contact the development team
