# Avalokan — Nepal Heritage Explorer

Avalokan is a native Android app for discovering Nepal's heritage: stories, places, and local events.
Built with Jetpack Compose and Material 3. The UI layer is complete; the data layer
(Room + Firestore wiring) is staged but not yet connected — screens currently render
curated hardcoded content.

## Screens

| Screen | Route | Notes |
|---|---|---|
| Home | `home` | Greeting header, Story-of-the-Day hero, Historical Gems rail, events promo, Recent Discoveries feed |
| Discover | `discover` | Search field, category chips, heritage site cards with save overlay |
| Events | `events` | Featured festival hero, upcoming-events list |
| Profile | `profile` | Avatar, visited/saved/events stats, place collection, registrations, gear → Settings |
| Settings | `settings` | Account + Support groups, logout, version footer |
| Story / Place / Event detail | `story_detail/{storyId}`, `place_detail/{placeId}`, `event_detail/{eventId}` | Editorial, guide, and experience layouts; bottom bar auto-hides |

Navigation is centralized in `ui/navigation/` (`NavDestination` sealed interface + `AvalokanNavHost`).
Detail routes already carry String IDs (`kathmandu-durbar`, `indra-jatra-2024`, …) so cards
navigate today; content resolves per-ID once the data layer lands.

## Tech stack

- **UI:** Jetpack Compose (BOM 2026.02.01), Material 3, Navigation Compose 2.10.1
- **DI:** Hilt 2.60.1 with KSP codegen (`@HiltAndroidApp` present; modules pending)
- **Local:** Room 3 (`AvalokanDatabase`, `PlaceItem`/`EventItem` entities + DAOs defined, not yet consumed)
- **Cloud:** Firebase BOM — Analytics + Firestore (`google-services.json` present, Firestore database expected with `places` / `events` collections keyed by the nav IDs above)
- **Language/tooling:** Kotlin 2.2.10, AGP 9.4.1, `minSdk 24 / targetSdk 37`, Java 11
- **Design system:** `ui/theme/` — Himalayan Fresh palette (Teal `#00796B`, Marigold `#FF6F00`…)
  with bundled Montserrat (headings) + Inter (body) fonts, editorial shapes, 8dp spacing grid

## Getting started

1. **Prerequisites:** Android Studio (Quail+), JDK 17+.
2. **Firebase config:** `app/google-services.json` is included. To use your own project, replace it
   with the file from Firebase console → Project settings, and create a Firestore database
   (region `asia-south1` recommended) with `places` and `events` collections.
3. **Build & run:**
   ```bash
   ./gradlew :app:assembleDebug
   ```
   Installs on any emulator/device running API 24+. First launch starts on Home.

## Project structure

```
app/src/main/java/com/example/avalokan/
├── MainActivity.kt            # enableEdgeToEdge + AvalokanTheme entry
├── AvalokanApp.kt             # Scaffold, floating bottom bar, NavController
├── AvalokanApplication.kt     # @HiltAndroidApp
├── data/
│   ├── AppContainer.kt        # legacy manual DI (to be retired in favor of Hilt)
│   ├── AvalokanDatabase.kt    # Room holder (defined, unwired)
│   ├── place/ / event/        # entities + DAOs (defined, unwired)
├── ui/
│   ├── home/ discover/ events/ profile/ settings/   # tab screens
│   ├── detail/                # StoryDetail, PlaceDetail, EventDetail + shared components
│   ├── components/            # shared HeritageCard (Home + Discover)
│   ├── navigation/            # NavDestination, NavGraph
│   └── theme/                 # Color, Type, Shape, Spacing, Theme
```

## Roadmap

- [x] UI layer: 5 tabs + 3 detail screens, theme, bottom bar, insets
- [x] Navigation: tabs, detail args, Settings flow
- [x] Imports: Hilt, Firebase SDK, Room compiler
- [ ] Hilt modules + repository implementations (Firestore ↔ Room)
- [ ] ViewModels (`@HiltViewModel`) replacing hardcoded content
- [ ] Real imagery via Data layer (placeholders sized and ready)
- [ ] Save/bookmark persistence, auth, Firestore security rules hardening
