@echo off
title LeadScout AI - 1-Click Starter & Installer (Windows PC)
echo ========================================================
echo   🚀 LeadScout AI - 1-Click Windows PC Starter
echo ========================================================
echo.

:: 1. Check Python
python --version >nul 2>&1
if %errorlevel% neq 0 (
    echo [!] Python is not found in PATH.
    echo Trying py launcher...
    py --version >nul 2>&1
    if %errorlevel% neq 0 (
        echo [ERROR] Python 3.9+ is required.
        echo Opening Python download page...
        start https://www.python.org/downloads/
        pause
        exit /b
    )
)

:: 2. Auto-create Desktop Shortcut on first run
powershell -Command "$ws = New-Object -ComObject WScript.Shell; $s = $ws.CreateShortcut([System.IO.Path]::Combine([System.Environment]::GetFolderPath('Desktop'), 'LeadScout AI.lnk')); $s.TargetPath = '%~dp0START_WINDOWS.bat'; $s.WorkingDirectory = '%~dp0'; $s.IconLocation = 'shell32.dll,13'; $s.Description = 'Launch LeadScout AI'; $s.Save()" >nul 2>&1

echo [1/3] Checking dependencies...
pip install -r "%~dp0requirements.txt" --quiet >nul 2>&1

echo [2/3] Opening LeadScout AI in your browser...
start http://localhost:8501

echo [3/3] Starting LeadScout AI Server...
echo.
echo ========================================================
echo   ✅ LeadScout AI is RUNNING!
echo   - PC Browser: http://localhost:8501
echo   - Desktop shortcut created on your PC Desktop!
echo ========================================================
echo.
streamlit run "%~dp0streamlit_app.py" --server.address=0.0.0.0 --server.port=8501 --server.headless=true

pause

