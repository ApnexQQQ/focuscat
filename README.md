# 🐱 FocusCat

An Android app that helps you stay focused by blocking distracting apps with a cute cat overlay and a 5-second delay.

## What It Does

When you try to open Instagram, TikTok, Facebook, Twitter, Snapchat, or Reddit:
1. A full-screen cat overlay appears 🐱
2. You must wait 5 seconds before continuing
3. You can choose to go back to the home screen instead

## Blocked Apps

- Instagram
- TikTok
- Facebook
- Twitter
- Snapchat
- Reddit

## Requirements

- Android 8.0+ (API 26)
- Overlay permission ("Display over other apps")
- Accessibility Service enabled

## How to Build

### Option 1: Android Studio
1. Open this project in Android Studio
2. Build → Build Bundle(s) / APK(s) → Build APK
3. Find the APK in `app/build/outputs/apk/debug/`

### Option 2: Command Line
```bash
./gradlew assembleDebug
```

## How to Install

1. Transfer the APK to your phone
2. Tap the APK file on your phone
3. Allow "Install from unknown sources" if prompted
4. Open the app and follow the setup steps

## Setup Steps

1. **Enable Overlay Permission** — Allows the app to draw over other apps
2. **Enable Accessibility Service** — Allows the app to detect when you open blocked apps
3. **Start FocusCat** — The app is now protecting you from distractions!

## License

MIT License — do whatever you want with it!