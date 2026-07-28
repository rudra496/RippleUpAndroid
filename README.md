# RippleUp 🍃 — Native Android App

A gamified climate-habit companion for the [RippleUp](https://github.com/rudra496/ytf)
web platform by **Youth for Tomorrow**. This is the native Android port — a real,
installable app that turns the website's "simulated phone" into a genuine mobile
experience while preserving every feature of the original.

Built with **Kotlin + Jetpack Compose + Material 3 + Room + CameraX + ML Kit**.

---

## ✨ Features

Everything from the web platform, now real and persistent on your phone:

- **Live dashboard** — animated points counter, CO₂ savings, daily streak, eco-actions, badge progress
- **4 eco actions** — Refill (+20), Food (+15), Recycle (+30), Transit (+25), each with CO₂ offsets
- **Real QR scanner** — live camera scanning via ML Kit, plus 3 demo presets (Campus Refill, Organic Grocer, Smart Recycling)
- **Confetti celebration** — leaf + spark burst on every verified action
- **3D-style eco-globe** — Compose-Canvas particle globe in onboarding, echoing the Three.js hero
- **Community leaderboard** — 5 teams + 10 individuals with exact stats and LinkedIn links
- **Impact calculator** — drag sliders, watch CO₂ / plastic / waste / points update live + an animated gauge
- **Full info hub** — about, 6 SDG chips, 10-panel journey, 4 research pillars, survey charts, stakeholder quotes, all 4 legal documents
- **On-device persistence** — progress survives restarts (Room database); fully offline, no accounts, no servers

---

## 🚀 Get the APK (no Android Studio needed)

The app builds itself in the cloud via GitHub Actions — **$0, no install**.

### Step 1 — Push to GitHub
1. Create a **new public repository** (e.g. `RippleUpAndroid`).
2. Push this folder to it:
   ```bash
   cd RippleUpAndroid
   git init
   git add .
   git commit -m "Initial RippleUp Android app"
   git branch -M main
   git remote add origin https://github.com/<your-username>/RippleUpAndroid.git
   git push -u origin main
   ```

### Step 2 — Wait for the build
1. Open your repo → click the **Actions** tab.
2. Watch **"Build Debug APK"** run (first build ~5 min as it caches Gradle).
3. When it shows a green ✅, click that run.

### Step 3 — Download the APK
1. Scroll to the **Artifacts** section at the bottom.
2. Click **`rippleup-debug-apk`** → a `.zip` downloads.
3. Unzip it → you'll find **`app-debug.apk`**.

### Step 4 — Install on your phone
1. Copy the APK to your Android phone (USB, Drive, email — anything).
2. Open it. Android will ask you to **enable "Install unknown apps"** for your file manager — allow it.
3. Tap **Install** → **Open**. 🎉

> **Tip:** Keep the repo public and you get unlimited free builds forever.

---

## 🛠️ Build locally (optional)

If you have Android Studio installed and want to build/run on an emulator or device:

```bash
./gradlew assembleDebug       # build the APK
./gradlew installDebug        # install on a connected device
```
Or open the project folder in Android Studio and click **Run ▶**.

---

## 🧱 Tech & architecture

| Layer | Choice |
|-------|--------|
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation-Compose (single-activity, 5-tab bottom nav) |
| Persistence | Room (2 tables: `user_stats`, `activity_log`) |
| Camera | CameraX + ML Kit Barcode |
| Permissions | Accompanist Permissions |
| Build | Gradle Kotlin DSL + version catalog |
| CI | GitHub Actions (cloud APK) |

```
app/src/main/java/com/yft/rippleup/
├── RippleUpApp.kt          # Application
├── MainActivity.kt         # Single activity
├── data/
│   ├── db/                 # Room entities, DAO, AppDatabase
│   ├── model/              # EcoAction, ScanPreset, Badge enums
│   └── repo/               # StatsRepository (single source of truth)
├── ui/
│   ├── theme/              # Color, Type, Theme (dark glassmorphism)
│   ├── components/         # GlassPanel, AnimatedCounter, Confetti, EcoGlobe, …
│   ├── nav/                # Routes + RippleUpApp scaffold
│   └── screens/            # onboarding, dashboard, actions, scan, leaderboard, more
└── util/                   # EcoMath (calculator formulas), Streak, ClickableExt
```

---

## 📐 Faithful to the original

The exact behaviour from `github.com/rudra496/ytf` is replicated:

- Action points/CO₂ values → `EcoAction` enum (20/0.20, 15/0.30, 30/0.15, 25/0.50)
- Scan presets → `ScanPreset` enum (25/0.20, 20/0.35, 35/0.15)
- Badge thresholds → Bronze (0) / Silver (14 actions) / Gold (16)
- Calculator formulas → `EcoMath.compute()` (CO₂, plastic, waste, points, ×52 annualised)
- Tier ladder → Eco Scout (0) → Carbon Champion (300) → Planet Guardian (700) → Eco Legend (1200)
- Leaderboard data → all 5 teams + 10 individuals verbatim
- Research stats → 77.3% / 63.6% / 70% + Ridwan & Tanvir quotes

---

## 👥 Credits

Built as a native companion to the web platform by **Youth for Tomorrow**
— concept lead **Saara Vishnoi**, engineering **Rudra Sarker**. See the in-app
leaderboard and [the source repo](https://github.com/rudra496/ytf) for the full
contributor roster.

© 2026 RippleUp · Youth for Tomorrow.
