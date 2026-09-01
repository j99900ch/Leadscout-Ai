"""
LeadScout AI - Streamlit Web Application Entry Point
Supports: Windows, macOS, Linux, VS Code, iPhone (Safari/Chrome), and Android (Chrome).
Run directly with:
    streamlit run app.py
"""
import os
import sys

# Seamlessly load and execute streamlit_app.py
current_dir = os.path.dirname(os.path.abspath(__file__))
streamlit_app_path = os.path.join(current_dir, "streamlit_app.py")

if os.path.exists(streamlit_app_path):
    with open(streamlit_app_path, "r", encoding="utf-8") as f:
        code = f.read()
    exec(code, globals())
else:
    import streamlit as st
    st.error("Error: streamlit_app.py not found in current directory.")
