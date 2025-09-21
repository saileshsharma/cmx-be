@echo off
REM ===================================
REM CMX-BE Development Profile Startup
REM ===================================

echo Starting CMX-BE with DEV profile...
echo.

REM Set development environment variables
set SPRING_PROFILES_ACTIVE=dev

REM Database configuration
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=cmx_new_db1
set DB_USER=producer_user
set DB_PASSWORD=PRODUCER_USER

REM Kafka configuration
set KAFKA_BROKERS=192.168.1.172:9092

REM Keycloak / Auth configuration
set KEYCLOAK_URL=http://localhost:9091
set KEYCLOAK_REALM=auto-insurance
set KEYCLOAK_CLIENT_ID=cmx-api
set KEYCLOAK_CLIENT_SECRET=graphql-secret

REM OpenTelemetry configuration
set OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

REM CMX Custom configuration
set CMX_UPLOADER_URL=http://localhost:8081

REM JVM Options for development
set JAVA_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC -Dspring.profiles.active=dev

echo Environment variables set for DEV profile
echo Database: %DB_HOST%:%DB_PORT%/%DB_NAME%
echo Kafka: %KAFKA_BROKERS%
echo Keycloak: %KEYCLOAK_URL%/realms/%KEYCLOAK_REALM%
echo.

REM Check if Maven is available
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven and add it to your PATH
    pause
    exit /b 1
)

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java 17+ and add it to your PATH
    pause
    exit /b 1
)

echo Starting Spring Boot application...
echo Press Ctrl+C to stop the application
echo.
echo Application will be available at:
echo - Main application: http://localhost:8080
echo - GraphQL endpoint: http://localhost:8080/graphql
echo - Health check: http://localhost:8080/actuator/health
echo - GraphiQL (if enabled): http://localhost:8080/graphiql
echo.

REM Start the application
mvn spring-boot:run -Dspring-boot.run.profiles=dev

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Application failed to start
    echo Check the logs above for error details
    pause
)