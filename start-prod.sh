#!/bin/bash
# ====================================
# CMX-BE Production Profile Startup
# ====================================

echo "Starting CMX-BE with PRODUCTION profile..."
echo

# Set production environment variables
export SPRING_PROFILES_ACTIVE=prod

# Database configuration (Override these with your production values)
if [ -z "$SPRING_DATASOURCE_URL" ]; then
    echo "WARNING: SPRING_DATASOURCE_URL not set, using default"
    export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/cmx_prod_db"
fi

if [ -z "$SPRING_DATASOURCE_USERNAME" ]; then
    echo "WARNING: SPRING_DATASOURCE_USERNAME not set, using default"
    export SPRING_DATASOURCE_USERNAME="cmx_user"
fi

if [ -z "$SPRING_DATASOURCE_PASSWORD" ]; then
    echo "ERROR: SPRING_DATASOURCE_PASSWORD not set"
    echo "Please set production database password in environment"
    exit 1
fi

# Kafka configuration
if [ -z "$KAFKA_BROKERS" ]; then
    echo "WARNING: KAFKA_BROKERS not set, using default"
    export KAFKA_BROKERS="kafka-cluster:9092"
fi

# OAuth2 configuration
if [ -z "$OAUTH_ISSUER_URI" ]; then
    echo "ERROR: OAUTH_ISSUER_URI not set"
    echo "Please set OAuth2 issuer URI in environment"
    exit 1
fi

# CORS configuration
if [ -z "$CORS_ALLOWED_ORIGINS" ]; then
    echo "WARNING: CORS_ALLOWED_ORIGINS not set, using default"
    export CORS_ALLOWED_ORIGINS="https://yourdomain.com"
fi

# OpenTelemetry configuration (optional)
if [ -z "$OTEL_EXPORTER_OTLP_ENDPOINT" ]; then
    echo "INFO: OTEL_EXPORTER_OTLP_ENDPOINT not set, tracing disabled"
fi

# JVM Options for production
export JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50 -XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -Dspring.profiles.active=prod"

echo "Environment variables set for PRODUCTION profile"
echo "Database: $SPRING_DATASOURCE_URL"
echo "Kafka: $KAFKA_BROKERS"
echo "OAuth Issuer: $OAUTH_ISSUER_URI"
echo "CORS Origins: $CORS_ALLOWED_ORIGINS"
echo

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo "Please install Maven and add it to your PATH"
    exit 1
fi

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed or not in PATH"
    echo "Please install Java 17+ and add it to your PATH"
    exit 1
fi

echo "Building application for production..."
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "ERROR: Build failed"
    exit 1
fi

echo
echo "Starting Spring Boot application in PRODUCTION mode..."
echo "Press Ctrl+C to stop the application"
echo
echo "Application will be available at:"
echo "- Main application: http://localhost:8080"
echo "- Health check: http://localhost:8080/actuator/health"
echo "- Metrics: http://localhost:8080/actuator/prometheus"
echo

# Start the application with the built JAR
java $JAVA_OPTS -jar target/cmx-1.0.0.jar

if [ $? -ne 0 ]; then
    echo
    echo "ERROR: Application failed to start"
    echo "Check the logs above for error details"
    exit 1
fi