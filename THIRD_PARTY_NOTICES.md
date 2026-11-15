# Third-Party Notices

Avalokan's original source code is licensed under the GNU Affero General Public
License, version 3 or later (`AGPL-3.0-or-later`). See [LICENSE](LICENSE).

That licence applies **only** to original Avalokan code. Third-party libraries,
fonts, icons, data, and services included in or used by Avalokan remain under
their own licences and terms. This file records them. It does not relicense any
third-party component, and nothing here claims authorship of third-party work.

The inventory below was compiled from the resolved `releaseRuntimeClasspath`
and the tracked repository contents on 2026-11-15. Dependency versions are those
Gradle actually resolves, which can differ from the versions requested in
`gradle/libs.versions.toml`.

Copies of the AGPL text and both font licences are mirrored into
`app/src/main/assets/licenses/` so they travel with the APK. When a licence text
changes, update the mirrored copy as well.

## Runtime components shipped in the app

These components are packaged into the APK and therefore distributed to users.
Versions are the resolved versions at the time of writing.

| Component | Version | Licence / terms |
| --- | --- | --- |
| Kotlin standard library (`org.jetbrains.kotlin:kotlin-stdlib`) | 2.3.21 | Apache-2.0 |
| Kotlin coroutines (`kotlinx-coroutines-android`, `-core`, `-play-services`) | 1.9.0 | Apache-2.0 |
| AndroidX Core, Core KTX, Core Splashscreen | 1.19.0 / 1.0.1 | Apache-2.0 |
| AndroidX Activity Compose | 1.13.0 | Apache-2.0 |
| AndroidX Lifecycle | 2.11.0 | Apache-2.0 |
| Jetpack Compose (UI, Foundation, Runtime, Animation) | Compose BOM 2026.02.01; UI/Foundation 1.12.1 | Apache-2.0 |
| Compose Material 3 | 1.4.0 | Apache-2.0 |
| Compose Material Icons Extended | 1.7.8 | Apache-2.0 |
| AndroidX Navigation Compose / Runtime | 2.10.1 | Apache-2.0 |
| AndroidX Room 3 (runtime, common) | 3.0.3 | Apache-2.0 |
| AndroidX DataStore Preferences | 1.1.7 | Apache-2.0 |
| AndroidX Hilt Navigation Compose | 1.2.0 | Apache-2.0 |
| Dagger / Hilt | 2.60.1 | Apache-2.0 |
| Coil Compose | 2.6.0 | Apache-2.0 |
| Accompanist Drawable Painter (transitive, via Coil) | 0.32.0 | Apache-2.0 |
| OkHttp / Okio (transitive) | 4.12.0 / 3.8.0 | Apache-2.0 |
| gRPC (transitive) | 1.62.2 | Apache-2.0 |
| Protocol Buffers Java Lite (transitive) | 3.25.5 | BSD-3-Clause |
| Guava (transitive) | 32.1.3-android | Apache-2.0 |
| Gson (transitive) | 2.10.1 | Apache-2.0 |
| Error Prone annotations (transitive) | 2.26.0 | Apache-2.0 |
| JSR-305 annotations (transitive) | 3.0.2 | BSD-3-Clause |
| Checker Framework Qual (transitive) | 3.37.0 | MIT |
| Animal Sniffer Annotations (transitive) | 1.23 | MIT |
| JSpecify (transitive) | 1.0.0 | CC0-1.0 |
| RE2/J (transitive) | 1.6 | BSD-3-Clause |
| PerfMark (transitive) | 0.26.0 | Apache-2.0 |
| Jakarta Dependency Injection API (transitive) | 2.0.1 | Apache-2.0 |
| javax.inject (transitive) | 1 | Apache-2.0 |
| Firebase BOM | 34.19.0 | BOM metadata only; constituent SDK terms apply |
| Firebase Firestore | 26.6.0 | Android Software Development Kit License; Firebase Terms of Service |
| Firebase Auth | 24.2.0 | Android Software Development Kit License; Firebase Terms of Service |
| Google Identity / Google ID (`com.google.android.libraries.identity.googleid`) | 1.1.1 | Android Software Development Kit License |
| Google Play services (`play-services-auth`, `-auth-base`, `-base`, `-basement`, `-fido`, `-tasks`, transitive) | 18.x–21.x | Android Software Development Kit License; Google APIs Terms of Service |
| AndroidX Credentials and Credentials Play Services Auth | 1.3.0 | Apache-2.0 (AndroidX wrapper); bundled Play services components remain proprietary |
| Google Play Core Common / Play Integrity (transitive) | 2.0.3 / 1.3.0 | Play Core Software Development Kit Terms of Service; Play Integrity API Terms of Service |
| Google reCAPTCHA for Android (transitive, via Firebase) | 18.6.1 | Android Software Development Kit License |

Licence identifiers follow the names published in each artefact's metadata or
the project's own documentation. "Android Software Development Kit License" and
the Play/Firebase terms are proprietary Google terms, not open-source licences.

## Build and test tooling (not shipped to users)

| Component | Version | Licence |
| --- | --- | --- |
| Android Gradle Plugin | 9.5.0 | Apache-2.0 |
| Kotlin Gradle plugin / Compose compiler plugin | 2.2.10 | Apache-2.0 |
| KSP (Kotlin Symbol Processing) | 2.3.6 | Apache-2.0 |
| Hilt Gradle plugin | 2.60.1 | Apache-2.0 |
| Google Services Gradle plugin | 4.5.0 | Apache-2.0 |
| Foojay Toolchains Resolver plugin | 1.0.0 | Apache-2.0 |
| Gradle Wrapper | 9.6.0 (per `gradle/wrapper`) | Apache-2.0 |
| JUnit 4 | 4.13.2 | Eclipse Public License 1.0 (EPL-1.0) |
| AndroidX Test Ext JUnit | 1.3.0 | Apache-2.0 |
| AndroidX Espresso | 3.7.0 | Apache-2.0 |
| kotlinx-coroutines-test | 1.10.2 | Apache-2.0 |
| Turbine | 1.2.1 | Apache-2.0 |

JUnit is a test-only dependency. It is not compiled into or distributed with the
released APK.

## Fonts

| Font | Files | Licence | Copyright notice |
| --- | --- | --- | --- |
| Inter | `inter_regular.ttf`, `inter_medium.ttf`, `inter_bold.ttf` | SIL Open Font License 1.1 | Copyright (c) 2016 The Inter Project Authors (https://github.com/rsms/inter) |
| Montserrat | `montserrat_semibold.ttf`, `montserrat_bold.ttf` | SIL Open Font License 1.1 | Copyright 2024 The Montserrat.Git Project Authors (https://github.com/JulietaUla/Montserrat.git) |

The full OFL 1.1 texts, including each copyright line, are bundled at:

- [`licenses/Inter-OFL-1.1.txt`](licenses/Inter-OFL-1.1.txt)
- [`licenses/Montserrat-OFL-1.1.txt`](licenses/Montserrat-OFL-1.1.txt)

The font binaries themselves carry no separate licence file in this repository;
the texts above are the licences published by the upstream font projects for
the same releases. The OFL requires the licence to accompany redistributed font
software, which is why the texts are kept in the repository.

## Icons and images

- **Material icons** used in the UI come from `androidx.compose.material:material-icons-extended`,
  distributed under Apache-2.0. The icon artwork derives from Google's Material
  Symbols/ Material Icons set, also published under Apache-2.0.
- **App launcher icon, adaptive-icon layers, splash artwork, and Play Store
  icon** (`app/src/main/res/mipmap-*`, `drawable-nodpi/avalokansplash.png`,
  `app/src/main/ic_launcher-playstore.png`) are Avalokan-specific artwork. Their
  authorship and any tool-generated provenance are recorded by the project owner;
  see "Items requiring owner confirmation" below.
- **Screenshots** in `docs/screenshots/` are captures of the running app. The
  `docs/` directory is excluded from version control by `.gitignore`.

## Bundled third-party notices

The following notices are reproduced because their licences require attribution
when the corresponding code or data is redistributed.

### Public Suffix List (MPL-2.0)

OkHttp bundles `publicsuffixes.gz`, compiled from the Public Suffix List:

> Note that publicsuffixes.gz is compiled from The Public Suffix List:
> https://publicsuffix.org/list/public_suffix_list.dat
>
> It is subject to the terms of the Mozilla Public License, v. 2.0:
> https://mozilla.org/MPL/2.0/

The MPL-2.0 text is included at [`licenses/MPL-2.0.txt`](licenses/MPL-2.0.txt).
The Public Suffix List data remains under MPL-2.0 and is not relicensed by this
project.

### Eclipse Jakarta Dependency Injection (Apache-2.0)

The packaged `META-INF/NOTICE.md` for Jakarta Dependency Injection states, in
part, that the content is maintained by the Eclipse Jakarta Dependency Injection
project, that "Jakarta Dependency Injection is a trademark of the Eclipse
Foundation", and that the materials are made available under the Apache License,
Version 2.0. The full notice travels inside the APK. The Apache-2.0 text is
included at [`licenses/Apache-2.0.txt`](licenses/Apache-2.0.txt).

## Items requiring owner confirmation

These are unresolved and must not be treated as cleared:

1. **Google proprietary components and AGPL distribution.** Firebase, Google
   Play services, Google Identity, Play Core, Play Integrity, and reCAPTCHA are
   governed by proprietary Google terms. Combining them with an AGPL-licensed
   application and distributing the result raises unresolved compliance
   questions. This notice does not assert that the combination is permitted.
2. **Launcher and splash artwork provenance.** The repository does not record
   whether this artwork was drawn by the owner, commissioned, or generated with
   a third-party tool. The owner must confirm authorship and the tool's terms
   before describing it as original.
3. **Screenshots and promotional media.** `docs/` and `brag-output/` are ignored
   by Git, but local copies exist. `brag-output/` contains generated video and
   audio; any music or sound effects must be licensed before publication.
   Screenshots should be checked for third-party imagery or personal data.
4. **Firebase content.** Place names, descriptions, story text, and images come
   from Firestore. Their copyright and image rights are not established by this
   repository and are outside the software licence.
5. **Prebuilt artefacts in version control.** `app/release/avalokan.apk` is a
   tracked build output. It predates these notices and must be rebuilt before
   any distribution so that it reflects the current licence and notice files.
6. **Dependency licence metadata gaps.** Roughly 200 resolved modules were
   inspected. Many AndroidX and Kotlin artefacts publish Gradle Module Metadata
   rather than a POM with a `<licenses>` block; their licences above come from
   the projects' published terms, not from a machine-readable field. Re-run the
   audit after any dependency upgrade.

## Full licence texts

| Licence | File |
| --- | --- |
| GNU Affero General Public License 3.0 (project licence) | [`LICENSE`](LICENSE) |
| Apache License 2.0 | [`licenses/Apache-2.0.txt`](licenses/Apache-2.0.txt) |
| Mozilla Public License 2.0 | [`licenses/MPL-2.0.txt`](licenses/MPL-2.0.txt) |
| SIL Open Font License 1.1 (Inter) | [`licenses/Inter-OFL-1.1.txt`](licenses/Inter-OFL-1.1.txt) |
| SIL Open Font License 1.1 (Montserrat) | [`licenses/Montserrat-OFL-1.1.txt`](licenses/Montserrat-OFL-1.1.txt) |
