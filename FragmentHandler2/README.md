# Multi-Fragment Sync (Navigation Component Edition)

### 🌟 Project Concept
This version of the project uses the **Jetpack Navigation Component**. It separates the logic of "where to go" from the logic of "how to get there."

### 🏗️ How it Works
* **NavGraph:** A centralized XML file that defines every screen and every possible path between them.
* **NavHostFragment:** A specialized container in the Activity that swaps fragments based on the NavGraph.
* **NavController:** The "steering wheel" used by fragments to trigger transitions.
* **Shared ViewModel:** Remains the central hub for data, ensuring text stays synced across the NavHost.

### 💎 Key Benefits
* **No Manual Transactions:** No more `beginTransaction()` or `commit()` boilerplate.
* **Standardized Back Behavior:** Follows Android's design guidelines for back-navigation out of the box.
* **Deep Linking Ready:** This architecture makes it much easier to support deep links later.