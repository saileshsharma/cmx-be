#!/bin/bash
# ===================================
# CMX-BE Development Profile Startup
# ===================================

echo "Starting CMX-BE with DEV profile..."
echo

# Set development environment variables
export SPRING_PROFILES_ACTIVE=dev

# Database configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=cmx_new_db1
export DB_USER=producer_user
export DB_PASSWORD=PRODUCER_USER

# Kafka configuration
export KAFKA_BROKERS=192.168.1.172:9092

# Keycloak / Auth configuration
export KEYCLOAK_URL=http://localhost:9091
export KEYCLOAK_REALM=auto-insurance
export KEYCLOAK_CLIENT_ID=cmx-api
export KEYCLOAK_CLIENT_SECRET=graphql-secret

# OpenTelemetry configuration
export OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

# CMX Custom configuration
export CMX_UPLOADER_URL=http://localhost:8081

# JVM Options for development
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -Dspring.profiles.active=dev"

echo "Environment variables set for DEV profile"
echo "Database: $DB_HOST:$DB_PORT/$DB_NAME"
echo "Kafka: $KAFKA_BROKERS"
echo "Keycloak: $KEYCLOAK_URL/realms/$KEYCLOAK_REALM"
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

echo "Starting Spring Boot application..."
echo "Press Ctrl+C to stop the application"
echo
echo "Application will be available at:"
echo "- Main application: http://localhost:8080"
echo "- GraphQL endpoint: http://localhost:8080/graphql"
echo "- Health check: http://localhost:8080/actuator/health"
echo "- GraphiQL (if enabled): http://localhost:8080/graphiql"
echo

# Start the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

if [ $? -ne 0 ]; then
    echo
    echo "ERROR: Application failed to start"
    echo "Check the logs above for error details"
    exit 1
fi