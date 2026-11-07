# Avalokan classification audit

Audit date: 2026-11-06
Scope: live Firestore project `avalokan-1fa3a`, collections `places` (16), `events` (4), `stories` (1).
Method: read every document with the Firestore REST API, compared the stored fields against the Nepal-centric taxonomy in `domain/classification/HeritageClassification.kt`, and checked the existing text for evidence of each recommendation. No document was modified for this audit.

For per-document practical-data verification status, see [`content-audit.md`](content-audit.md).

## Why this audit exists

The app previously used one flat `category`/`type` string for every kind of content. That made a Durbar Square, a lake, and a stupa indistinguishable in Discover, and it let a technology conference be presented with festival language because its legacy type was `Technical`. The taxonomy below separates five independent dimensions:

1. **Primary category** — the place's main heritage identity.
2. **Secondary categories** — additional identities that must not be lost.
3. **Typologies** — architectural/landscape forms; a place may have several.
4. **Attributes** — Historical, Religious/Spiritual, Cultural, Archaeological, Architectural, Natural, Pilgrimage, Community Heritage.
5. **Locality, tags and related content** — geographic and editorial context.

Events use a separate `eventKind` plus `locality`, `participation`, `ticketing`, `priceBasis` and `format`. A story uses its own editorial categories.

Keys are stable strings (for example `PAGODA_NEWA`, `STUPA`, `FESTIVAL`). Display names live in `strings.xml`; the domain layer never contains user-facing copy.

## Places

| Content | Current Classification | Recommended Classification | Missing / Unverified Information |
|---|---|---|---|
| `bhaktapur-durbar` — Bhaktapur Durbar Square | `category: Historical`; `meta` says Cultural Site; badge "800+ Years"; `featured: true` | Primary `HISTORIC_URBAN_HERITAGE`; secondary `PALACE_OR_FORT`; typologies `DURBAR`, `PAGODA`, `NEWA_COURTYARD`; attributes Historical, Cultural, Architectural, Religious/Spiritual, Community Heritage; UNESCO listed; locality Bhaktapur | Exact construction dates; coordinates; official entry fee (stored `Free` is unverified for foreign visitors); source and verification date; which monuments are inside the square |
| `boudha-stupa` — Boudha Stupa | `category: Historical`; badge "14th Century" | Primary `BUDDHIST_HERITAGE_SITE`; typologies `STUPA`, `CHORTEN`, `BUDDHIST_SACRED_SITE`; attributes Religious/Spiritual, Historical, Cultural, Architectural, Pilgrimage, Community Heritage; UNESCO listed; locality Kathmandu | Founding date and current structure date; coordinates; source and verification date |
| `changu-narayan` — Changu Narayan Temple | `category: Historical`; `meta` Religious Site; badge "Ancient Heritage" | Primary `HINDU_HERITAGE_SITE`; secondary `ARCHAEOLOGICAL_SITE`; typology `PAGODA`; attributes Historical, Religious/Spiritual, Cultural, Architectural, Archaeological, Pilgrimage; UNESCO listed; locality Changunarayan | Earliest inscription date; coordinates; source and verification date |
| `chitwan-national-park` — Chitwan National Park | `category: Nature`; badge "UNESCO Natural Heritage" | Primary `NATURAL_HERITAGE`; attribute Natural; UNESCO listed; locality Chitwan | Park entry fee (stored `Free` is not credible for a national park); buffer-zone communities; coordinates; source and verification date |
| `gorkha-durbar` — Gorkha Durbar | `category: Historical`; badge "Unification History" | Primary `PALACE_OR_FORT`; secondary `HISTORIC_URBAN_HERITAGE`; typology `DURBAR`; attributes Historical, Cultural, Architectural, Religious/Spiritual, Pilgrimage; locality Gorkha | Construction dates; UNESCO status is **not** verified — do not add a UNESCO badge; coordinates; source |
| `kathmandu-durbar` — Kathmandu Durbar Square | `category: Historical`; badge "EST. 6TH CENTURY BCE"; entry fee has a stray newline | Primary `HISTORIC_URBAN_HERITAGE`; secondary `PALACE_OR_FORT`; typologies `DURBAR`, `PAGODA`, `NEWA_COURTYARD`; attributes Historical, Cultural, Architectural, Religious/Spiritual, Community Heritage; UNESCO listed; locality Kathmandu | The "6th century BCE" badge is unverified and should not be reused as a taxonomy fact; coordinates; actual entry fee; source and verification date |
| `lumbini` — Lumbini | `category: Historical`; `meta` Cultural Site; badge "2500+ Years"; `featured: true` | Primary `BUDDHIST_HERITAGE_SITE`; secondary `SACRED_LANDSCAPE`, `ARCHAEOLOGICAL_SITE`; typologies `PILGRIMAGE_LANDSCAPE`, `BUDDHIST_SACRED_SITE`; attributes Religious/Spiritual, Historical, Cultural, Archaeological, Pilgrimage; UNESCO listed; locality Lumbini | Relationship to the Lumbini master plan / sacred garden; coordinates; source |
| `lumbini-sacred-garden` — Lumbini Sacred Garden | `category: Nature`; badge "UNESCO World Heritage"; entry fee blank | Primary `BUDDHIST_HERITAGE_SITE`; secondary `SACRED_LANDSCAPE`, `ARCHAEOLOGICAL_SITE`; typologies `PILGRIMAGE_LANDSCAPE`, `BUDDHIST_SACRED_SITE`; attributes Religious/Spiritual, Archaeological, Historical, Cultural, Pilgrimage; UNESCO listed; locality Lumbini | The `Nature` category is misleading; coordinates; entry fee; distinction from `lumbini` (possible duplicate content) |
| `patan-durbar` — Patan Durbar Square | `category` **blank**; `meta` Cultural Site; badge "Medieval Heritage"; entry fee "Rs. 1000 (foreigners)" | Primary `HISTORIC_URBAN_HERITAGE`; secondary `PALACE_OR_FORT`; typologies `DURBAR`, `NEWA_COURTYARD`, `SHIKHARA` (Krishna Mandir); attributes Historical, Cultural, Architectural, Religious/Spiritual, Community Heritage; UNESCO listed; locality Patan (Lalitpur) | Missing `category` is a data bug; coordinates; source and verification date; the fee applies to foreigners only and needs a verified price basis |
| `phewa-lake` — Phewa Taal (Lake) | `category: Nature`; `meta` "Pokhara . Local"; badge "Lakeside View"; `featured: true` | Primary `NATURAL_HERITAGE`; typology `TAAL`; attribute Natural; locality Pokhara | **Not** a sacred landscape: do not add `SACRED_LANDSCAPE` or pilgrimage attributes without evidence; coordinates; water-area and conservation status; source |
| `rani-mahal` — Rani Mahal | `category: Historical`; badge "Riverside Palace" | Primary `PALACE_OR_FORT`; attributes Historical, Architectural, Cultural; locality Palpa | No typology fits the current enum cleanly; construction date, architect and current conservation status unverified; coordinates; source |
| `rara-lake` — Rara Lake | `category: Nature`; badge "Remote Alpine Lake" | Primary `NATURAL_HERITAGE`; typology `TAAL`; attribute Natural; locality Mugu | Coordinates; protected-area status; source and verification date |
| `sagarmatha-national-park` — Sagarmatha National Park | `category: Adventure`; badge "Everest Region" | Primary `NATURAL_HERITAGE`; attribute Natural; UNESCO listed; locality Solukhumbu | `Adventure` is an activity, not a heritage category; park entry fee (stored `Free`); coordinates; source |
| `swayambhunath-stupa` — Swayambhunath Stupa | `category: Spiritual`; badge "Hilltop Buddhist Heritage"; `featured: true` | Primary `BUDDHIST_HERITAGE_SITE`; typologies `STUPA`, `CHORTEN`, `BUDDHIST_SACRED_SITE`, `PILGRIMAGE_LANDSCAPE`; attributes Religious/Spiritual, Historical, Cultural, Architectural, Pilgrimage; UNESCO listed; locality Kathmandu | Founding date; coordinates; source and verification date |
| `taksar-bhojpur` — "Taksar " (note trailing space) | `category: Cultural`; badge "Traditional Craft Heritage" | Primary `LIVING_CULTURAL_HERITAGE`; attributes Cultural, Community Heritage, Historical; living heritage; locality Bhojpur | No architectural typology is appropriate; exact settlement/community, craft process and number of active artisans unverified; coordinates; source |
| `tilaurakot-archaeological-site` — Tilaurakot Archaeological Site | `category: Historical`; badge "Ancient Shakya Kingdom" | Primary `ARCHAEOLOGICAL_SITE`; secondary `BUDDHIST_HERITAGE_SITE`; attributes Archaeological, Historical, Religious/Spiritual, Cultural; locality Tilaurakot | UNESCO status is **not** confirmed as inscribed; do not add a UNESCO badge; coordinates; excavation provenance; source |

### Current Discover coverage

The five approved filters map to these typologies:

| Filter key | Typologies included |
|---|---|
| `PAGODA_NEWA` | `DEGAH`, `BAHAL`, `BAHI`, `DURBAR`, `PAGODA`, `NEWA_COURTYARD` |
| `STUPA_MONASTERY` | `STUPA`, `CHORTEN`, `GOMPA`, `MONASTERY`, `BUDDHIST_SACRED_SITE` |
| `SHIKHARA_GRANTHAKUTA` | `SHIKHARA`, `GRANTHAKUTA` |
| `SACRED_WATER` | `HITI`, `DHUNGE_DHARA`, `POKHARI`, `KUNDA`, `TRADITIONAL_WATER_SYSTEM` |
| `SACRED_LANDSCAPE` | `TAAL`, `DANDA`, `SACRED_CAVE`, `SACRED_FOREST`, `PILGRIMAGE_LANDSCAPE` |

`SACRED_WATER` currently has **no matching live content**. The UI must show a real empty state and must not fabricate a hiti or dhunge dhara. `SHIKHARA_GRANTHAKUTA` is matched only through Patan's Krishna Mandir.

## Events

Events are classified on five independent axes. Festival presentation is never inferred from the title or the legacy `type` alone.

| Content | Current Classification | Recommended Classification | Missing / Unverified Information |
|---|---|---|---|
| `ai-conf` — AI Conf 2027 | `type: Technical`; `fee: Not Available`; `meta: The Plaza, Kathmandu`; link present; `featured` absent | `eventKind: TECHNOLOGY_INNOVATION`; `format: CONFERENCE`; `locality: LOCAL` (Kathmandu venue); `ticketing: UNKNOWN`; `participation: UNKNOWN`; secondary `EDUCATIONAL`; tags Artificial Intelligence, Conference, Kathmandu | Price and ticketing model; whether registration is required; organiser; verification date. Do **not** show a price or a "Book Tickets" CTA |
| `chhath` — Chhath Puja | `type: Cultural`; `fee: Free`; `meta: Nepal • Tharu Community`; `featured` absent | `eventKind: FESTIVAL`; `format: FESTIVAL`; `locality: REGIONAL` (Terai/Tharu community reach, not nationwide); `ticketing: FREE`; `participation: OPEN`; secondary `RELIGIOUS_CELEBRATION`; tags Hindu Festival, Terai, Chhath | Exact year's dates and venue(s); whether public rituals are open to non-adherents at every site; source |
| `devfest-kathmandu` — DevFest Kathmandu 2026 | `type: Technical`; `fee: Check Link`; `meta: Kathmandu, Nepal • 9:00 AM`; link present; `featured` absent | `eventKind: TECHNOLOGY_INNOVATION`; `format: CONFERENCE`; `locality: LOCAL` (Kathmandu); `ticketing: UNKNOWN`; `participation: UNKNOWN`; secondary `EDUCATIONAL`; tags Google Developer Groups, Developers, Kathmandu | "Check Link" is not a price; registration requirement is likely but **unverified** and must stay `UNKNOWN` until confirmed; organiser; verification date |
| `tihar` — Tihar | `type: Cultural`; `fee: Free`; `meta: Nepal . 11 AM`; `featured: true`; no link | `eventKind: FESTIVAL`; `format: FESTIVAL`; `locality: NATIONWIDE`; `ticketing: FREE`; `participation: OPEN`; secondary `RELIGIOUS_CELEBRATION`; tags Hindu Festival, Festival of Lights, Deusi Bhailo, Nepal | Exact year and the meaning of "11 AM"; a canonical event link; source. Featured presentation is allowed because `featured=true` **and** the kind is festival-like |

### Legacy event semantics

| Stored `type` | New `eventKind` | Reason |
|---|---|---|
| `Technical`, `Technical` variants | `TECHNOLOGY_INNOVATION` | The existing AI Conf and DevFest documents use this value |
| `Religious` | `RELIGIOUS_CELEBRATION` | Explicit religious context |
| `Cultural` | `OTHER` (never `FESTIVAL`) | A generic cultural type is not proof of a festival |
| Anything else / blank | `OTHER` | Unknown values must not be guessed |

`Cultural` deliberately maps to `OTHER` in the legacy fallback. Chhath and Tihar are festivals because the audit recommends and backfills `eventKind: FESTIVAL`, not because their titles contain a festival name.

## Stories

| Content | Current Classification | Recommended Classification | Missing / Unverified Information |
|---|---|---|---|
| `boudhanath` — Boudhanath Stupa | `badge: Story of the Day`; no editorial category; `placeIds` absent; author blank | Primary `RELIGION_PHILOSOPHY`; secondary `HISTORY_CIVILIZATION`, `ARCHITECTURE_HERITAGE`; typology `STUPA`; tags Tibetan Buddhism, Kathmandu, Buddhist Heritage, UNESCO World Heritage; related place `boudha-stupa` | Author, read time, editorial source and verification date; historical period (leave blank unless verified) |

## Legacy bridge in this release

Until Firestore is backfilled, `data/classification/LegacyClassificationCatalog.kt` supplies the audited values above for the 16 places, 4 events and 1 story. Rules:

- A value present in Firestore **always wins**.
- A blank or missing Firestore field falls back to the audit seed.
- An id with no audit entry receives **no** classification. Nothing is inferred from `category`, `type`, `meta` or the title.
- The shim is a bridge, not a second source of truth. It can be deleted after the backfill without changing the UI or domain layers.

## Safe Firestore migration plan

The migration is additive and reversible. No existing field is renamed or deleted in this phase, so older app versions keep working.

### 1. Export and snapshot

1. Export `places`, `events` and `stories` with the Firestore Admin SDK or `gcloud firestore export` before writing anything.
2. Record document counts and a checksum of ids.
3. Keep the export in a versioned bucket with a retention window covering at least one app release.

### 2. Add fields without rewriting legacy fields

Write only the new fields. Leave `category`, `type`, `meta`, `fee`, `action`, `featured` and `joinLink` untouched so a rollback needs no data restore.

Field sets:

- `places`: `primaryCategory`, `secondaryCategories[]`, `typologies[]`, `attributes[]`, `tags[]`, `locality`, `relatedPlaceIds[]`, `relatedEventIds[]`, `heritageType`, `unescoStatus`, `livingHeritage`
- `events`: `eventKind`, `locality`, `ticketing`, `priceBasis`, `participation`, `format`, `secondaryCategories[]`, `tags[]`, `relatedPlaceIds[]`, `relatedEventIds[]`
- `stories`: `primaryCategory`, `secondaryCategories[]`, `typologies[]`, `tags[]`, `relatedEventIds[]`, `placeIds[]`, `period`

Validation rules before writing:

- Enum keys must exist in `HeritageClassification.kt`; otherwise leave the field blank and log the document id.
- Do not write a price unless the source document actually contains a verified price.
- Do not set `participation` from assumptions; use `UNKNOWN` when the source does not say.
- Do not add `UNESCO` tags to Gorkha Durbar or Tilaurakot without a citation.

### 3. Dry run

1. Run the backfill against the Firestore emulator using a copy of the export.
2. Diff the emulator result against the expected table above.
3. Start the app against the emulator and confirm every Discover filter, event card and detail screen.

### 4. Production backfill in batches

1. Backfill `places` first in batches of 25, then `events`, then `stories`.
2. Log every write with the document id and the fields changed.
3. Re-read each batch and verify that explicit fields were preserved.
4. Stop if any document fails validation; do not partially classify a collection.

### 5. Verify in the app

- Discover: all five filters return the expected ids, and `SACRED_WATER` shows the empty state rather than fabricated content.
- Place detail: badges appear only from verified fields.
- Events: the scope header reflects the actual `locality` values; Tihar keeps the featured treatment; AI Conf and DevFest never show a price or a booking CTA.
- Event detail: the section title matches the event kind; the footer appears only when a price or a valid link exists.

### 6. Remove the bridge

After 100% of documents carry the new fields and one release has shipped:

1. Delete `LegacyClassificationCatalog.kt` and the seed fallbacks in the three repositories.
2. Add a migration test that asserts a document with no classification reads as unclassified rather than falling back.
3. Remove this section from the audit or mark it complete.

### Rollback

1. Because the migration only adds fields, reverting the app build is safe; old clients ignore unknown fields.
2. If a bad value reaches production, re-export first, then clear only the affected new fields and rerun the batch.
3. Never run a destructive restore of the whole collection without a fresh export.

## Verification status

- Compile: `./gradlew compileDebugKotlin` passes.
- Unit tests: classification, event semantics, Firestore mapping, domain mapping, Discover filtering and the v9→v10 migration are covered by JVM tests.
- Room: version 10 adds every new column with `DEFAULT ''`; `10.json` is generated. The migration is tested through a recording `SQLiteConnection`.
- Not verified: no emulator or device is connected, so the visual result of the new filters, cards and detail layout has not been checked at runtime.
- Pre-existing unrelated test failures: `AuthErrorMessageTest` text expectation and two `SafeUriTest` cases that depend on an unmocked `android.net.Uri`.
