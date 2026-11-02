# Avalokan

A native Android app for discovering Nepal's heritage — historical places, cultural stories, and local events in one place.

Avalokan brings together the places worth visiting, the stories behind them, and the festivals happening around you. It is built with Kotlin and Jetpack Compose, uses Firebase for content and accounts, and keeps a local cache so the app stays useful when the network does not.

## Screenshots

| Home | Discover |
| :---: | :---: |
| <img src="docs/screenshots/home.png" width="300" alt="Home screen with Story of the Day and Historical Gems" /> | <img src="docs/screenshots/discover.png" width="300" alt="Discover screen with search, category filters, and place cards" /> |
| **Story of the Day**, historical places, and event discovery | Search, category filters, place cards, and save toggles |

| Place details | Events |
| :---: | :---: |
| <img src="docs/screenshots/place-detail.png" width="300" alt="Place detail screen with entry fee, hours, rating, and saved state" /> | <img src="docs/screenshots/events.png" width="300" alt="Events screen with a featured festival and upcoming events" /> |
| Facts, description, directions, sharing, and saving | Featured festival and upcoming events with organizer links |

## About

Nepal has centuries of history spread across its cities, temples, and living traditions. The information is out there, but it is usually scattered across travel sites, social posts, and local knowledge.

Avalokan puts that experience in one place: browse heritage sites, read editorial stories, find events, and keep a personal collection of the places you want to visit. An account is optional — the app works without signing in, and an anonymous session is created automatically so content can load.

## Features

- **Explore heritage places** — browse historical, cultural, and spiritual sites with descriptions, entry fees, hours, ratings, and practical details.
- **Search and filter** — find places by name and narrow the list by category.
- **Read stories** — a featured Story of the Day and full editorial story screens.
- **Find events** — a featured festival, an upcoming events list, and a Book Now action that opens the organizer's link when one is available.
- **Save for later** — bookmark places and favorite events locally, with or without an account.
- **Optional account** — continue anonymously, or sign in with email/password or Google to sync your display name and bio.
- **Profile** — display name, bio, a photo picked from your device, and your saved places and favorite events.
- **Settings** — light, dark, or system theme; clear cached content; reset local data; sign out.
- **Offline-friendly** — Firestore content is cached in Room and the last good copy stays on screen when a network request fails.
- **Clear screen states** — loading, empty, and error views with retry actions on the main list screens.

## Technology

| Area | Tools |
| --- | --- |
| Language | Kotlin, coroutines and Flow |
| UI | Jetpack Compose, Material 3, Navigation Compose |
| Architecture | MVVM, repository interfaces, Hilt dependency injection |
| Local storage | Room, DataStore |
| Backend | Firebase Authentication, Cloud Firestore, Firebase Analytics |
| Sign-in | Credential Manager and Google Identity |
| Images | Coil |
| Testing | JUnit, Turbine, coroutines-test, AndroidX Test, Compose UI Test |

Avalokan supports Android 7.0 (API 24) and above.

## Architecture

The app is split into three layers: `ui/` for Compose screens and ViewModels, `domain/` for the models and repository interfaces the UI depends on, and `data/` for Firebase and Room implementations. ViewModels never see Room entities or Firestore documents.

Content is read from Cloud Firestore and cached in Room. The UI observes Room, so screens stay responsive and readable offline; Firestore updates are written into the cache in the background. Hilt wires the layers together, and local preferences such as theme and saved IDs live in DataStore.

## Getting started

### Requirements

- A recent version of Android Studio with its bundled JDK
- An Android device or emulator running Android 7.0 (API 24) or higher
- A Firebase project for authentication and content

### Set up Firebase

1. Create a Firebase project and add an Android app with the package name `com.example.avalokan`.
2. Enable **Anonymous**, **Email/Password**, and **Google** sign-in.
3. Create a Cloud Firestore database with `places`, `events`, and `stories` collections. Profile data uses a `users` collection.
4. Download `google-services.json` and place it in `app/`.
5. Copy `secrets.properties.example` to `secrets.properties` and set `GOOGLE_SERVER_CLIENT_ID` to your Firebase web client ID. Both files are kept out of version control.

### Run the app

Open the project in Android Studio and run it on a device or emulator. From the command line:

```bash
git clone https://github.com/SamratVsn/Avalokan.git
cd Avalokan
./gradlew :app:assembleDebug
```

### Run the tests

```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedDebugAndroidTest   # requires a connected device or emulator
```

## Project status

Avalokan is a **working beta**. Browsing, search, stories, events, saving, profiles, and authentication work end to end. It is not production-ready and is not published on Google Play.

Some parts are intentionally simple for now: the Book Now action opens the organizer's link when one is available rather than processing a booking in the app, notifications and the place gallery are placeholders, saved content and avatars stay on the device, and there is no cross-device sync for collections.

## Contributing

Contributions and suggestions are welcome, especially around accessibility, Android architecture, and heritage content. Fork the repository, create a branch, make your changes, run the tests, and open a pull request describing what changed.

## Credits

Screenshots were captured from the running app.

## Author

**Samrat Parajuli**

[Portfolio](https://samratparajuli0.com.np) · [GitHub](https://github.com/SamratVsn) · [LinkedIn](https://www.linkedin.com/in/samratvsn/)

## License

This repository does not currently include an open-source license.
