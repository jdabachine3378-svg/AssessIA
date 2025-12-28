# Assessia Backend - Microservices Architecture

A complete microservices-based system for automated assessment and correction of exams using OCR, NLP, and scoring capabilities.

## Architecture Overview

This project consists of 6 microservices:

1. **Eureka Server** (Port 8761) - Service Discovery
2. **Gateway** (Port 8081) - API Gateway with routing and security
3. **OCR Service** (Port 8082) - Optical Character Recognition for document processing
4. **NLP Service** (Port 8083) - Natural Language Processing for text analysis
5. **Scoring Service** (Port 8084) - Evaluation and scoring engine
6. **Feedback Service** (Port 8085) - Feedback management

## Prerequisites

Before running the project, ensure you have:

- **Java 21** or higher
- **Maven 3.8+**
- **MySQL 8.0+** (for feedback-service and scoring-service)
- **RabbitMQ** (for message queuing)
- **Redis** (for scoring-service caching)

## Setup Instructions

### 1. Database Setup

Create the required MySQL databases:

```sql
CREATE DATABASE assessia_feedback;
CREATE DATABASE assessiabase;
```

Update database credentials in:
- `feedback-service/src/main/resources/application.yml`
- `scoring-service/src/main/resources/application.yaml`

### 2. RabbitMQ Setup

Install and start RabbitMQ:

```bash
# macOS
brew install rabbitmq
brew services start rabbitmq

# Or using Docker
docker run -d --name rabbitmq -p 5673:5672 -p 15672:15672 rabbitmq:3-management
```

RabbitMQ Management Console: http://localhost:15672 (guest/guest)

### 3. Redis Setup

Install and start Redis:

```bash
# macOS
brew install redis
brew services start redis

# Or using Docker
docker run -d --name redis -p 6379:6379 redis:latest
```

### 4. Tesseract OCR Setup (for OCR Service)

Install Tesseract OCR:

```bash
# macOS
brew install tesseract
brew install tesseract-lang  # For multiple language support

# Linux (Ubuntu/Debian)
sudo apt-get install tesseract-ocr
sudo apt-get install tesseract-ocr-fra  # French language data
```

## Building the Project

From the root directory:

```bash
# Build all services
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests
```

## Running the Services

### Option 1: Manual Start (Recommended Order)

Start services in the following order to ensure proper dependency resolution:

1. **Start Eureka Server** (Service Discovery must be first):
```bash
cd eureka-server
mvn spring-boot:run
```
Wait for it to start, then verify at: http://localhost:8761

2. **Start Gateway**:
```bash
cd gateway
mvn spring-boot:run
```

3. **Start Business Services** (can be started in parallel):
```bash
# OCR Service
cd ocr-service
mvn spring-boot:run

# NLP Service
cd nlp-service
mvn spring-boot:run

# Scoring Service
cd scoring-service
mvn spring-boot:run

# Feedback Service
cd feedback-service
mvn spring-boot:run
```

### Option 2: Run from JAR files

After building:

```bash
# Run each service from its target directory
java -jar eureka-server/target/eureka-server-0.0.1-SNAPSHOT.jar
java -jar gateway/target/gateway-0.0.1-SNAPSHOT.jar
java -jar ocr-service/target/ocr-service-0.0.1-SNAPSHOT.jar
java -jar nlp-service/target/nlp-service-0.0.1-SNAPSHOT.jar
java -jar scoring-service/target/scoring-service-0.0.1-SNAPSHOT.jar
java -jar feedback-service/target/feedback-service-0.0.1-SNAPSHOT.jar
```

### Option 3: Using Docker Compose (if docker-compose files are configured)

```bash
docker-compose up -d
```

## Service Endpoints

| Service | Port | Endpoint |
|---------|------|----------|
| Eureka Server | 8761 | http://localhost:8761 |
| Gateway | 8081 | http://localhost:8081 |
| OCR Service | 8082 | http://localhost:8081/ocr/** |
| NLP Service | 8083 | http://localhost:8081/nlp/** |
| Scoring Service | 8084 | http://localhost:8081/scoring/** |
| Feedback Service | 8085 | http://localhost:8081/feedback/** |

**Note**: All business services should be accessed through the Gateway (port 8081).

## Verifying the Setup

1. **Check Eureka Dashboard**: 
   - Visit http://localhost:8761
   - All 5 services (gateway, ocr-service, nlp-service, scoring-service, feedback-service) should be registered

2. **Check Gateway Routes**:
   - Visit http://localhost:8081/actuator/gateway/routes
   - Should show all configured routes

3. **Health Checks**:
```bash
curl http://localhost:8761/actuator/health  # Eureka
curl http://localhost:8081/actuator/health  # Gateway
curl http://localhost:8082/actuator/health  # OCR
curl http://localhost:8083/actuator/health  # NLP
curl http://localhost:8084/actuator/health  # Scoring
curl http://localhost:8085/actuator/health  # Feedback
```

## Configuration

### Service Ports
- Eureka Server: 8761
- Gateway: 8081
- OCR Service: 8082
- NLP Service: 8083
- Scoring Service: 8084
- Feedback Service: 8085

### RabbitMQ
- Port: 5673 (mapped from default 5672)
- Management UI: 15672

### MySQL
- Feedback DB: assessia_feedback (port 3306)
- Scoring DB: assessiabase (port 3306)

### Redis
- Port: 6379

## Troubleshooting

### Services not registering with Eureka
- Ensure Eureka Server is running before starting other services
- Check firewall/network settings
- Verify `eureka.client.service-url.defaultZone` in application.yaml

### Database Connection Issues
- Verify MySQL is running: `mysql.server status`
- Check database credentials in application.yaml files
- Ensure databases are created

### RabbitMQ Connection Issues
- Verify RabbitMQ is running: `rabbitmqctl status`
- Check port 5673 is accessible
- Default credentials: guest/guest (only works for localhost)

### Gateway routing issues
- Check all services are registered in Eureka
- Verify route configuration in gateway/application.yaml
- Check Gateway logs for routing errors

## Development

### Adding a New Service

1. Add module to parent `pom.xml`
2. Create service with parent POM:
```xml
<parent>
    <groupId>com.assessai</groupId>
    <artifactId>assessia-backend</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <relativePath>../pom.xml</relativePath>
</parent>
```
3. Add Eureka Client dependency
4. Configure application.yaml with unique port and service name
5. Add route in Gateway configuration

## Technology Stack

- **Spring Boot**: 3.4.1
- **Spring Cloud**: 2024.0.0
- **Java**: 21
- **Netflix Eureka**: Service Discovery
- **Spring Cloud Gateway**: API Gateway
- **RabbitMQ**: Message Broker
- **MySQL**: Relational Database
- **Redis**: Caching
- **Tesseract**: OCR Engine
- **PDFBox**: PDF Processing

## License

This project is proprietary software for Assessia.

## Support

For issues or questions, please contact the development team.
