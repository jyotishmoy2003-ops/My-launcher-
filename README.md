# MyLauncher

A minimal Android home-screen launcher built with Kotlin + Jetpack Compose.

## Features
- Shows all installed apps in an A–Z grid
- Search bar to filter apps
- Tap to open, long-press for App info / Uninstall
- Auto-refreshes when apps are installed or removed
- Shows your wallpaper behind a dark scrim

## Run it
1. Open this folder in **Android Studio** (Ladybug or newer) and let Gradle sync.
2. Run on a phone or emulator (Android 8.0+).
3. Press the Home button, then choose **My Launcher** and tap **Always**.
   To switch back: Settings > Apps > Default apps > Home app.

## Where to customize
- `MainActivity.kt` – all UI and logic (grid size, colors, search)
- `res/values/themes.xml` – wallpaper / system bar styling
- `AndroidManifest.xml` – the HOME intent-filter that makes it a launcher
- `app/build.gradle.kts` – package name (`applicationId`), SDK versions

## Ideas to add next
Favorites dock, hide apps, icon packs, swipe gestures, widgets (`AppWidgetHost`), settings screen.
