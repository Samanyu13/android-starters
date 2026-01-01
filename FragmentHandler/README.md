# Fragment Handler - Multi-Fragment Sync

### 🌟 Project Concept
This application is a proof-of-concept for **Shared State Management** in Android. It replaces the old, clunky method of passing data manually between screens with a "single source of truth" architecture.

### 🏗️ How it Works
The app uses a **Single-Activity Architecture** with three interconnected layers:

* **The Shared Hub:** A centralized ViewModel that holds a single piece of text. Because it is owned by the Activity, it stays alive even when Fragments are swapped or destroyed.
* **The Smart Stream:** Using StateFlow, the UI "listens" for changes. If the text is updated on one screen, all other screens are automatically updated to match.
* **The History Chain:** Each navigation step is added to a "Back Stack." This allows the user to navigate backward through the screens naturally without exiting the app.

### 📱 User Flow
1.  **Fragment One:** User inputs text.
2.  **Fragment Two:** The text is pre-filled from the previous screen; the user can modify it.
3.  **Fragment Three:** Displays the final version of the text.

### 💎 Key Benefits
* **Zero Data Loss:** Text remains consistent even when navigating back and forth.
* **Lifecycle Safe:** The app intelligently stops tracking data when the screen is off, saving battery and memory.
* **Scalable:** Adding a 4th or 5th fragment is as simple as "plugging in" to the existing shared hub.