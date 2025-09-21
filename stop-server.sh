#!/bin/bash
# ===========================
# CMX-BE Server Stop Script
# ===========================

echo "Stopping CMX-BE server..."

# Find and kill Java processes running CMX
CMX_PIDS=$(ps aux | grep -i "cmx" | grep java | grep -v grep | awk '{print $2}')

if [ ! -z "$CMX_PIDS" ]; then
    echo "Found CMX processes: $CMX_PIDS"
    for pid in $CMX_PIDS; do
        echo "Killing process $pid"
        kill -TERM $pid
        sleep 2
        # Force kill if still running
        if kill -0 $pid 2>/dev/null; then
            echo "Force killing process $pid"
            kill -KILL $pid
        fi
    done
else
    echo "No CMX processes found"
fi

# Alternative: Kill processes on port 8080
PORT_PIDS=$(lsof -ti:8080 2>/dev/null)
if [ ! -z "$PORT_PIDS" ]; then
    echo "Found processes on port 8080: $PORT_PIDS"
    for pid in $PORT_PIDS; do
        echo "Killing process on port 8080: $pid"
        kill -TERM $pid
        sleep 2
        # Force kill if still running
        if kill -0 $pid 2>/dev/null; then
            echo "Force killing process $pid"
            kill -KILL $pid
        fi
    done
else
    echo "No processes found on port 8080"
fi

echo "CMX-BE server stopped."