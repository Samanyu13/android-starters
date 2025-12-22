# RecyclerView Double API

**RecyclerViewDoubleAPI** is a sample Android app demonstrating how to:

- Fetch data from **two different web APIs**
- Merge and display the results in a single **RecyclerView**
- Handle network requests using Kotlin and Android libraries
- Efficiently bind data using a RecyclerView Adapter

This project is part of the **Android Starters** collection.

---

## 📌 Features

- 📡 **Multiple API Requests**  
  Fetch data from two endpoints and combine results.

- 📝 **Data Display**  
  Display the fetched items in a RecyclerView with ViewHolder.

- 🔄 **Modern Android Architecture**  
  Uses Retrofit / OkHttp / Gson (or similar) for networking.

- 📱 **Clean UI**  
  Simple and responsive user interface.

---

## 🧠 Tech Stack

- ⚙️ Kotlin
- 🧩 Android Jetpack
- 📍 RecyclerView
- 🌐 Retrofit + OkHttp (for API calls)
- 🗂 Gson / Moshi (for JSON parsing)
- 📦 Gradle

---

## 🛠 Setup

### Requirements

- Android Studio Flamingo or newer
- Minimum SDK: API 21+
- Internet access

### Installation

1. Clone the repository:

```bash
git clone https://github.com/Samanyu13/android-starters.git
```
2. Open in Android Studio.
3. Let Gradle sync.
4. Run on an emulator or device.

## App Flow

1. App starts and displays a loading indicator.
2. Two API requests are made in parallel or in sequence.
3. The combined data list is shown in a RecyclerView.
4. Clicking an item (if implemented) shows details.