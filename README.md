<div align="center">

# 🎯 LeadScout AI
### *Autonomous B2B Lead Discovery, Power Scoring & Automated Web Sales Intelligence*

<p align="center">
  <img src="https://media.giphy.com/media/LmNwrBhejkK9EFP504/giphy.gif" width="120" alt="Radar / Discovery Animation" />
</p>

[![Python Version](https://img.shields.io/badge/Python-3.10%2B-blue?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![Streamlit](https://img.shields.io/badge/Streamlit-1.32%2B-FF4B4B?style=for-the-badge&logo=streamlit&logoColor=white)](https://streamlit.io/)
[![Google Gemini AI](https://img.shields.io/badge/Google%20Gemini-2.5%20Flash-8E75B2?style=for-the-badge&logo=google&logoColor=white)](https://ai.google.dev/)
[![Android Native](https://img.shields.io/badge/Android-Jetpack%20Compose-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)

<p align="center">
  <b>Transforming messy public directories into verified corporate leads with ground-reality reviews, heuristic power ratings, and objection-proof website sales pitch scripts.</b>
</p>

[✨ Key Features](#-key-features) • [⚡ Quick Start](#-quick-start-vs-code) • [🧠 Core Architecture](#-system-architecture) • [📊 Tech Stack](#-technology-stack) • [📱 Cross-Platform](#-cross-platform-support)

---

</div>

## 💡 What is LeadScout AI?

**LeadScout AI** bridges the gap between raw business discovery and sales conversion. Traditional scrapers extract bare lists without context or quality verification. LeadScout AI pairs real-time web scraping with **Google Gemini LLMs** and a multi-factor **Decision Tree Power Scoring Engine** to:

1. 🔍 **Discover High-Value Targets**: Target specialized niches (e.g., IRDAI-licensed insurance brokers, CBSE/ICSE/IB schools) across 30+ Indian states and districts.
2. ⭐ **Curate Ground-Reality Reviews**: Extract authentic customer and parent sentiment to reveal reputation versus digital friction.
3. 🔥 **Calculate Lead Power Scores (68%–99%)**: Prioritize which leads to contact first based on budget, urgency, and reachability.
4. 🎯 **Synthesize Website Redesign Pitches**: Audit target sites on-the-fly and generate 4 high-converting objection-handling bullet lines to pitch website redesigns.
5. 💬 **Execute 1-Click Outreach**: Initiate instant personalized WhatsApp conversations or draft cold emails with zero contact saving.

---

## ✨ Key Features

<table width="100%">
<tr>
<td width="50%" valign="top">

### 🎯 Intelligent Lead Discovery & Extraction
<img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Magnifying%20Glass%20Tilted%20Right.png" width="35" alt="Search" />
<ul>
  <li><b>Hyper-Local Dorking:</b> Precision filtering by Indian states, commercial districts, and niche specializations.</li>
  <li><b>Decision-Maker Identification:</b> Extracts C-suite executives, principals, and branch directors with direct contact emails and phone lines.</li>
  <li><b>Autonomous Deduplication:</b> Prevents duplicate outreach across recurring discovery batches.</li>
</ul>

</td>
<td width="50%" valign="top">

### 🔥 Heuristic Power Scoring Engine
<img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Travel%20and%20places/High%20Voltage.png" width="35" alt="Power" />
<ul>
  <li><b>Multi-Factor Scoring:</b> Calculates a 68%–99% Lead Power percentage using authority rating, sector purchasing capacity, digital gap, and reachability.</li>
  <li><b>Instant Outreach Prioritization:</b> Visual badges flag <code>🔥 Initiate 1st</code> prospects to maximize sales rep time.</li>
  <li><b>Data-Driven Sorting:</b> Instantly filter leads by top conversion probability.</li>
</ul>

</td>
</tr>

<tr>
<td width="50%" valign="top">

### 🌐 Live Website Audit & Sales Pitch Bullets
<img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Bullseye.png" width="35" alt="Bullseye" />
<ul>
  <li><b>Automated Tech Stack Audit:</b> Highlights slow page loads, non-responsive viewports, and missing WhatsApp funnels.</li>
  <li><b>4 Objection-Proof Pitch Lines:</b>
    <ul>
      <li><i>The Reality Check Hook</i></li>
      <li><i>The Direct ROI Value Proposition</i></li>
      <li><i>Competitive Urgency & FOMO</i></li>
      <li><i>The "Our Old Site is Fine" Objection Buster</i></li>
    </ul>
  </li>
  <li><b>1-Click WhatsApp Script:</b> Formats tailored, professional opening lines ready for instant dispatch.</li>
</ul>

</td>
<td width="50%" valign="top">

### 📥 Enterprise CRM Export & Multi-Format Hub
<img src="https://raw.githubusercontent.com/Tarikul-Islam-Anik/Animated-Fluent-Emojis/master/Emojis/Objects/Card%20Index%20Dividers.png" width="35" alt="Database" />
<ul>
  <li><b>Styled Excel Spreadsheets (.xlsx):</b> Formatted headers, conditional styling, and structured rating columns.</li>
  <li><b>Clean CSV & JSON Exports:</b> Ready for immediate import into HubSpot, Salesforce, Zoho CRM, or Apollo.</li>
  <li><b>Native Share Provider:</b> 1-tap file sharing to Google Drive, Slack, or email.</li>
</ul>

</td>
</tr>
</table>

---
🧠 System Architecture
code
Mermaid
flowchart TD
    A["Target Input: State, City, Sector & Niche"] --> B["LeadScout Scraper & Dorking Engine"]
    B --> C["Gemini AI Extraction & JSON Parsing"]
    C --> D["Deduplication & Sanitization Layer"]
    
    subgraph ANALYSIS["Analysis & Scoring Engine"]
        D --> E["Decision Tree Engine: Authority + Budget + Urgency"]
        E --> F["Power Score %: 68% - 99%"]
        D --> G["Ground-Reality Sentiment & Review Mining"]
        D --> H["Real-Time Website & Conversion Audit"]
    end
    
    H --> I["4-Bullet Sales Pitch Generator"]
    
    subgraph DISTRIBUTION["UI & Distribution Hub"]
        F --> J["Sleek Streamlit Dashboard / Popover System"]
        G --> J
        I --> J
        J --> K["1-Click WhatsApp Direct Action"]
        J --> L["Excel / CSV CRM Export"]
        J --> M["Android Native Companion APK"]
    end

💻 Technology Stack
Domain	Technologies
Frontend Framework	Streamlit, Jetpack Compose (Material 3 for Android companion)
Language	Python 3.10+, Kotlin 2.0+
Generative AI & LLM	Google Gemini API (gemini-2.5-flash, gemini-3.5-flash)
Data Engine & Audit	Pandas, OpenPyXL, BeautifulSoup4, Requests, Regex
Intelligence Engine	Multi-factor Heuristic Decision Logic & Sentiment Categorizer
Export Formats	Styled .xlsx, .csv, .json

⚡ Quick Start (VS Code / Terminal)
Run LeadScout AI locally in just two commands:
1. Clone the repository & enter the directory:
code
Bash
git clone https://github.com/your-username/leadscout-ai.git
cd leadscout-ai
2. Activate your Virtual Environment:
Windows (Command Prompt / PowerShell):
code
Cmd
venv\Scripts\activate
macOS / Linux:
code
Bash
source venv/bin/activate
3. Run the application:
code
Bash
streamlit run app.py
Note: You can also run streamlit run streamlit_app.py. The application automatically binds to http://localhost:8501 and broadcasts to local network devices (tablets and mobile phones).

🔑 Environment Configuration
Create a .env file in the root directory (or use the built-in Settings sidebar):
code
Env
GEMINI_API_KEY=your_gemini_api_key_here
Get a free API key with generous tier limits from Google AI Studio.

📱 Cross-Platform Support
LeadScout AI is built for hybrid desktop and mobile workflows:

💻 Windows PC: Includes a 1-click launcher (START_WINDOWS.bat) that creates a desktop shortcut automatically.

🍏 macOS: Includes START_MAC.command configured to resolve Gatekeeper quarantine flags (xattr -cr) and launch native app bundles.

📱 Android Mobile (PWA & Native APK):
Open in Chrome and select "Add to Home screen" / "Install app" for an instant app icon.
Companion standalone APK bundle is provided in the repository release files.


📂 Project Structure
code
Text
├── app.py                  # Streamlit primary entry point for VS Code execution
├── streamlit_app.py        # Lead intelligence dashboard, scoring engine & UI
├── requirements.txt        # Pre-configured dependency manifest
├── START_WINDOWS.bat       # 1-click Windows runner & shortcut creator
├── START_MAC.command       # 1-click macOS launcher with Gatekeeper bypass
├── START_HERE.html         # Universal multi-platform launch hub
└── README.md               # Project documentation
<div align="center">
Built with precision for modern B2B sales teams and digital marketing agencies.
⭐ If you find this project useful, give it a star on GitHub! ⭐
</div>
