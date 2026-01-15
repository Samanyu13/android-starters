# Offline-First Data Syncer SDK
A lightweight, modern Android SDK built with **Jetpack Compose**, **Room**, and **WorkManager**. This project demonstrates an "Offline-First" architecture where data is captured, stored locally, and synchronized with a background worker when internet connectivity is available.

---

## 🚀 Features

* **Jetpack Compose UI**: A fully reactive, single-module UI for data entry.
* **Offline-First**: Uses **Room Database** to ensure zero data loss during network outages.
* **Background Sync**: Powered by **WorkManager** with network constraints (syncs only when online).
* **Reactive Data Flow**: Uses **Kotlin Coroutines Flow** to update the main app UI in real-time.
* **Modern Image Handling**: Integrated with **Coil** for image loading and the **Android Photo Picker**.

---

## 🛠 Architecture

The project is split into two modules:
1.  **:app**: The consumer application that integrates the SDK and displays sync history.
2.  **:form-sdk**: The library containing the UI, Database logic, and Sync Worker.

---

## 🔄 How Syncing Works
- **Local Persistence:** When the user clicks "Save", the data is immediately written to the Room DB with an isSynced = false flag.
- **Job Scheduling:** FormSDK.scheduleSync() enqueues a OneTimeWorkRequest.
- **Constraint Handling:** The system waits until NetworkType.CONNECTED is satisfied.
- **Worker Execution:** The SyncWorker fetches all unsynced rows, uploads them (simulated), and updates the local flag to isSynced = true.
- **UI Update:** Since the Main App observes a Flow, the UI updates to show a ✅ automatically.
