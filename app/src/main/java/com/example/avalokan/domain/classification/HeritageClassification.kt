package com.example.avalokan.domain.classification

/**
 * Nepal-centric classification vocabulary.
 *
 * Storage keeps the stable [key] strings so a Firestore document edited by a
 * curator never depends on Kotlin enum ordinals. Unknown keys are preserved by
 * the data layer and simply ignored by typed accessors — that is what makes
 * the taxonomy extensible without a schema change.
 *
 * Display labels live in `strings.xml`; this file intentionally has no Android
 * dependency so it can be unit-tested on the JVM.
 */

/** Normalises human- and snake-case values into a stable comparison key. */
private fun normalizeKey(value: String?): String =
    value.orEmpty().trim().uppercase()
        .replace(Regex("[^A-Z0-9]+"), "_")
        .trim('_')

// ---------------------------------------------------------------------------
// Places
// ---------------------------------------------------------------------------

/** The five architectural/heritage discovery groups used by Discover filters. */
enum class HeritageGroup(val key: String) {
    PAGODA_NEWA("PAGODA_NEWA"),
    STUPA_MONASTERY("STUPA_MONASTERY"),
    SHIKHARA_GRANTHAKUTA("SHIKHARA_GRANTHAKUTA"),
    SACRED_WATER("SACRED_WATER"),
    SACRED_LANDSCAPE("SACRED_LANDSCAPE");

    companion object {
        fun fromKey(value: String?): HeritageGroup? {
            val key = normalizeKey(value)
            if (key.isEmpty()) return null
            return entries.firstOrNull { it.key == key || it.name == key }
        }
    }
}

/**
 * Granular heritage typology. Each value rolls up into exactly one
 * [HeritageGroup], while a place may carry several typologies.
 */
enum class HeritageTypology(val group: HeritageGroup) {
    // Pagoda & Newa architecture
    DEGAH(HeritageGroup.PAGODA_NEWA),
    BAHAL(HeritageGroup.PAGODA_NEWA),
    BAHI(HeritageGroup.PAGODA_NEWA),
    DURBAR(HeritageGroup.PAGODA_NEWA),
    PAGODA(HeritageGroup.PAGODA_NEWA),
    NEWA_COURTYARD(HeritageGroup.PAGODA_NEWA),

    // Stupas & monasteries
    STUPA(HeritageGroup.STUPA_MONASTERY),
    CHORTEN(HeritageGroup.STUPA_MONASTERY),
    GOMPA(HeritageGroup.STUPA_MONASTERY),
    MONASTERY(HeritageGroup.STUPA_MONASTERY),
    BUDDHIST_SACRED_SITE(HeritageGroup.STUPA_MONASTERY),

    // Shikhara & granthakuta
    SHIKHARA(HeritageGroup.SHIKHARA_GRANTHAKUTA),
    GRANTHAKUTA(HeritageGroup.SHIKHARA_GRANTHAKUTA),

    // Sacred water architecture
    HITI(HeritageGroup.SACRED_WATER),
    DHUNGE_DHARA(HeritageGroup.SACRED_WATER),
    POKHARI(HeritageGroup.SACRED_WATER),
    KUNDA(HeritageGroup.SACRED_WATER),
    TRADITIONAL_WATER_SYSTEM(HeritageGroup.SACRED_WATER),

    // Sacred landscapes
    TAAL(HeritageGroup.SACRED_LANDSCAPE),
    DANDA(HeritageGroup.SACRED_LANDSCAPE),
    SACRED_CAVE(HeritageGroup.SACRED_LANDSCAPE),
    SACRED_FOREST(HeritageGroup.SACRED_LANDSCAPE),
    PILGRIMAGE_LANDSCAPE(HeritageGroup.SACRED_LANDSCAPE);

    companion object {
        private val aliases = mapOf(
            "DURBAR_SQUARE" to DURBAR,
            "NEWA" to NEWA_COURTYARD,
            "NEWARI" to NEWA_COURTYARD,
            "BAHA" to BAHAL,
            "STUPA_COMPLEX" to STUPA,
            "BUDDHIST_MONASTERY" to MONASTERY,
            "WATER_SPOUT" to DHUNGE_DHARA,
            "STONE_SPOUT" to DHUNGE_DHARA,
            "POND" to POKHARI,
            "LAKE" to TAAL,
            "HILL" to DANDA,
            "CAVE" to SACRED_CAVE,
            "FOREST" to SACRED_FOREST,
        )

        fun fromKey(value: String?): HeritageTypology? {
            val key = normalizeKey(value)
            if (key.isEmpty()) return null
            return entries.firstOrNull { it.name == key } ?: aliases[key]
        }

        /** Expands a stored list, preserving order and dropping duplicates/unknowns. */
        fun fromKeys(values: List<String>): List<HeritageTypology> =
            values.mapNotNull(::fromKey).distinct()
    }
}

/** Main identity of a place. Distinct from the typology dimensions above. */
enum class PlacePrimaryCategory(val key: String) {
    HISTORIC_URBAN_HERITAGE("HISTORIC_URBAN_HERITAGE"),
    BUDDHIST_HERITAGE_SITE("BUDDHIST_HERITAGE_SITE"),
    HINDU_HERITAGE_SITE("HINDU_HERITAGE_SITE"),
    PALACE_OR_FORT("PALACE_OR_FORT"),
    ARCHAEOLOGICAL_SITE("ARCHAEOLOGICAL_SITE"),
    NATURAL_HERITAGE("NATURAL_HERITAGE"),
    LIVING_CULTURAL_HERITAGE("LIVING_CULTURAL_HERITAGE"),
    SACRED_LANDSCAPE("SACRED_LANDSCAPE"),
    OTHER("OTHER");

    companion object {
        private val aliases = mapOf(
            "HISTORIC" to HISTORIC_URBAN_HERITAGE,
            "HISTORICAL" to HISTORIC_URBAN_HERITAGE,
            "BUDDHIST" to BUDDHIST_HERITAGE_SITE,
            "HINDU" to HINDU_HERITAGE_SITE,
            "PALACE" to PALACE_OR_FORT,
            "FORT" to PALACE_OR_FORT,
            "ARCHAEOLOGY" to ARCHAEOLOGICAL_SITE,
            "NATURE" to NATURAL_HERITAGE,
            "CULTURAL" to LIVING_CULTURAL_HERITAGE,
        )

        fun fromKey(value: String?): PlacePrimaryCategory? {
            val key = normalizeKey(value)
            if (key.isEmpty()) return null
            return entries.firstOrNull { it.key == key || it.name == key } ?: aliases[key]
        }
    }
}

/** Independent place attributes that complement the architectural typology. */
enum class PlaceAttribute(val key: String) {
    HISTORICAL("HISTORICAL"),
    RELIGIOUS_SPIRITUAL("RELIGIOUS_SPIRITUAL"),
    CULTURAL("CULTURAL"),
    ARCHAEOLOGICAL("ARCHAEOLOGICAL"),
    ARCHITECTURAL("ARCHITECTURAL"),
    NATURAL("NATURAL"),
    PILGRIMAGE("PILGRIMAGE"),
    COMMUNITY_HERITAGE("COMMUNITY_HERITAGE");

    companion object {
        private val aliases = mapOf(
            "HISTORIC" to HISTORICAL,
            "RELIGIOUS" to RELIGIOUS_SPIRITUAL,
            "SPIRITUAL" to RELIGIOUS_SPIRITUAL,
            "COMMUNITY" to COMMUNITY_HERITAGE,
        )

        fun fromKey(value: String?): PlaceAttribute? {
            val key = normalizeKey(value)
            if (key.isEmpty()) return null
            return entries.firstOrNull { it.key == key || it.name == key } ?: aliases[key]
        }
    }
}

/** Verified-only badges. The UI shows the first one or two returned. */
enum class PlaceBadge {
    UNESCO_HERITAGE,
    BUDDHIST_HERITAGE,
    NEWA_ARCHITECTURE,
    SACRED_WATER_SYSTEM,
    LIVING_HERITAGE,
    ARCHAEOLOGICAL_SITE,
    HISTORIC_MONUMENT,
    NATURAL_HERITAGE,
}

// ---------------------------------------------------------------------------
// Events
// ---------------------------------------------------------------------------

/** Event-specific type. Never reuse place categories for events. */
enum class EventKind(val key: String) {
    FESTIVAL("FESTIVAL"),
    RELIGIOUS_CELEBRATION("RELIGIOUS_CELEBRATION"),
    LOCAL_TRADITION("LOCAL_TRADITION"),
    CULTURAL_PERFORMANCE("CULTURAL_PERFORMANCE"),
    COMMUNITY_GATHERING("COMMUNITY_GATHERING"),
    WORKSHOP("WORKSHOP"),
    EXHIBITION("EXHIBITION"),
    FOOD_CULINARY("FOOD_CULINARY"),
    HERITAGE_EXPERIENCE("HERITAGE_EXPERIENCE"),
    TECHNOLOGY_INNOVATION("TECHNOLOGY_INNOVATION"),
    EDUCATIONAL("EDUCATIONAL"),
    OTHER("OTHER");

    val isFestivalLike: Boolean
        get() = this == FESTIVAL || this == RELIGIOUS_CELEBRATION || this == LOCAL_TRADITION

    companion object {
        private val aliases = mapOf(
            "TECH" to TECHNOLOGY_INNOVATION,
            "TECHNICAL" to TECHNOLOGY_INNOVATION,
            "TECHNOLOGY" to TECHNOLOGY_INNOVATION,
            "RELIGIOUS" to RELIGIOUS_CELEBRATION,
            "COMMUNITY" to COMMUNITY_GATHERING,
            "FOOD" to FOOD_CULINARY,
        )

        fun fromKey(value: String?): EventKind? {
            val key = normalizeKey(value)
            if (key.isEmpty()) return null
            return entries.firstOrNull { it.key == key || it.name == key } ?: aliases[key]
        }

        /**
         * Legacy documents only have `type`. This is a conservative fallback:
         * a generic "Cultural" value becomes OTHER, never FESTIVAL, because a
         * festival must be explicitly classified.
         */
        fun fromLegacyType(value: String?): EventKind = when (normalizeKey(value)) {
            "TECH", "TECHNICAL", "TECHNOLOGY" -> TECHNOLOGY_INNOVATION
            "RELIGIOUS" -> RELIGIOUS_CELEBRATION
            "CULTURAL" -> OTHER
            else -> OTHER
        }
    }
}

enum class EventFormat(val key: String) {
    FESTIVAL("FESTIVAL"),
    WORKSHOP("WORKSHOP"),
    EXHIBITION("EXHIBITION"),
    GATHERING("GATHERING"),
    CONFERENCE("CONFERENCE"),
    PERFORMANCE("PERFORMANCE"),
    OTHER("OTHER");

    companion object {
        fun fromKey(value: String?): EventFormat? {
            val key = normalizeKey(value)
            if (key.isEmpty()) return null
            return entries.firstOrNull { it.key == key || it.name == key }
        }
    }
}

enum class GeographicScope(val key: String) {
    LOCAL("LOCAL"),
    REGIONAL("REGIONAL"),
    NATIONWIDE("NATIONWIDE"),
    UNKNOWN("UNKNOWN");

    companion object {
        private val aliases = mapOf(
            "NATIONAL" to NATIONWIDE,
            "COUNTRY" to NATIONWIDE,
            "CITY" to LOCAL,
            "MUNICIPALITY" to LOCAL,
            "COMMUNITY" to LOCAL,
            "PROVINCE" to REGIONAL,
            "DISTRICT" to REGIONAL,
        )

        fun fromKey(value: String?): GeographicScope {
            val key = normalizeKey(value)
            if (key.isEmpty()) return UNKNOWN
            return entries.firstOrNull { it.key == key || it.name == key } ?: aliases[key] ?: UNKNOWN
        }
    }
}

enum class Participation(val key: String) {
    OPEN("OPEN"),
    REGISTRATION_REQUIRED("REGISTRATION_REQUIRED"),
    INVITATION_ONLY("INVITATION_ONLY"),
    UNKNOWN("UNKNOWN");

    companion object {
        private val aliases = mapOf(
            "PUBLIC" to OPEN,
            "REGISTRATION" to REGISTRATION_REQUIRED,
            "INVITE_ONLY" to INVITATION_ONLY,
        )

        fun fromKey(value: String?): Participation {
            val key = normalizeKey(value)
            if (key.isEmpty()) return UNKNOWN
            return entries.firstOrNull { it.key == key || it.name == key } ?: aliases[key] ?: UNKNOWN
        }
    }
}

enum class PricingModel(val key: String) {
    FREE("FREE"),
    TICKETED("TICKETED"),
    DONATION("DONATION"),
    UNKNOWN("UNKNOWN");

    companion object {
        private val aliases = mapOf(
            "DONATION_BASED" to DONATION,
            "PAID" to TICKETED,
            "TICKET" to TICKETED,
        )

        fun fromKey(value: String?): PricingModel {
            val key = normalizeKey(value)
            if (key.isEmpty()) return UNKNOWN
            return entries.firstOrNull { it.key == key || it.name == key } ?: aliases[key] ?: UNKNOWN
        }
    }
}

// ---------------------------------------------------------------------------
// Stories
// ---------------------------------------------------------------------------

enum class StoryCategory(val key: String) {
    HISTORY_CIVILIZATION("HISTORY_CIVILIZATION"),
    ARCHITECTURE_HERITAGE("ARCHITECTURE_HERITAGE"),
    RELIGION_PHILOSOPHY("RELIGION_PHILOSOPHY"),
    FESTIVALS_TRADITIONS("FESTIVALS_TRADITIONS"),
    ART_CRAFTSMANSHIP("ART_CRAFTSMANSHIP"),
    PEOPLE_LEGENDS("PEOPLE_LEGENDS"),
    FOOD_CULTURE("FOOD_CULTURE"),
    NATURE_SACRED_LANDSCAPES("NATURE_SACRED_LANDSCAPES"),
    LOCAL_COMMUNITIES("LOCAL_COMMUNITIES"),
    ARCHAEOLOGY("ARCHAEOLOGY");

    companion object {
        private val aliases = mapOf(
            "HISTORY" to HISTORY_CIVILIZATION,
            "ARCHITECTURE" to ARCHITECTURE_HERITAGE,
            "RELIGION" to RELIGION_PHILOSOPHY,
            "FESTIVALS" to FESTIVALS_TRADITIONS,
            "ART" to ART_CRAFTSMANSHIP,
            "CRAFT" to ART_CRAFTSMANSHIP,
            "PEOPLE" to PEOPLE_LEGENDS,
            "LEGENDS" to PEOPLE_LEGENDS,
            "FOOD" to FOOD_CULTURE,
            "NATURE" to NATURE_SACRED_LANDSCAPES,
            "COMMUNITIES" to LOCAL_COMMUNITIES,
        )

        fun fromKey(value: String?): StoryCategory? {
            val key = normalizeKey(value)
            if (key.isEmpty()) return null
            return entries.firstOrNull { it.key == key || it.name == key } ?: aliases[key]
        }
    }
}
