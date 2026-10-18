# CB Widgets

Android home-screen widget collection with a skeuomorphic design language.
Kotlin, Jetpack Compose, Hilt, DataStore. minSdk 26.

> Status: Phase 4A. Real home-screen widgets: Digital, Minimal and Analog Clock, Date Card, Battery Ring and Battery Bar.

## Build the APK with GitHub Actions
1. Push to `main`.
2. Open the **Actions** tab, select the latest "Build debug APK" run.
3. Download the `cb-widgets-debug-apk` artifact, unzip it and install the `.apk` on your phone
   (allow "Install unknown apps" for your browser/file manager).

## Build locally
Requires JDK 17 and the Android SDK (Android Studio recommended).
Run `gradle wrapper` once, then `./gradlew assembleDebug`.

## Secrets
Never commit `google-services.json`, keystores, `keystore.properties` or API keys.
They are git-ignored and are supplied through GitHub Actions secrets in later phases.
