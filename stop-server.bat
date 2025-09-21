@echo off
REM ===========================
REM CMX-BE Server Stop Script
REM ===========================

echo Stopping CMX-BE server...

REM Find and kill Java processes running CMX
for /f "tokens=2" %%i in ('tasklist /FI "IMAGENAME eq java.exe" /FO TABLE /NH ^| findstr "cmx"') do (
    echo Killing process %%i
    taskkill /F /PID %%i
)

REM Alternative: Kill all java processes on port 8080
for /f "tokens=5" %%i in ('netstat -ano ^| findstr ":8080"') do (
    echo Killing process on port 8080: %%i
    taskkill /F /PID %%i 2>nul
)

echo CMX-BE server stopped.