@echo off
title LeadScout AI - Streamlit Web Server (Windows)
echo ========================================================
echo   Starting LeadScout AI on Local Browser (Windows)
echo   Supports Windows, Mac, iPhone and Android Browsers
echo ========================================================
echo.

:: Check for Python
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] Python is not installed or not found in PATH.
    echo Please install Python 3.9+ from https://www.python.org/downloads/
    pause
    exit /b
)

echo [1/2] Installing requirements...
pip install -r requirements.txt

echo.
echo [2/2] Launching Streamlit Web App...
echo - Local Windows Browser: http://localhost:8501
echo - iPhone / Android Phone (same Wi-Fi): http://0.0.0.0:8501
echo.
streamlit run streamlit_app.py --server.address=0.0.0.0 --server.port=8501

pause
