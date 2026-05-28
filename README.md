# To-Do List App (Android)

A clean, modern, and lightweight To-Do List application built for Android. This project demonstrates how to implement local data persistence using the Room database, manage UI efficiently with ViewBinding, and handle background threads safely via Kotlin Coroutines.

---

## Features

* **Add Tasks:** Quickly add new entries via a clean input field.
* **Persistent Storage:** Tasks are saved in a local SQLite database using **Room**, meaning your data is safe even after closing or restarting the app.
* **Complete/Incomplete States:** Fully functional checkboxes that update the task status in real-time.
* **Delete Tasks:** A dedicated button on each item to permanently remove it from the list.
* **Optimized List Updates:** Uses `ListAdapter` and `DiffUtil` for smooth animations and efficient UI rendering.

---

## Tech Stack & Architecture

* **Language:** Kotlin
* **UI Layout:** XML (ConstraintLayout, RecyclerView, CardView)
* **ViewBinding:** For safe and clean binding of UI components without `findViewById`.
* **Room Database:** Modern abstraction layer over SQLite for offline data storage.
* **Kotlin Coroutines:** Asynchronous handling of database transactions (Insert, Update, Delete) on background threads (`Dispatchers.IO`) to keep the UI smooth.

---

## Project Structure

```text
com.example.todolist
│
├── todo/                  # Database Layer
│    ├── Task.kt           # Data Entity (Room Table)
│    ├── TaskDao.kt        # Data Access Object (SQL Queries)
│    └── TaskDatabase.kt   # Room Database Client (Singleton)
│
├── MainActivity.kt        # UI Controller & Coroutine Scopes
└── TaskAdapter.kt         # Custom RecyclerView Adapter (ListAdapter)
