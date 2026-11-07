package com.example.avalokan.data.classification

/**
 * Local classification bridge for documents written before the taxonomy fields
 * existed in Firestore.
 *
 * This is NOT a second source of truth and it does not invent facts: every
 * value below is derived from the place/event/story text that is already in
 * production, and the full audit is recorded in
 * `docs/classification-audit.md`. Firestore always wins when a field is
 * present, so once the collection is backfilled this catalog can be deleted
 * without touching the UI or domain layers.
 */
internal object LegacyClassificationCatalog {

    data class PlaceSeed(
        val primaryCategory: String,
        val secondaryCategories: List<String> = emptyList(),
        val typologies: List<String> = emptyList(),
        val attributes: List<String> = emptyList(),
        val tags: List<String> = emptyList(),
        val locality: String = "",
        val heritageType: String = "",
        val unescoStatus: String = "",
        val livingHeritage: Boolean = false,
    )

    data class EventSeed(
        val eventKind: String,
        val locality: String,
        val ticketing: String,
        val participation: String,
        val format: String,
        val priceBasis: String = "",
        val secondaryCategories: List<String> = emptyList(),
        val tags: List<String> = emptyList(),
        val relatedPlaceIds: List<String> = emptyList(),
    )

    data class StorySeed(
        val primaryCategory: String,
        val secondaryCategories: List<String> = emptyList(),
        val typologies: List<String> = emptyList(),
        val tags: List<String> = emptyList(),
        val placeIds: List<String> = emptyList(),
        val relatedEventIds: List<String> = emptyList(),
        val period: String = "",
    )

    private val places: Map<String, PlaceSeed> = mapOf(
        "bhaktapur-durbar" to PlaceSeed(
            primaryCategory = "HISTORIC_URBAN_HERITAGE",
            secondaryCategories = listOf("PALACE_OR_FORT"),
            typologies = listOf("DURBAR", "PAGODA", "NEWA_COURTYARD"),
            attributes = listOf(
                "HISTORICAL", "CULTURAL", "ARCHITECTURAL",
                "RELIGIOUS_SPIRITUAL", "COMMUNITY_HERITAGE",
            ),
            tags = listOf("Malla Period", "Newa Architecture", "UNESCO World Heritage", "Kathmandu Valley"),
            locality = "Bhaktapur",
            heritageType = "Durbar Square",
            unescoStatus = "World Heritage",
        ),
        "boudha-stupa" to PlaceSeed(
            primaryCategory = "BUDDHIST_HERITAGE_SITE",
            typologies = listOf("STUPA", "CHORTEN", "BUDDHIST_SACRED_SITE"),
            attributes = listOf(
                "RELIGIOUS_SPIRITUAL", "HISTORICAL", "CULTURAL",
                "ARCHITECTURAL", "PILGRIMAGE", "COMMUNITY_HERITAGE",
            ),
            tags = listOf("Tibetan Buddhism", "UNESCO World Heritage", "Kathmandu", "Buddhist Pilgrimage"),
            locality = "Kathmandu",
            heritageType = "Stupa",
            unescoStatus = "World Heritage",
        ),
        "changu-narayan" to PlaceSeed(
            primaryCategory = "HINDU_HERITAGE_SITE",
            secondaryCategories = listOf("ARCHAEOLOGICAL_SITE"),
            typologies = listOf("PAGODA"),
            attributes = listOf(
                "HISTORICAL", "RELIGIOUS_SPIRITUAL", "CULTURAL",
                "ARCHITECTURAL", "ARCHAEOLOGICAL", "PILGRIMAGE",
            ),
            tags = listOf("UNESCO World Heritage", "Ancient Temple", "Bhaktapur", "Vaishnavism"),
            locality = "Changunarayan",
            heritageType = "Pagoda Temple",
            unescoStatus = "World Heritage",
        ),
        "chitwan-national-park" to PlaceSeed(
            primaryCategory = "NATURAL_HERITAGE",
            attributes = listOf("NATURAL"),
            tags = listOf("UNESCO World Heritage", "Wildlife", "Terai", "One-horned Rhinoceros"),
            locality = "Chitwan",
            heritageType = "National Park",
            unescoStatus = "World Heritage",
        ),
        "gorkha-durbar" to PlaceSeed(
            primaryCategory = "PALACE_OR_FORT",
            secondaryCategories = listOf("HISTORIC_URBAN_HERITAGE"),
            typologies = listOf("DURBAR"),
            attributes = listOf(
                "HISTORICAL", "CULTURAL", "ARCHITECTURAL",
                "RELIGIOUS_SPIRITUAL", "PILGRIMAGE",
            ),
            tags = listOf("Shah Dynasty", "Unification of Nepal", "Gorkha"),
            locality = "Gorkha",
            heritageType = "Hilltop Palace",
        ),
        "kathmandu-durbar" to PlaceSeed(
            primaryCategory = "HISTORIC_URBAN_HERITAGE",
            secondaryCategories = listOf("PALACE_OR_FORT"),
            typologies = listOf("DURBAR", "PAGODA", "NEWA_COURTYARD"),
            attributes = listOf(
                "HISTORICAL", "CULTURAL", "ARCHITECTURAL",
                "RELIGIOUS_SPIRITUAL", "COMMUNITY_HERITAGE",
            ),
            tags = listOf("Malla Period", "Kumari Ghar", "UNESCO World Heritage", "Newa Architecture"),
            locality = "Kathmandu",
            heritageType = "Durbar Square",
            unescoStatus = "World Heritage",
        ),
        "lumbini" to PlaceSeed(
            primaryCategory = "BUDDHIST_HERITAGE_SITE",
            secondaryCategories = listOf("SACRED_LANDSCAPE", "ARCHAEOLOGICAL_SITE"),
            typologies = listOf("PILGRIMAGE_LANDSCAPE", "BUDDHIST_SACRED_SITE"),
            attributes = listOf(
                "RELIGIOUS_SPIRITUAL", "HISTORICAL", "CULTURAL",
                "ARCHAEOLOGICAL", "PILGRIMAGE",
            ),
            tags = listOf("Birthplace of the Buddha", "UNESCO World Heritage", "Maya Devi Temple", "Rupandehi"),
            locality = "Lumbini",
            heritageType = "Buddhist Pilgrimage Site",
            unescoStatus = "World Heritage",
        ),
        "lumbini-sacred-garden" to PlaceSeed(
            primaryCategory = "BUDDHIST_HERITAGE_SITE",
            secondaryCategories = listOf("SACRED_LANDSCAPE", "ARCHAEOLOGICAL_SITE"),
            typologies = listOf("PILGRIMAGE_LANDSCAPE", "BUDDHIST_SACRED_SITE"),
            attributes = listOf(
                "RELIGIOUS_SPIRITUAL", "ARCHAEOLOGICAL", "HISTORICAL",
                "CULTURAL", "PILGRIMAGE",
            ),
            tags = listOf("Maya Devi Temple", "Ashoka Pillar", "UNESCO World Heritage", "Buddhist Pilgrimage"),
            locality = "Lumbini",
            heritageType = "Sacred Garden",
            unescoStatus = "World Heritage",
        ),
        "patan-durbar" to PlaceSeed(
            primaryCategory = "HISTORIC_URBAN_HERITAGE",
            secondaryCategories = listOf("PALACE_OR_FORT"),
            typologies = listOf("DURBAR", "NEWA_COURTYARD", "SHIKHARA"),
            attributes = listOf(
                "HISTORICAL", "CULTURAL", "ARCHITECTURAL",
                "RELIGIOUS_SPIRITUAL", "COMMUNITY_HERITAGE",
            ),
            tags = listOf("Malla Period", "Krishna Mandir", "UNESCO World Heritage", "Newa Architecture", "Lalitpur"),
            locality = "Patan (Lalitpur)",
            heritageType = "Durbar Square",
            unescoStatus = "World Heritage",
        ),
        "phewa-lake" to PlaceSeed(
            primaryCategory = "NATURAL_HERITAGE",
            typologies = listOf("TAAL"),
            attributes = listOf("NATURAL"),
            tags = listOf("Pokhara", "Annapurna Views", "Freshwater Lake", "Lakeside"),
            locality = "Pokhara",
            heritageType = "Lake",
        ),
        "rani-mahal" to PlaceSeed(
            primaryCategory = "PALACE_OR_FORT",
            attributes = listOf("HISTORICAL", "ARCHITECTURAL", "CULTURAL"),
            tags = listOf("Rana Architecture", "Kali Gandaki", "Palpa"),
            locality = "Palpa",
            heritageType = "Riverside Palace",
        ),
        "rara-lake" to PlaceSeed(
            primaryCategory = "NATURAL_HERITAGE",
            typologies = listOf("TAAL"),
            attributes = listOf("NATURAL"),
            tags = listOf("Rara National Park", "Karnali", "Alpine Lake"),
            locality = "Mugu",
            heritageType = "Alpine Lake",
        ),
        "sagarmatha-national-park" to PlaceSeed(
            primaryCategory = "NATURAL_HERITAGE",
            attributes = listOf("NATURAL"),
            tags = listOf("UNESCO World Heritage", "Everest", "Khumbu", "Himalaya"),
            locality = "Solukhumbu",
            heritageType = "National Park",
            unescoStatus = "World Heritage",
        ),
        "swayambhunath-stupa" to PlaceSeed(
            primaryCategory = "BUDDHIST_HERITAGE_SITE",
            typologies = listOf("STUPA", "CHORTEN", "BUDDHIST_SACRED_SITE", "PILGRIMAGE_LANDSCAPE"),
            attributes = listOf(
                "RELIGIOUS_SPIRITUAL", "HISTORICAL", "CULTURAL",
                "ARCHITECTURAL", "PILGRIMAGE",
            ),
            tags = listOf("UNESCO World Heritage", "Monkey Temple", "Kathmandu Valley", "Buddhist Pilgrimage"),
            locality = "Kathmandu",
            heritageType = "Hilltop Stupa",
            unescoStatus = "World Heritage",
        ),
        "taksar-bhojpur" to PlaceSeed(
            primaryCategory = "LIVING_CULTURAL_HERITAGE",
            attributes = listOf("CULTURAL", "COMMUNITY_HERITAGE", "HISTORICAL"),
            tags = listOf("Metal Craft", "Bhojpur", "Living Heritage", "Artisans"),
            locality = "Bhojpur",
            heritageType = "Craft Settlement",
            livingHeritage = true,
        ),
        "tilaurakot-archaeological-site" to PlaceSeed(
            primaryCategory = "ARCHAEOLOGICAL_SITE",
            secondaryCategories = listOf("BUDDHIST_HERITAGE_SITE"),
            attributes = listOf(
                "ARCHAEOLOGICAL", "HISTORICAL", "RELIGIOUS_SPIRITUAL", "CULTURAL",
            ),
            tags = listOf("Shakya Kingdom", "Kapilvastu", "Buddha's Early Life", "Archaeology"),
            locality = "Tilaurakot",
            heritageType = "Archaeological Site",
        ),
    )

    private val events: Map<String, EventSeed> = mapOf(
        "ai-conf" to EventSeed(
            eventKind = "TECHNOLOGY_INNOVATION",
            locality = "LOCAL",
            ticketing = "UNKNOWN",
            participation = "UNKNOWN",
            format = "CONFERENCE",
            secondaryCategories = listOf("EDUCATIONAL"),
            tags = listOf("Artificial Intelligence", "Conference", "Kathmandu"),
        ),
        "chhath" to EventSeed(
            eventKind = "FESTIVAL",
            locality = "REGIONAL",
            ticketing = "FREE",
            participation = "OPEN",
            format = "FESTIVAL",
            secondaryCategories = listOf("RELIGIOUS_CELEBRATION"),
            tags = listOf("Hindu Festival", "Terai", "Chhath"),
        ),
        "devfest-kathmandu" to EventSeed(
            eventKind = "TECHNOLOGY_INNOVATION",
            locality = "LOCAL",
            ticketing = "UNKNOWN",
            participation = "UNKNOWN",
            format = "CONFERENCE",
            secondaryCategories = listOf("EDUCATIONAL"),
            tags = listOf("Google Developer Groups", "Developers", "Kathmandu"),
        ),
        "tihar" to EventSeed(
            eventKind = "FESTIVAL",
            locality = "NATIONWIDE",
            ticketing = "FREE",
            participation = "OPEN",
            format = "FESTIVAL",
            secondaryCategories = listOf("RELIGIOUS_CELEBRATION"),
            tags = listOf("Hindu Festival", "Festival of Lights", "Deusi Bhailo", "Nepal"),
        ),
    )

    private val stories: Map<String, StorySeed> = mapOf(
        "boudhanath" to StorySeed(
            primaryCategory = "RELIGION_PHILOSOPHY",
            secondaryCategories = listOf("HISTORY_CIVILIZATION", "ARCHITECTURE_HERITAGE"),
            typologies = listOf("STUPA"),
            tags = listOf("Tibetan Buddhism", "Kathmandu", "Buddhist Heritage", "UNESCO World Heritage"),
            placeIds = listOf("boudha-stupa"),
        ),
    )

    fun place(id: String): PlaceSeed? = places[id]
    fun event(id: String): EventSeed? = events[id]
    fun story(id: String): StorySeed? = stories[id]
}
