#!/bin/bash
# ========================================================
#   Starting LeadScout AI on Local Browser (macOS / Linux)
#   Supports Mac, Windows, iPhone and Android Browsers
# ========================================================

echo "🎯 Starting LeadScout AI Streamlit Server..."
echo ""

# Check python3
if ! command -v python3 &> /dev/null
then
    echo "❌ Python 3 could not be found. Please install Python 3.9+ from https://www.python.org/"
    exit 1
fi

echo "📦 [1/2] Installing requirements..."
pip3 install -r requirements.txt

echo ""
echo "🚀 [2/2] Launching Streamlit Web App..."
echo "  💻 Mac Browser:    http://localhost:8501"
echo "  📱 iPhone/Android: Check IP shown in terminal below (e.g. http://192.168.x.x:8501)"
echo ""

streamlit run streamlit_app.py --server.address=0.0.0.0 --server.port=8501
