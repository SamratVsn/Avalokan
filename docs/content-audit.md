# Avalokan content verification audit

Audit date: 2026-11-07
Scope: every content id audited in `docs/classification-audit.md` — `places` (16), `events` (4), `stories` (1).
Evidence base: the live Firestore snapshot and field notes recorded in `docs/classification-audit.md`. No document was modified for this audit.

This list is a triage tool, not a source of practical facts. Do not copy a value from this file into Firestore, a card, or a detail screen.

## Status definitions

| Status | Meaning | UI consequence |
|---|---|---|
| `verified` | A citation (`sourceLabel` and/or `sourceUrl`) and a verification date were checked against the displayed value. | Practical info may be shown with its source and last-verified date. |
| `unverified` | A value exists but is contradicted, implausible, or cannot be attributed. | The value is hidden until it can be sourced. |
| `needs source` | A value is stored but no citation or verification date is recorded. | The value is hidden until a citation is recorded. |
| `outdated` | A citation exists but the value is known to have changed. | The value is hidden until re-verified. |

The 2026-11-06 audit recorded missing source and verification dates for these documents. Until a citation is written to Firestore, every entry below remains `needs source` or `unverified`; none can honestly be called `verified`.

## Places

| Content id | Name | Status | Source recorded | Last verified recorded | Notes / next action |
|---|---|---|---|---|---|
| `bhaktapur-durbar` | Bhaktapur Durbar Square | `needs source` | Not recorded | Not recorded | Stored entry fee `Free` is unverified for foreign visitors. Record a citation and the fee basis. |
| `boudha-stupa` | Boudha Stupa | `needs source` | Not recorded | Not recorded | Practical fields and the badge year have no citation. Collect source and verification date. |
| `changu-narayan` | Changu Narayan Temple | `needs source` | Not recorded | Not recorded | Practical fields and the inscription date claim are unsourced. Collect source. |
| `chitwan-national-park` | Chitwan National Park | `unverified` | Not recorded | Not recorded | Stored entry fee `Free` is not credible for a national park. Replace with a sourced fee or hide it. |
| `gorkha-durbar` | Gorkha Durbar | `needs source` | Not recorded | Not recorded | Practical fields are unsourced. UNESCO status is also unverified; do not add a UNESCO badge. |
| `kathmandu-durbar` | Kathmandu Durbar Square | `needs source` | Not recorded | Not recorded | Entry fee contains a stray newline and has no citation. The "6th century BCE" claim is unverified. |
| `lumbini` | Lumbini | `needs source` | Not recorded | Not recorded | Practical fields are unsourced; relationship to the sacred garden is not disambiguated. |
| `lumbini-sacred-garden` | Lumbini Sacred Garden | `needs source` | Not recorded | Not recorded | Entry fee is blank and no citation exists. Possible duplicate of `lumbini`. |
| `patan-durbar` | Patan Durbar Square | `needs source` | Not recorded | Not recorded | Stored fee applies to foreign visitors only and needs a verified price basis. |
| `phewa-lake` | Phewa Taal (Lake) | `needs source` | Not recorded | Not recorded | Practical fields and conservation status are unsourced. |
| `rani-mahal` | Rani Mahal | `needs source` | Not recorded | Not recorded | Practical fields and construction details are unsourced. |
| `rara-lake` | Rara Lake | `needs source` | Not recorded | Not recorded | Practical fields and protected-area status are unsourced. |
| `sagarmatha-national-park` | Sagarmatha National Park | `unverified` | Not recorded | Not recorded | Stored entry fee `Free` is not credible for a national park. Replace with a sourced fee or hide it. |
| `swayambhunath-stupa` | Swayambhunath Stupa | `needs source` | Not recorded | Not recorded | Practical fields and the founding-date claim are unsourced. |
| `taksar-bhojpur` | Taksar (Bhojpur) | `needs source` | Not recorded | Not recorded | Craft-process and active-artisan claims are unverified; practical fields are unsourced. |
| `tilaurakot-archaeological-site` | Tilaurakot Archaeological Site | `needs source` | Not recorded | Not recorded | Practical fields and excavation provenance are unsourced. UNESCO status is not confirmed. |

## Events

| Content id | Name | Status | Source recorded | Last verified recorded | Notes / next action |
|---|---|---|---|---|---|
| `ai-conf` | AI Conf 2027 | `needs source` | Not recorded | Not recorded | Price and ticketing model are unknown, so no price is shown. Record organiser, price basis, and citation. |
| `chhath` | Chhath Puja | `needs source` | Not recorded | Not recorded | Stored `Free` price has no citation; exact-year dates and venues are unverified. |
| `devfest-kathmandu` | DevFest Kathmandu 2026 | `needs source` | Not recorded | Not recorded | `Check Link` is not a price. Registration requirement and fee need a source. |
| `tihar` | Tihar | `needs source` | Not recorded | Not recorded | Stored `Free` price and the `11 AM` time have no citation. |

## Stories

| Content id | Name | Status | Source recorded | Last verified recorded | Notes / next action |
|---|---|---|---|---|---|
| `boudhanath` | Boudhanath Stupa | `needs source` | Not recorded | Not recorded | Author, read time, editorial source, and verification date are missing. |

## Maintenance rules

1. A row becomes `verified` only after a citation is stored in `sourceLabel` and/or `sourceUrl` and the value is checked against it. Prefer also storing `lastVerifiedAt`.
2. Set `verificationStatus` to one of `VERIFIED`, `UNVERIFIED`, `NEEDS_SOURCE`, `OUTDATED` when the backfill writes the row, so the UI and this list agree.
3. Add an entry for every new place, event, or story before it ships. Missing content is an audit failure, not an implicit verification.
4. Re-audit any row older than 12 months, or immediately when a fee, schedule, or venue is reported as wrong.
5. Do not invent a visitor category, fee, hour, best-time, source, or verification date to fill a blank field.
