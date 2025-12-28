#!/bin/bash

# Assessia Microservices Startup Script
# This script starts all microservices in the correct order

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  Assessia Microservices Startup${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# Function to check if a service is running
check_service() {
    local url=$1
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            return 0
        fi
        sleep 2
        attempt=$((attempt + 1))
    done
    return 1
}

# Function to start a service
start_service() {
    local service_name=$1
    local service_dir=$2
    local port=$3
    local health_url="http://localhost:${port}/actuator/health"
    
    echo -e "${YELLOW}Starting ${service_name}...${NC}"
    cd "$service_dir"
    mvn spring-boot:run > "../logs/${service_name}.log" 2>&1 &
    local pid=$!
    echo $pid > "../logs/${service_name}.pid"
    cd - > /dev/null
    
    echo -e "Waiting for ${service_name} to be ready..."
    if check_service "$health_url"; then
        echo -e "${GREEN}✓ ${service_name} started successfully (PID: $pid)${NC}"
        return 0
    else
        echo -e "${RED}✗ ${service_name} failed to start${NC}"
        return 1
    fi
}

# Create logs directory
mkdir -p logs

# Get the backend directory
BACKEND_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo -e "${YELLOW}Building all services...${NC}"
mvn clean install -DskipTests
echo -e "${GREEN}✓ Build completed${NC}"
echo ""

# Start Eureka Server first
echo -e "${YELLOW}Step 1/6: Starting Eureka Server${NC}"
start_service "eureka-server" "$BACKEND_DIR/eureka-server" "8761" || exit 1
echo ""

# Wait a bit for Eureka to fully initialize
echo -e "Waiting for Eureka to fully initialize..."
sleep 5

# Start Gateway
echo -e "${YELLOW}Step 2/6: Starting Gateway${NC}"
start_service "gateway" "$BACKEND_DIR/gateway" "8081" || exit 1
echo ""

# Start OCR Service
echo -e "${YELLOW}Step 3/6: Starting OCR Service${NC}"
start_service "ocr-service" "$BACKEND_DIR/ocr-service" "8082" || exit 1
echo ""

# Start NLP Service
echo -e "${YELLOW}Step 4/6: Starting NLP Service${NC}"
start_service "nlp-service" "$BACKEND_DIR/nlp-service" "8083" || exit 1
echo ""

# Start Scoring Service
echo -e "${YELLOW}Step 5/6: Starting Scoring Service${NC}"
start_service "scoring-service" "$BACKEND_DIR/scoring-service" "8084" || exit 1
echo ""

# Start Feedback Service
echo -e "${YELLOW}Step 6/6: Starting Feedback Service${NC}"
start_service "feedback-service" "$BACKEND_DIR/feedback-service" "8085" || exit 1
echo ""

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}  All services started successfully!${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo -e "Service URLs:"
echo -e "  Eureka Dashboard: ${GREEN}http://localhost:8761${NC}"
echo -e "  Gateway:          ${GREEN}http://localhost:8081${NC}"
echo -e "  OCR Service:      ${GREEN}http://localhost:8082${NC}"
echo -e "  NLP Service:      ${GREEN}http://localhost:8083${NC}"
echo -e "  Scoring Service:  ${GREEN}http://localhost:8084${NC}"
echo -e "  Feedback Service: ${GREEN}http://localhost:8085${NC}"
echo ""
echo -e "To stop all services, run: ${YELLOW}./stop-services.sh${NC}"
echo -e "Logs are available in: ${YELLOW}./logs/${NC}"
echo ""
