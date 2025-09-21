@echo off
REM ====================================
REM CMX-BE Production Profile Startup
REM ====================================

echo Starting CMX-BE with PRODUCTION profile...
echo.

REM Set production environment variables
set SPRING_PROFILES_ACTIVE=prod

REM Database configuration (Override these with your production values)
if "%SPRING_DATASOURCE_URL%"=="" (
    echo WARNING: SPRING_DATASOURCE_URL not set, using default
    set SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/cmx_prod_db
)
if "%SPRING_DATASOURCE_USERNAME%"=="" (
    echo WARNING: SPRING_DATASOURCE_USERNAME not set, using default
    set SPRING_DATASOURCE_USERNAME=cmx_user
)
if "%SPRING_DATASOURCE_PASSWORD%"=="" (
    echo WARNING: SPRING_DATASOURCE_PASSWORD not set
    echo Please set production database password in environment
    pause
    exit /b 1
)

REM Kafka configuration
if "%KAFKA_BROKERS%"=="" (
    echo WARNING: KAFKA_BROKERS not set, using default
    set KAFKA_BROKERS=kafka-cluster:9092
)

REM OAuth2 configuration
if "%OAUTH_ISSUER_URI%"=="" (
    echo WARNING: OAUTH_ISSUER_URI not set
    echo Please set OAuth2 issuer URI in environment
    pause
    exit /b 1
)

REM CORS configuration
if "%CORS_ALLOWED_ORIGINS%"=="" (
    echo WARNING: CORS_ALLOWED_ORIGINS not set, using default
    set CORS_ALLOWED_ORIGINS=https://yourdomain.com
)

REM OpenTelemetry configuration (optional)
if "%OTEL_EXPORTER_OTLP_ENDPOINT%"=="" (
    echo INFO: OTEL_EXPORTER_OTLP_ENDPOINT not set, tracing disabled
)

REM JVM Options for production
set JAVA_OPTS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:InitialRAMPercentage=50 -XX:+ExitOnOutOfMemoryError -XX:+UseG1GC -Dspring.profiles.active=prod

echo Environment variables set for PRODUCTION profile
echo Database: %SPRING_DATASOURCE_URL%
echo Kafka: %KAFKA_BROKERS%
echo OAuth Issuer: %OAUTH_ISSUER_URI%
echo CORS Origins: %CORS_ALLOWED_ORIGINS%
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

echo Building application for production...
mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo Starting Spring Boot application in PRODUCTION mode...
echo Press Ctrl+C to stop the application
echo.
echo Application will be available at:
echo - Main application: http://localhost:8080
echo - Health check: http://localhost:8080/actuator/health
echo - Metrics: http://localhost:8080/actuator/prometheus
echo.

REM Start the application with the built JAR
java %JAVA_OPTS% -jar target/cmx-1.0.0.jar

if %errorlevel% neq 0 (
    echo.
    echo ERROR: Application failed to start
    echo Check the logs above for error details
    pause
)