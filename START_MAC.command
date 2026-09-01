#!/bin/bash
# =========================================================================
#  🎯 START LEADSCOUT AI - macOS 1-CLICK LAUNCHER & APP INSTALLER
#  Extract the zip, double-click this file, and LeadScout will install
#  its App Icon in /Applications & on your Desktop, then launch instantly!
# =========================================================================

# Clear terminal screen and show banner
clear
echo "================================================================="
echo "  🚀 LEADSCOUT AI - AUTOMATIC MAC INSTALLER & LAUNCHER"
echo "================================================================="
echo ""

# Change to the directory where this script is located
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
cd "$DIR"

# Automatically remove macOS Gatekeeper quarantine attribute on all files
xattr -cr "$DIR" 2>/dev/null || true
xattr -dr com.apple.quarantine "$DIR" 2>/dev/null || true
chmod +x "$DIR"/*.command "$DIR"/*.sh "$DIR"/*.py 2>/dev/null || true

echo "📂 Project Directory: $DIR"
echo "🛡️ Gatekeeper check passed (Quarantine flags cleared)."
echo ""

# 1. Check for Python 3
echo "🔍 [1/4] Checking Python 3..."
if ! command -v python3 &> /dev/null; then
    echo "❌ Python 3 was not detected on your Mac."
    echo "👉 Please install Python 3.9+ from https://www.python.org/downloads/mac-osx/"
    echo "Or run in terminal: brew install python3"
    echo ""
    read -p "Press [Enter] to exit..."
    exit 1
fi
PYTHON_VER=$(python3 --version)
echo "✅ $PYTHON_VER detected."
echo ""

# 2. Install required Python packages
echo "📦 [2/4] Installing Python requirements (Streamlit, Pandas, Requests, OpenPyXL)..."
python3 -m pip install -r "$DIR/requirements.txt" --quiet
if [ $? -ne 0 ]; then
    echo "⚠️ Pip install notice, attempting with --user..."
    python3 -m pip install -r "$DIR/requirements.txt" --user --quiet
fi
echo "✅ Requirements ready."
echo ""

# 3. Create macOS .app Bundle in /Applications and on Desktop
echo "🍎 [3/4] Installing LeadScout AI App Icon on your Mac..."
python3 "$DIR/install_mac_app.py"
echo ""

# 4. Launch Streamlit Application and open in Mac default browser
echo "🚀 [4/4] Launching LeadScout AI Server..."
echo ""
echo "================================================================="
echo "  ✨ LEADSCOUT AI IS RUNNING SIMULTANEOUSLY!"
echo "  💻 Mac Browser:    http://localhost:8501"
echo "  📱 iPhone/Android: Use your Mac's Local Network IP:8501"
echo "================================================================="
echo ""
echo "Opening browser in 2 seconds..."
echo "(Keep this window open while using the app, or launch anytime via the Desktop App Icon)"
echo ""

# Open default browser automatically in background
(sleep 2 && open "http://localhost:8501") &

# Start streamlit with 0.0.0.0 for simultaneous multi-device access (Mac + Mobile phone on Wi-Fi)
python3 -m streamlit run "$DIR/streamlit_app.py" --server.address=0.0.0.0 --server.port=8501
