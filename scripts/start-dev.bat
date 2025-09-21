@echo off
setlocal ENABLEDELAYEDEXPANSION

REM ===================================
REM CMX-BE Development Profile Startup
REM ===================================

REM --- Move to the script's directory (so pom.xml is found)
cd /d "%~dp0"

echo.
echo === CMX-BE :: DEV Startup ===
echo.

REM --- Pick Maven launcher: mvnw if present, else mvn
set MVN=.\mvnw.cmd
if not exist "%MVN%" (
  set MVN=mvn
)

REM --- Check Java
where java >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Java not found in PATH. Please install Java 17+ and add to PATH or set JAVA_HOME.
  goto :fail
)
for /f "tokens=* usebackq" %%j in (`java -version 2^>^&1`) do (
  echo [JAVA] %%j
)

REM --- Check Maven (or wrapper)
if /I "%MVN%"=="mvn" (
  where mvn >nul 2>&1
  if errorlevel 1 (
    echo [ERROR] Maven not found in PATH and no mvnw wrapper present.
    goto :fail
  )
)
"%MVN%" -v
if errorlevel 1 (
  echo [ERROR] Maven is installed but failed to run.
  goto :fail
)

echo.
echo Setting environment variables for DEV profile...
echo.

REM --------- ENV: Spring profile
set SPRING_PROFILES_ACTIVE=dev

REM --------- ENV: Database
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=cmx_new_db1
set DB_USER=producer_user
set DB_PASSWORD=PRODUCER_USER

REM --------- ENV: Kafka
set KAFKA_BROKERS=192.168.1.172:9092

REM --------- ENV: Keycloak / Auth
set KEYCLOAK_URL=http://localhost:9091
set KEYCLOAK_REALM=auto-insurance
set KEYCLOAK_CLIENT_ID=cmx-api
set KEYCLOAK_CLIENT_SECRET=graphql-secret

REM --------- ENV: OpenTelemetry
set OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

REM --------- ENV: CMX Custom
set CMX_UPLOADER_URL=http://localhost:8081

REM --------- JVM / Maven opts (note: JAVA_OPTS not read by Maven; prefer MAVEN_OPTS or JAVA_TOOL_OPTIONS)
set "JAVA_TOOL_OPTIONS=-Xmx2g -Xms1g -XX:+UseG1GC"
REM Optionally: set "MAVEN_OPTS=-Xmx2g -Xms1g -XX:+UseG1GC"

echo  Profile     : %SPRING_PROFILES_ACTIVE%
echo  DB          : %DB_HOST%:%DB_PORT%/%DB_NAME% (user=%DB_USER%)
echo  Kafka       : %KAFKA_BROKERS%
echo  Keycloak    : %KEYCLOAK_URL%/realms/%KEYCLOAK_REALM%
echo  Uploader    : %CMX_UPLOADER_URL%
echo  OTEL        : %OTEL_EXPORTER_OTLP_ENDPOINT%
echo.

REM --- Quick pre-flight: check ports if you have PowerShell
where powershell >nul 2>&1
if not errorlevel 1 (
  for %%P in (8080 8081 9091 4317 5432) do (
    powershell -NoProfile -Command "Get-NetTCPConnection -LocalPort %%P -ErrorAction SilentlyContinue | Out-Null; if($?) { Write-Host [WARN] Port %%P is in use }"
  )
)

echo.
echo Starting Spring Boot on http://localhost:8080  (GraphQL: /graphql)
echo (Stop with Ctrl+C)
echo.

REM --- Run Spring Boot with explicit profile and show full logs if it fails
"%MVN%" spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILES_ACTIVE%
set RUN_EXIT=%ERRORLEVEL%

if %RUN_EXIT% NEQ 0 (
  echo.
  echo [ERROR] Application failed to start (exit code %RUN_EXIT%)
  echo Tips:
  echo   1) Ensure application-%SPRING_PROFILES_ACTIVE%.properties uses variables like \${DB_HOST}, \${KAFKA_BROKERS}, etc.
  echo   2) Check for port conflicts on 8080 / 5432 / 9091 / 4317.
  echo   3) If using Keycloak locally, verify realm '%KEYCLOAK_REALM%' exists and issuer matches.
  echo   4) If PostgreSQL requires SSL or different host, adjust DB_* vars accordingly.
  echo   5) Run with debug:   "%MVN%" -e -X spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILES_ACTIVE%
  goto :fail
)

goto :eof

:fail
echo.
echo Press any key to exit...
pause >nul
exit /b 1
