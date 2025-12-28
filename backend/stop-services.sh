#!/bin/bash

# Assessia Microservices Shutdown Script
# This script stops all running microservices

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}========================================${NC}"
echo -e "${YELLOW}  Stopping Assessia Microservices${NC}"
echo -e "${YELLOW}========================================${NC}"
echo ""

# Function to stop a service
stop_service() {
    local service_name=$1
    local pid_file="logs/${service_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local pid=$(cat "$pid_file")
        if ps -p $pid > /dev/null 2>&1; then
            echo -e "Stopping ${service_name} (PID: $pid)..."
            kill $pid
            rm "$pid_file"
            echo -e "${GREEN}✓ ${service_name} stopped${NC}"
        else
            echo -e "${YELLOW}⚠ ${service_name} not running${NC}"
            rm "$pid_file"
        fi
    else
        echo -e "${YELLOW}⚠ No PID file found for ${service_name}${NC}"
    fi
}

# Stop services in reverse order
stop_service "feedback-service"
stop_service "scoring-service"
stop_service "nlp-service"
stop_service "ocr-service"
stop_service "gateway"
stop_service "eureka-server"

echo ""
echo -e "${GREEN}All services stopped${NC}"
