# UserDisplay

A lightweight Android application that demonstrates how to fetch user information from a remote API
and display it using a RecyclerView.

This project is part of the **Android Starters** collection.

---

## 📌 Features

- **Remote Data Fetching**  
  Retrieves a list of users (Names, Emails, and Avatars) from a JSON API.

- **Dynamic List**  
  Uses RecyclerView with a custom adapter for smooth scrolling.

- **Image Loading**  
  Utilizes Glide for asynchronous image downloading and caching.

- **Retrofit Networking**  
  Implements Type-safe HTTP requests.

- **Modern Layout**  
  Uses CardView and ConstraintLayout for a clean UI design.

---

## 🧠 Tech Stack

- Language: Kotlin
- Networking: Retrofit 2 & Gson Converter
- Image Handling: Glide
- UI Components: RecyclerView, CardView, Material Design
- API: ReqRes.in (Standard placeholder API for user data)
- Gradle

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

2. Open in Android Studio and navigate to the UserDisplay folder and open it as an existing
   project.
3. Let Gradle sync.
4. Run on an emulator or device.

## Implementation Details

1. Data Class: A User model captures the first_name, last_name, email, and avatar fields from the
   API.
2. Retrofit: The ApiService defines a @GET request to the /users endpoint.
3. Adapter: The UserAdapter binds the user data to the views and uses Glide to load the profile
   picture into an ImageView.