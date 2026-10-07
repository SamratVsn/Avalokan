<div align="center">

# 🏔️ Avalokan

### Discover Nepal. Explore its heritage. Experience its stories.

A native Android application for exploring Nepal's **historical places, cultural heritage, religious sites, and local events** — bringing the stories behind the places closer to everyone.

Built with **Kotlin, Jetpack Compose, and Material 3**.

<br/>

[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.10-7F52FF?style=flat-square\&logo=kotlin\&logoColor=white)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?style=flat-square\&logo=jetpackcompose\&logoColor=white)](https://developer.android.com/compose)
[![Material 3](https://img.shields.io/badge/Material%203-Design-6750A4?style=flat-square)](https://m3.material.io/)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square\&logo=android\&logoColor=white)]

<br/>

**Project Status:** UI & navigation implemented · Data layer in progress

</div>

---

## 🌄 About the Project

Nepal is home to centuries of history, living traditions, sacred places, architectural wonders, and festivals that bring communities together. Yet discovering the stories behind these places and finding events happening nearby can mean searching across many different sources.

**Avalokan aims to bring these experiences together in one place.**

The application is being designed as a heritage explorer where people can discover noteworthy destinations, learn their stories, explore cultural and religious landmarks, and find local events.

The current implementation focuses on the user interface, navigation, and design system. Screens use curated sample content while the Room and Firebase/Firestore data integrations are being developed.

### ✨ Core Experience

* 🏛️ **Explore Heritage** — Discover historical landmarks, cultural destinations, and religious sites.
* 📖 **Discover Stories** — Explore editorial-style stories and featured heritage highlights.
* 🎉 **Find Local Events** — Browse festivals, celebrations, and upcoming community events.
* 🔎 **Discover Places** — Browse destination cards and explore categories.
* 🔖 **Save & Collect** — Planned bookmarking and personalized place collections.
* 👤 **Your Profile** — A dedicated space for saved places, visits, event activity, and settings.

---

## 📱 Screens & Navigation

| Screen            | Experience                                                                                        |
| ----------------- | ------------------------------------------------------------------------------------------------- |
| **Home**          | Personalized greeting, Story of the Day, Historical Gems, event promotion, and recent discoveries |
| **Discover**      | Search, category filters, heritage cards, and save controls                                       |
| **Events**        | Featured festival and upcoming events                                                             |
| **Profile**       | Profile overview, activity statistics, collections, registrations, and settings access            |
| **Settings**      | Account and support options, logout, and app version                                              |
| **Story Details** | Editorial layouts for heritage stories                                                            |
| **Place Details** | Destination-focused information and guide layouts                                                 |
| **Event Details** | Event and festival experience layouts                                                             |

Navigation is centralized in `ui/navigation/` through a sealed `NavDestination` interface and `AvalokanNavHost`. Detail routes already accept string IDs, allowing screens to navigate using stable identifiers while the data integration is developed.

> **Note:** These screens currently render curated hardcoded content. Data-driven rendering, persistence, and live Firestore integration are still in progress.

### Screenshots

Add current screenshots here as the UI stabilizes. Showing the actual app is more useful than using generic travel imagery.

<!--
Suggested layout:
| Home | Discover | Events |
|------|----------|--------|
| ![Home](docs/screenshots/home.png) | ![Discover](docs/screenshots/discover.png) | ![Events](docs/screenshots/events.png) |

| Place Details | Profile | Settings |
|---------------|---------|----------|
| ![Place Details](docs/screenshots/place-detail.png) | ![Profile](docs/screenshots/profile.png) | ![Settings](docs/screenshots/settings.png) |
-->

---

## 🎨 Design System

Avalokan uses a custom visual language inspired by Nepal's landscapes, heritage, and cultural warmth.

| Element              | Implementation                                                             |
| -------------------- | -------------------------------------------------------------------------- |
| **Visual direction** | Editorial layouts with heritage-inspired presentation                      |
| **Color palette**    | Himalayan Fresh — teal `#00796B`, marigold `#FF6F00`, and supporting tones |
| **Typography**       | Montserrat for headings, Inter for body text                               |
| **UI framework**     | Jetpack Compose + Material 3                                               |
| **Layout system**    | 8dp spacing grid, reusable components, and consistent shapes               |
| **Navigation**       | Centralized navigation with a floating bottom bar                          |
| **Window handling**  | Edge-to-edge layouts and appropriate system-bar insets                     |

The goal is a modern, readable interface that makes heritage content feel engaging without overwhelming the information itself.

---

## 🧱 Architecture & Technology

Avalokan is being developed with a modern Android stack, with a focus on separating the UI from data access and keeping the application maintainable as it grows.

### Technology Stack

| Layer                    | Technologies                                  |
| ------------------------ | --------------------------------------------- |
| **Language**             | Kotlin 2.2.10                                 |
| **UI**                   | Jetpack Compose, Material 3                   |
| **Navigation**           | Navigation Compose 2.10.1                     |
| **Dependency Injection** | Hilt 2.60.1, KSP                              |
| **Local Database**       | Room 3                                        |
| **Cloud Data**           | Firebase Firestore                            |
| **Analytics**            | Firebase Analytics                            |
| **Build**                | Android Gradle Plugin 9.4.1, Java 11          |
| **Design System**        | Custom theme, typography, shapes, and spacing |

### Current Architecture

```text
┌─────────────────────────────────────┐
│             UI Layer                │
│  Home · Discover · Events · Profile │
│  Settings · Story/Place/Event Detail│
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│        Navigation & UI State        │
│   NavDestination · AvalokanNavHost  │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│       Data Layer — In Progress      │
│  Repositories · ViewModels · Hilt   │
│                                     │
│   Room (local) ↔ Firestore (cloud)  │
└─────────────────────────────────────┘
```

The data layer shown above is the **planned architecture**, not a claim that synchronization or repository-backed UI is already implemented.

### Data Layer Plan

* **Room:** Local persistence using `AvalokanDatabase`, `PlaceItem`, `EventItem`, and their DAOs.
* **Firestore:** Cloud-backed heritage places and events, using stable document IDs aligned with navigation identifiers.
* **Repositories:** A consistent access layer between the UI and local/cloud data sources.
* **Hilt:** Dependency injection for repositories, database access, and ViewModels.
* **ViewModels:** UI state management to replace hardcoded screen content.

The intended architecture will allow the UI to consume data through repositories rather than depending directly on Firestore or database implementations.

---

## 📂 Project Structure

```text
app/src/main/java/com/example/avalokan/
├── MainActivity.kt
├── AvalokanApp.kt
├── AvalokanApplication.kt
│
├── data/
│   ├── AppContainer.kt
│   ├── AvalokanDatabase.kt
│   ├── place/
│   └── event/
│
└── ui/
    ├── home/
    ├── discover/
    ├── events/
    ├── profile/
    ├── settings/
    ├── detail/
    ├── components/
    ├── navigation/
    └── theme/
```

* `MainActivity.kt` — Activity entry point, edge-to-edge setup, and theme initialization.
* `AvalokanApp.kt` — Main scaffold, floating bottom navigation, and navigation host.
* `AvalokanApplication.kt` — Hilt application entry point.
* `data/` — Database and entity/DAO definitions; integration is in progress.
* `ui/` — Feature screens, detail layouts, shared components, navigation, and design system.

---

## 🚀 Getting Started

### Prerequisites

* Android Studio
* JDK 17 or a compatible JDK supported by the project's Gradle setup
* Android device or emulator running Android 7.0 (API 24) or higher

### 1. Clone the repository

```bash
git clone https://github.com/SamratVsn/Avalokan.git
cd Avalokan
```

### 2. Open the project

Open the project in Android Studio and allow Gradle synchronization to complete.

### 3. Configure Firebase if needed

The repository currently includes `app/google-services.json`.

If you use your own Firebase project:

1. Register the Android app with the matching application ID.
2. Download its `google-services.json`.
3. Place the file in the `app/` directory.
4. Configure Firestore if you want to work on the planned cloud integration.

The current sample UI does not yet depend on live Firestore content. A configured Firestore database with the expected collections will be needed when the integration is implemented.

### 4. Build the application

```bash
./gradlew :app:assembleDebug
```

Install the generated debug APK on a compatible emulator or device, or run the application directly from Android Studio.

---

## 🗺️ Roadmap

* [x] Build the main UI screens and shared components
* [x] Implement tab navigation and detail routes
* [x] Establish the custom theme and design system
* [x] Add initial Room entities and DAO definitions
* [x] Add Hilt and Firebase SDK dependencies
* [ ] Configure Hilt modules and dependency injection
* [ ] Implement repository interfaces and data sources
* [ ] Connect Firestore and Room to the application
* [ ] Introduce ViewModels and observable UI state
* [ ] Replace hardcoded content with data-driven screens
* [ ] Implement persistent bookmarks and saved collections
* [ ] Add authentication and account flows
* [ ] Configure and verify Firestore security rules
* [ ] Add real destination imagery and richer heritage content

The roadmap reflects the current development plan; completed infrastructure setup does not necessarily mean the corresponding feature is fully integrated into the UI.

---

## 🤝 Contributing

Avalokan is an evolving project, and suggestions around Android architecture, heritage discovery, UI accessibility, and data modeling are welcome.

If you'd like to contribute:

1. Fork the repository.
2. Create a feature branch.
3. Make your changes.
4. Test the application.
5. Open a pull request describing the changes.

---

## 👨‍💻 Author

**Samrat Parajuli**
Android Developer · Kotlin · Jetpack Compose · Nepal 🇳🇵

[Portfolio](https://samratparajuli0.com.np) · [GitHub](https://github.com/SamratVsn) · [LinkedIn](https://www.linkedin.com/in/samratvsn/)

---

<div align="center">

**Discover Nepal. Preserve its stories. Connect with its heritage.**

*Built with curiosity and a love for Nepal.*

</div>
