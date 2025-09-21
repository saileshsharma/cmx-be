@echo off
setlocal ENABLEDELAYEDEXPANSION

REM ============================================
REM CMX-BE :: DEV Startup with Status & Insights
REM ============================================

REM -------- Tunables --------
set "SPRING_PROFILE=dev"
set "RUN_MODE=spring-boot-run"   REM options: spring-boot-run | jar
set "BUILD_BEFORE_RUN=false"     REM only for RUN_MODE=jar; set true to rebuild always
set "JAVA_MEM=-Xms1g -Xmx2g -XX:+UseG1GC"
set "APP_PORT=8080"
set "ACTUATOR_HEALTH=http://localhost:%APP_PORT%/actuator/health"
set "ACTUATOR_ENV=http://localhost:%APP_PORT%/actuator/env"
set "HEALTH_TIMEOUT_SEC=120"     REM how long to wait for health=UP
set "CHECK_PORTS=8080 5432 9091 4317"
set "TITLE=CMX-BE (DEV)"
REM --------------------------

REM --- Move to script directory (repo root)
cd /d "%~dp0"

title %TITLE%
echo.
echo =====================================================
echo  %TITLE% :: Startup
echo =====================================================
echo.

REM --- Pick Maven launcher (prefer wrapper)
set "MVN=.\mvnw.cmd"
if not exist "%MVN%" set "MVN=mvn"

REM ======== PREREQUISITES ========
echo [*] Checking prerequisites...

where java >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Java not found in PATH. Install Java 17+ and/or set JAVA_HOME.
  goto :fail
)
for /f "tokens=* usebackq" %%j in (`java -version 2^>^&1`) do echo [JAVA] %%j

if /I "%MVN%"=="mvn" (
  where mvn >nul 2>&1
  if errorlevel 1 (
    echo [ERROR] Maven not found and no mvnw wrapper present.
    goto :fail
  )
)
%MVN% -v >nul 2>&1
if errorlevel 1 (
  echo [ERROR] Maven detected but failed to run. Check your installation.
  goto :fail
)

where curl >nul 2>&1
if errorlevel 1 (
  echo [WARN] curl not found. Health checks will be limited.
)

where powershell >nul 2>&1
if errorlevel 1 (
  echo [WARN] PowerShell not found. Port checks will be limited.
)

REM ======== ENV VARS ========
echo.
echo [*] Loading environment variables for profile: %SPRING_PROFILE%

REM --------- Database
set DB_HOST=localhost
set DB_PORT=5432
set DB_NAME=cmx_new_db1
set DB_USER=producer_user
set DB_PASSWORD=PRODUCER_USER

REM --------- Kafka
set KAFKA_BROKERS=192.168.1.172:9092

REM --------- Keycloak / Auth
set KEYCLOAK_URL=http://localhost:9091
set KEYCLOAK_REALM=auto-insurance
set KEYCLOAK_CLIENT_ID=cmx-api
set KEYCLOAK_CLIENT_SECRET=graphql-secret

REM --------- OpenTelemetry
set OTEL_EXPORTER_OTLP_ENDPOINT=http://localhost:4317

REM --------- CMX Custom
set CMX_UPLOADER_URL=http://localhost:8081

REM --------- JVM (works for both mvn and jar via JAVA_TOOL_OPTIONS)
set "JAVA_TOOL_OPTIONS=%JAVA_MEM%"

echo    Profile    : %SPRING_PROFILE%
echo    DB         : %DB_HOST%:%DB_PORT%/%DB_NAME% (user=%DB_USER%)
echo    Kafka      : %KAFKA_BROKERS%
echo    Keycloak   : %KEYCLOAK_URL%/realms/%KEYCLOAK_REALM%
echo    Uploader   : %CMX_UPLOADER_URL%
echo    OTEL       : %OTEL_EXPORTER_OTLP_ENDPOINT%
echo.

REM ======== PREFLIGHT: PORTS ========
echo [*] Preflight: checking common ports...
if not errorlevel 1 (
  for %%P in (%CHECK_PORTS%) do (
    powershell -NoProfile -Command ^
      "$p=Get-NetTCPConnection -LocalPort %%P -ErrorAction SilentlyContinue; if($p){Write-Host ('[WARN] Port %%P is in use by PID(s): ' + ($p.OwningProcess -join ','))} else {Write-Host '[OK]   Port %%P is free'}"
  )
) else (
  echo [INFO] Skipping detailed port check (PowerShell not available).
)

REM ======== RUN ========
echo.
echo [*] Starting CMX-BE on http://localhost:%APP_PORT%
echo     GraphQL  : http://localhost:%APP_PORT%/graphql
echo     Health   : %ACTUATOR_HEALTH%
echo.

if /I "%RUN_MODE%"=="jar" goto :runJar
goto :runMvn

:runMvn
echo [MODE] spring-boot:run
echo [CMD ] %MVN% spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILE%
echo.
%MVN% spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILE%
set "RUN_EXIT=%ERRORLEVEL%"
if %RUN_EXIT% NEQ 0 (
  echo.
  echo [ERROR] Application terminated with exit code %RUN_EXIT%.
  goto :postRunFail
)
goto :postRunOk

:runJar
echo [MODE] jar
if /I "%BUILD_BEFORE_RUN%"=="true" (
  echo [*] Building project (skip tests)...
  %MVN% -DskipTests package
  if errorlevel 1 (
    echo [ERROR] Maven build failed.
    goto :fail
  )
)

REM Find newest jar in target
set "JAR="
for /f "delims=" %%F in ('dir /b /o:-d target\*.jar 2^>nul') do (
  set "JAR=target\%%F"
  goto :gotJar
)
:gotJar
if "%JAR%"=="" (
  echo [ERROR] No JAR found in target\. Build the project first.
  goto :fail
)

echo [CMD ] java -jar "%JAR%" --spring.profiles.active=%SPRING_PROFILE%
echo.
start "CMX-BE (DEV)" cmd /c java -jar "%JAR%" --spring.profiles.active=%SPRING_PROFILE%
echo [*] Launched in a new window. Performing health checks...
goto :healthPoll

REM ======== HEALTH & RESOLVED CONFIG ========
:healthPoll
if not exist "%SystemRoot%\System32\curl.exe" (
  echo [INFO] curl unavailable; skipping HTTP health checks.
  goto :end
)

set /a "elapsed=0"
set /a "timeout=%HEALTH_TIMEOUT_SEC%"

:pollLoop
>nul 2>&1 curl -s %ACTUATOR_HEALTH%
if %ERRORLEVEL% EQU 0 (
  for /f "tokens=* usebackq" %%H in (`curl -s %ACTUATOR_HEALTH%`) do (
    echo [HEALTH] %%H
    echo %%H | find /I "\"status\":\"UP\"" >nul
    if not errorlevel 1 (
      echo [OK] Service reported UP.
      goto :showEnv
    )
  )
) else (
  echo [WAIT] Service not responding yet...
)

REM sleep 2s (via ping)
ping -n 3 127.0.0.1 >nul
set /a elapsed+=2
if %elapsed% LSS %timeout% goto :pollLoop

echo [WARN] Health did not become UP within %timeout%s. Continuing anyway...
goto :showEnv

:showEnv
echo.
echo [*] Attempting to display resolved Spring config (via /actuator/env)...
echo     (Requires 'management.endpoints.web.exposure.include=env' in dev)
echo.

REM Print three key resolved properties if available
for %%K in (spring.datasource.url spring.kafka.bootstrap-servers spring.security.oauth2.resourceserver.jwt.issuer-uri) do (
  for /f "usebackq tokens=* delims=" %%R in (`
    powershell -NoProfile -Command ^
      "try{$envJson=(Invoke-WebRequest -UseBasicParsing '%ACTUATOR_ENV%').Content | ConvertFrom-Json;}catch{}; ^
       if($envJson -and $envJson.propertySources){ ^
         $val=($envJson.propertySources | ForEach-Object { $_.properties.'%%K' } | Where-Object { $_ } | Select-Object -First 1).value; ^
         if($val){Write-Output $val} else {Write-Output '__NOT_FOUND__'} ^
       } else {Write-Output '__NO_ENV__'}"
  `) do (
    if /I "%%R"=="__NO_ENV__" (
      echo [INFO] /actuator/env not exposed or unreachable.
      goto :end
    ) else if /I "%%R"=="__NOT_FOUND__" (
      echo [CFG] %%K = (not found)
    ) else (
      echo [CFG] %%K = %%R
    )
  )
)

goto :end

REM ======== POST-RUN HANDLERS ========
:postRunOk
echo.
echo [INFO] Application exited normally.
goto :end

:postRunFail
echo.
echo [HINTS]
echo   1) Ensure application-%SPRING_PROFILE%.properties uses \${...} placeholders for env:
echo      spring.datasource.url=jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
echo      spring.datasource.username=${DB_USER}
echo      spring.datasource.password=${DB_PASSWORD}
echo      spring.kafka.bootstrap-servers=${KAFKA_BROKERS}
echo      spring.security.oauth2.resourceserver.jwt.issuer-uri=${KEYCLOAK_URL}/realms/${KEYCLOAK_REALM}
echo      otel.exporter.otlp.endpoint=${OTEL_EXPORTER_OTLP_ENDPOINT}
echo      cmx.uploader.url=${CMX_UPLOADER_URL}
echo   2) Free busy ports (8080/5432/9091/4317) or change APP_PORT.
echo   3) If Keycloak is off, temporarily disable in dev:
echo      security.oauth2.enabled=false
echo      spring.security.oauth2.resourceserver.jwt.issuer-uri=
echo   4) Re-run with debug:
echo      %MVN% -e -X spring-boot:run -Dspring-boot.run.profiles=%SPRING_PROFILE%
goto :fail

:fail
echo.
echo Press any key to exit...
pause >nul
exit /b 1

:end
echo.
echo Done.
endlocal
