# MindFlow 📝

MindFlow is a modern, premium, and feature-rich Android notes management application built from the ground up using **Kotlin** and **Jetpack Compose**. Designed with elegant Material 3 components and sleek transitions, MindFlow keeps your thoughts organized while offering daily doses of inspiration.

---

## ✨ Features

- **💡 Dynamic Daily Inspiration**: Features a beautiful motivation quote banner fetched from a remote API. Includes a failsafe local cache of high-quality fallback quotes to ensure premium presentation even when offline.
- **🎨 Custom Note Styling**: Create and organize notes with vibrant, custom-colored backgrounds.
- **🏷️ Category Organization**: Organize your thoughts with tags like *Work*, *Personal*, *Ideas*, and *Todo*. Use interactive filter chips to view notes by category instantly.
- **🔍 Real-Time Search**: Instantly filter through your note titles and content as you type in the search bar.
- **🛠️ Full CRUD Operations**: Create, read, update, and delete notes with smooth confirmation dialogs and zero-latency database synchronization.
- **⚡ Seamless Offline Sync**: Notes are safely persisted locally using the Room database.

---

## 🛠️ Architecture & Tech Stack

The application is built using modern Android development best practices:

- **UI Framework**: [Jetpack Compose](https://developer.android.com/compose) (Material 3 design system)
- **Local Database**: [Room Database](https://developer.android.com/training/data-storage/room) for reactive offline-first storage
- **Asynchronous Flow**: Kotlin [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [StateFlow](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-state-flow/) for real-time UI reactive updates
- **Network Layer**: Standard HTTP requests for fetching remote quotes securely
- **Design Pattern**: MVVM (Model-View-ViewModel) with structured unidirectional data flow (UDF)

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Jellyfish 2023.3.1 or newer recommended)
- **Android SDK** 34+
- **JDK 17** or newer

### Setup

1. Clone or download this repository:
   ```bash
   git clone https://github.com/Abdurehman95/Daily-note-app.git
   ```
2. Open the project folder in **Android Studio**.
3. Let Gradle sync and resolve all dependencies.
4. Run the app on an Android Emulator or a physical device!

### ✔️Team Members 
   1. Yonas yirgu
   2. Hailemeskel Getaneh
   3. Abdurehman Seid
   4. Dagnachew Getahun
   5. Halid Faruk
