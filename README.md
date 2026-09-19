# 🌿 Nature Moments — Peaceful Nature Reels & Studio Admin Portal

[![Live App](https://img.shields.io/badge/Live%20App-Online-52B788?style=for-the-badge&logo=google-chrome&logoColor=white)](https://gulshan8285.github.io/terra-nova-nature/)
[![Admin Studio](https://img.shields.io/badge/Admin%20Studio-Live-E88D67?style=for-the-badge&logo=safari&logoColor=white)](https://gulshan8285.github.io/terra-nova-nature/admin.html)
[![Android APK](https://img.shields.io/badge/Android%20APK-v1.2.0%20(5.0MB)-17483C?style=for-the-badge&logo=android&logoColor=white)](NatureMoments.apk)

> A modern, high-performance nature reels platform featuring authentic 9:16 vertical video playback, ambient nature soundscapes, full offline downloading, 20+ curated categories, and a dedicated **Studio Admin Portal** with zero-latency live sync directly into the user mobile application.

---

## 🌟 Live Links

| Portal | URL | Description |
| :--- | :--- | :--- |
| 📱 **User Mobile Web App** | [https://gulshan8285.github.io/terra-nova-nature/](https://gulshan8285.github.io/terra-nova-nature/) | High-definition 9:16 reels, category scroller, and ambient audio |
| 🛠️ **Studio Admin Portal** | [https://gulshan8285.github.io/terra-nova-nature/admin.html](https://gulshan8285.github.io/terra-nova-nature/admin.html) | Upload reels, manage 20+ categories, live phone simulator & GitHub sync |
| 📦 **Android Release APK** | [`NatureMoments.apk`](NatureMoments.apk) | Standalone release build for Android devices |

---

## ✨ Key Features

### 1. Studio Admin Portal (`admin.html`)
- **Direct App Live Sync**: Videos uploaded or added in the Admin Portal appear in the user mobile app immediately via `BroadcastChannel` and dynamic `reels.json` remote fetching.
- **Interactive 9:16 Phone Simulator**: Real-time mockup showing exactly how the reel looks to mobile users before publishing.
- **20+ Nature Categories Manager**: Add, edit, and organize all nature categories (Forest, Waterfall, Mountain, Ocean, Rain, Sunrise, Sunset, Wildlife, etc.).
- **Dual Video Input**: Supports direct MP4 video URLs (Pexels, Cloudinary, Supabase, CDN) and local video file uploads with automatic duration calculation.
- **GitHub API 1-Click Commit**: Direct integration to commit updated `data/reels.json` to the GitHub repository.

### 2. User Mobile Application (`index.html`)
- **Curated Home Grid**: 2-column rounded cards with center frosted glass play button matching the reference design.
- **9:16 Vertical Continuous Reels Feed**: Snap-scrolling feed with tap-to-pause and double-tap heart burst animation.
- **Nature Soundscape Engine**: Procedural ambient audio synthesizer tailored to active nature categories.
- **High-Contrast Save & Collections**: Organized by Saved, Liked, and Downloaded with live count badges.
- **100% Client-Side Privacy**: Zero personal data collection, no account required, works offline.

---

## 🏗️ Project Architecture

```
├── admin.html               # Studio Admin Portal website
├── index.html               # Main mobile app interface
├── NatureMoments.apk        # Compiled Android application package
├── css/
│   ├── admin.css            # Admin Portal luxury glassmorphic styles
│   ├── home-screen.css      # Soft cream & modern cards design
│   ├── reels-feed.css       # 9:16 vertical feed layout
│   ├── player.css           # Fullscreen video player overlay
│   ├── components.css       # Glass drawer, tabs, buttons, modals
│   └── design-tokens.css    # Harmonious color tokens & fonts
├── js/
│   ├── admin.js             # Admin Portal logic & sync controller
│   ├── app.js               # Application bootstrap & navigation
│   ├── bundle.js            # Bundled production script
│   ├── data/
│   │   ├── categories.js    # Categories data & dynamic loader
│   │   └── reels.js         # Curated reels & real-time sync engine
│   ├── components/
│   │   ├── reels-feed.js    # Continuous vertical reel feed
│   │   ├── reels-grid.js    # Home 2-column video grid
│   │   ├── video-player.js  # Dedicated video player
│   │   ├── save-screen.js   # Bookmarks, liked & downloaded tab
│   │   └── side-drawer.js   # Glassmorphism navigation drawer
│   └── services/
│       ├── sound-engine.js  # Ambient audio synthesizer
│       ├── downloader.js    # Offline video download manager
│       ├── storage.js       # IndexedDB & localStorage wrapper
│       └── i18n.js          # Multi-language support (10 languages)
├── data/
│   ├── categories.json      # Master categories dataset
│   └── reels.json           # Master authentic nature reels
├── assets/                  # High-res logos, icons, and nature videos
└── android/                 # Android Native Gradle project
```

---

## 🚀 Running Locally

```bash
# Start local development server
python3 -m http.server 4173

# Open App
http://localhost:4173/

# Open Admin Studio
http://localhost:4173/admin.html
```

---

## 📱 Compiling Android APK

```bash
cd android
./gradlew assembleDebug
```
The output APK is generated at `android/app/build/outputs/apk/debug/app-debug.apk` and copied to `NatureMoments.apk`.

---

© 2026 Nature Moments. All rights reserved.
