# FCM Store Alert App ⚡

A simple Android application built with **Jetpack Compose** and **Clean Architecture (MVVM)** to demonstrate the implementation of **Firebase Cloud Messaging (FCM)**. This project simulates a retail app that receives "Flash Sale" notifications.

---

## 🚀 Features
- **Real-time Notifications:** Receive push notifications from Firebase.
- **Background & Foreground Support:** Handles messages whether the app is open or closed.
- **Notification Channels:** Implements a specific "Store Alerts" channel.
- **Android 13+ Ready:** Includes runtime permission handling for `POST_NOTIFICATIONS`.
- **Clean Architecture (MVVM):** Separated concerns into Data, UI State, and UI layers.
- **Reactive UI:** Uses `StateFlow` and Coroutines for seamless state updates.
- **Topic Subscription:** Subscribes to the `deals` topic for mass broadcasting.

---

## 🛠️ Setup Instructions (The "FCM Thing")

To get this project running, you need to connect it to your own Firebase project.

### 1. Firebase Console Configuration
1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  **Create a Project:** Click "Add Project" and name it `FCM Store App`.
3.  **Register Android App:**
    *   Click the Android icon.
    *   **Package Name:** `com.happyminds.fcmtestapp` (Must match `namespace` in `app/build.gradle.kts`).
    *   Click "Register App".
4.  **Download Config:** Download the `google-services.json` file.
5.  **Placement:** Move the `google-services.json` file into the `/app/` directory of this project.

### 2. Implementation Details
- **Gradle:** Added Firebase BOM, Messaging, and Coroutine Play Services support.
- **Architecture:** Refactored into **MVVM**:
    - **Data Layer:** `NotificationRepository` handles all Firebase-specific logic using Coroutines.
    - **UI Layer (ViewModel):** `StoreViewModel` manages the UI state and handles business logic.
    - **UI Layer (Activity/Compose):** `MainActivity` observes the state and renders the UI.
- **Service:** `MyFirebaseMessagingService.kt` handles incoming messages and system notifications.

---

## 📲 How to Test

### Option A: Send to a Single Device (Individual)
1.  **Build and Run:** Run the app on a physical device or emulator.
2.  **Get your Token:**
    *   Open the **Logcat** tab in Android Studio and filter for `FCM`.
    *   Look for: `FCM Registration Token: [LONG_STRING_HERE]`.
3.  **Send Test:** In Firebase Console (Engage > Messaging), click **Send test message**, paste the token, and click **Test**.

### Option B: Broadcast to Everyone (Topics)
1.  **Target:** In Firebase Console (Engage > Messaging), create a new notification.
2.  **Select Topic:** In the **Target** section, select **Topic** and choose `deals`.
3.  **Publish:** Click **Review** and **Publish**. Every device with the app will receive the notification!

---

## 📂 Project Structure
- `data/NotificationRepository.kt`: Clean interface for Firebase Messaging (Coroutines based).
- `ui/StoreViewModel.kt`: Manages UI state (`StoreUiState`) and coordinates between data and UI.
- `MainActivity.kt`: Entry point, permission handling, and Compose UI hosting.
- `MyFirebaseMessagingService.kt`: Background service for handling incoming FCM payloads.
- `AndroidManifest.xml`: Standard Android manifest with Service and Permission declarations.
