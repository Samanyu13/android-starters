# Android User Registration & Auth (Firebase)

### 🌟 Project Concept
A modern Android application demonstrating a robust authentication flow using Jetpack Compose, Credential Manager (Google One Tap), and Firebase Authentication.

### 🏗️ Features
- **Google One Tap Sign-In:** Uses the latest androidx.credentials API for a seamless, bottom-sheet login experience.
- **Email/Password Auth:** Standard registration and login flow with Firebase.
- **Reactive Navigation:** State-driven navigation using LaunchedEffect and StateFlow.
- **MVVM Architecture:** Clean separation of concerns between the UI and Firebase logic.
- **Secure Secret Management:** Uses local.properties and BuildConfig to keep API keys out of source control.
- **Image Loading:** Profile pictures handled via Coil.

### 🛠️ Setup & Installation
To protect sensitive information, the `local.properties` and `google-services.json` files are not included in this repository. Follow these steps to run the app:

1. Firebase Configuration
    - Create a project in the Firebase Console.
    - Add an Android App to your project using the package name `com.example.userregistration`.
    - Download the `google-services.json` and place it in the `app/` directory.
    - Enable Email/Password and Google sign-in providers in the Firebase Authentication tab.

2. Google Web Client ID
    - In the Firebase Console, go to Authentication > Sign-in method > Google.
    - Under Web SDK configuration, copy the Web client ID.
    - Open your local.properties file (in the project root) and add the following line:
```
GOOGLE_WEB_CLIENT_ID=your_actual_client_id_here.apps.googleusercontent.com
```

3. SHA-1 Fingerprint
    - For Google Sign-In to work on your local machine/emulator:
    - Generate your SHA-1 fingerprint (run ./gradlew signingReport in the terminal).
    - Add the SHA-1 to your Android App settings in the Firebase Console.

### 🏗️ Architecture
The app follows the official Android architecture recommendations:
- UI: Jetpack Compose (Material 3)
- Navigation: Compose Navigation Component
- ViewModel: Logic for Firebase interaction and state management.
- Dependency Management: Version Catalogs (libs.versions.toml)

### 📦 Dependencies
- `androidx.credentials`: For Google Sign-in.
- `com.google.firebase:firebase-auth-ktx`: For user management.
- `io.coil-kt:coil-compose`: For profile image rendering.