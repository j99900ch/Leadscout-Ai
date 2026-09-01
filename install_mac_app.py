#!/usr/bin/env python3
"""
LeadScout AI - macOS Native App Installer
Creates a standalone macOS '.app' bundle in /Applications and on ~/Desktop
with a custom launcher so clicking the app icon starts LeadScout AI and opens the browser.
"""

import os
import sys
import stat
import subprocess
import shutil
from pathlib import Path

APP_NAME = "LeadScout AI"
BUNDLE_IDENTIFIER = "com.aistudio.leadscoutai"
VERSION = "1.0.0"

def create_mac_app():
    script_dir = Path(__file__).resolve().parent
    applications_dir = Path("/Applications")
    user_apps_dir = Path.home() / "Applications"
    desktop_dir = Path.home() / "Desktop"

    # Prefer user Applications if /Applications is write-protected
    target_apps_dir = applications_dir if os.access(applications_dir, os.W_OK) else user_apps_dir
    target_apps_dir.mkdir(parents=True, exist_ok=True)
    
    app_bundle_path = target_apps_dir / f"{APP_NAME}.app"
    desktop_app_path = desktop_dir / f"{APP_NAME}.app"
    local_app_path = script_dir / f"{APP_NAME}.app"

    print("========================================================")
    print(f" 🍎 Installing {APP_NAME} to macOS Applications & Desktop")
    print("========================================================")

    # 1. Launcher Shell Script inside the .app bundle
    launcher_script_content = f"""#!/bin/bash
DIR="{script_dir}"
cd "$DIR"

# Check if streamlit is running, if not start it
if ! lsof -i:8501 > /dev/null 2>&1; then
    python3 -m pip install -r "$DIR/requirements.txt" > /dev/null 2>&1
    nohup python3 -m streamlit run "$DIR/streamlit_app.py" --server.address=0.0.0.0 --server.port=8501 > "$DIR/app.log" 2>&1 &
    sleep 2
fi

# Open Browser
open "http://localhost:8501"
"""

    # 2. Info.plist content
    info_plist_content = f"""<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
    <key>CFBundleExecutable</key>
    <string>LeadScoutAI</string>
    <key>CFBundleIdentifier</key>
    <string>{BUNDLE_IDENTIFIER}</string>
    <key>CFBundleName</key>
    <string>{APP_NAME}</string>
    <key>CFBundleDisplayName</key>
    <string>{APP_NAME}</string>
    <key>CFBundlePackageType</key>
    <string>APPL</string>
    <key>CFBundleShortVersionString</key>
    <string>{VERSION}</string>
    <key>CFBundleVersion</key>
    <string>1</string>
    <key>LSMinimumSystemVersion</key>
    <string>10.13</string>
    <key>LSUIElement</key>
    <false/>
    <key>NSHighResolutionCapable</key>
    <true/>
</dict>
</plist>
"""

    # 3. Build bundle directories
    for path in [app_bundle_path, local_app_path]:
        contents_dir = path / "Contents"
        macos_dir = contents_dir / "MacOS"
        resources_dir = contents_dir / "Resources"

        macos_dir.mkdir(parents=True, exist_ok=True)
        resources_dir.mkdir(parents=True, exist_ok=True)

        # Write Info.plist
        (contents_dir / "Info.plist").write_text(info_plist_content, encoding="utf-8")
        
        # Write PkgInfo
        (contents_dir / "PkgInfo").write_text("APPL????", encoding="utf-8")

        # Write Executable
        exec_file = macos_dir / "LeadScoutAI"
        exec_file.write_text(launcher_script_content, encoding="utf-8")
        
        # Make executable
        st = os.stat(exec_file)
        os.chmod(exec_file, st.st_mode | stat.S_IEXEC | stat.S_IXGRP | stat.S_IXOTH)

    # 4. Create Desktop Shortcut / App copy
    try:
        if desktop_dir.exists():
            if desktop_app_path.exists():
                shutil.rmtree(desktop_app_path)
            shutil.copytree(local_app_path, desktop_app_path)
            print(f"✅ Created Desktop App: {desktop_app_path}")
    except Exception as e:
        print(f"ℹ️ Note creating desktop shortcut: {e}")

    # 5. Register with macOS LaunchServices & Clear Quarantine
    try:
        subprocess.run(["xattr", "-cr", str(app_bundle_path)], check=False)
        subprocess.run(["xattr", "-dr", "com.apple.quarantine", str(app_bundle_path)], check=False)
        subprocess.run(["touch", str(app_bundle_path)], check=False)
        if desktop_app_path.exists():
            subprocess.run(["xattr", "-cr", str(desktop_app_path)], check=False)
            subprocess.run(["xattr", "-dr", "com.apple.quarantine", str(desktop_app_path)], check=False)
            subprocess.run(["touch", str(desktop_app_path)], check=False)
    except Exception:
        pass

    print(f"🎉 Successfully installed '{APP_NAME}.app'!")
    print(f"📍 Location: {app_bundle_path}")
    print(f"🖥️ Desktop Icon: {desktop_app_path}")
    print("👉 You can now double click the app icon from Applications or Desktop at any time!")

if __name__ == "__main__":
    create_mac_app()
