package com.example.avalokan

import com.example.avalokan.domain.classification.EventFormat
import com.example.avalokan.domain.classification.EventKind
import com.example.avalokan.domain.classification.GeographicScope
import com.example.avalokan.domain.classification.HeritageGroup
import com.example.avalokan.domain.classification.HeritageTypology
import com.example.avalokan.domain.classification.Participation
import com.example.avalokan.domain.classification.PlaceAttribute
import com.example.avalokan.domain.classification.PlaceBadge
import com.example.avalokan.domain.classification.PlacePrimaryCategory
import com.example.avalokan.domain.classification.PricingModel
import com.example.avalokan.domain.classification.StoryCategory
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the Nepal-centric taxonomy: stable storage keys, alias tolerance and
 * multi-dimensional matching. Unknown keys must never crash or be guessed.
 */
class HeritageClassificationTest {

    private fun place(
        typologies: List<String> = emptyList(),
        primaryCategory: String = "",
        attributes: List<String> = emptyList(),
        unescoStatus: String = "",
        livingHeritage: Boolean = false,
        tags: List<String> = emptyList(),
    ) = Place(
        id = "place",
        name = "Place",
        description = "Description",
        typologies = typologies,
        primaryCategory = primaryCategory,
        attributes = attributes,
        unescoStatus = unescoStatus,
        livingHeritage = livingHeritage,
        tags = tags,
    )

    @Test
    fun `phase one groups cover the five approved Discover filters`() {
        assertEquals(
            listOf(
                "PAGODA_NEWA",
                "STUPA_MONASTERY",
                "SHIKHARA_GRANTHAKUTA",
                "SACRED_WATER",
                "SACRED_LANDSCAPE",
            ),
            HeritageGroup.entries.map { it.key },
        )
    }

    @Test
    fun `every typology rolls up into exactly one group`() {
        HeritageTypology.entries.forEach { typology ->
            assertTrue(
                "${typology.name} must belong to a group",
                HeritageGroup.entries.contains(typology.group),
            )
        }
    }

    @Test
    fun `group parsing accepts case and separator variants`() {
        assertEquals(HeritageGroup.PAGODA_NEWA, HeritageGroup.fromKey("pagoda_newa"))
        assertEquals(HeritageGroup.PAGODA_NEWA, HeritageGroup.fromKey("Pagoda Newa"))
        assertEquals(HeritageGroup.SACRED_WATER, HeritageGroup.fromKey(" sacred-water "))
        assertNull(HeritageGroup.fromKey("All"))
        assertNull(HeritageGroup.fromKey(""))
        assertNull(HeritageGroup.fromKey(null))
    }

    @Test
    fun `typology list drops duplicates and unknown keys but keeps order`() {
        val parsed = HeritageTypology.fromKeys(
            listOf("DURBAR", "pagoda", "DURBAR", "FUTURE_TYPOLOGY", ""),
        )

        assertEquals(listOf(HeritageTypology.DURBAR, HeritageTypology.PAGODA), parsed)
    }

    @Test
    fun `typology aliases map legacy vocabulary`() {
        assertEquals(HeritageTypology.DURBAR, HeritageTypology.fromKey("DURBAR_SQUARE"))
        assertEquals(HeritageTypology.DHUNGE_DHARA, HeritageTypology.fromKey("WATER_SPOUT"))
        assertEquals(HeritageTypology.TAAL, HeritageTypology.fromKey("LAKE"))
        assertEquals(HeritageTypology.POKHARI, HeritageTypology.fromKey("POND"))
    }

    @Test
    fun `place with multiple typologies matches every corresponding group`() {
        val subject = place(typologies = listOf("DURBAR", "SHIKHARA"))

        assertTrue(subject.matches(HeritageGroup.PAGODA_NEWA))
        assertTrue(subject.matches(HeritageGroup.SHIKHARA_GRANTHAKUTA))
        assertFalse(subject.matches(HeritageGroup.STUPA_MONASTERY))
        assertTrue(subject.matches(null))
    }

    @Test
    fun `place groups are deduplicated even with several typologies in one group`() {
        val subject = place(typologies = listOf("STUPA", "CHORTEN", "MONASTERY"))

        assertEquals(listOf(HeritageGroup.STUPA_MONASTERY), subject.heritageGroups)
    }

    @Test
    fun `unknown typology keys are ignored by typed accessors`() {
        val subject = place(typologies = listOf("FUTURE_TYPOLOGY"))

        assertTrue(subject.heritageTypologies.isEmpty())
        assertTrue(subject.heritageGroups.isEmpty())
        assertFalse(subject.matches(HeritageGroup.PAGODA_NEWA))
    }

    @Test
    fun `search matches name, locality and tags`() {
        val subject = Place(
            id = "boudha-stupa",
            name = "Boudha Stupa",
            description = "Description",
            locality = "Kathmandu",
            tags = listOf("Tibetan Buddhism"),
        )

        assertTrue(subject.matchesQuery("boudha"))
        assertTrue(subject.matchesQuery("kathmandu"))
        assertTrue(subject.matchesQuery("tibetan"))
        assertFalse(subject.matchesQuery("pokhara"))
        assertTrue(subject.matchesQuery("   "))
    }

    @Test
    fun `badges are derived from verified fields and capped`() {
        val subject = place(
            typologies = listOf("DURBAR", "PAGODA"),
            primaryCategory = "BUDDHIST_HERITAGE_SITE",
            attributes = listOf("HISTORICAL", "ARCHAEOLOGICAL"),
            unescoStatus = "World Heritage",
            livingHeritage = true,
        )

        val badges = subject.badges(limit = 8)

        assertTrue(badges.contains(PlaceBadge.UNESCO_HERITAGE))
        assertTrue(badges.contains(PlaceBadge.BUDDHIST_HERITAGE))
        assertTrue(badges.contains(PlaceBadge.NEWA_ARCHITECTURE))
        assertTrue(badges.contains(PlaceBadge.LIVING_HERITAGE))
        assertTrue(badges.contains(PlaceBadge.ARCHAEOLOGICAL_SITE))
        assertTrue(badges.contains(PlaceBadge.HISTORIC_MONUMENT))
        assertEquals(2, subject.badges().size)
    }

    @Test
    fun `unclassified place has no invented badges`() {
        val subject = place()

        assertTrue(subject.badges().isEmpty())
        assertNull(subject.primary)
        assertTrue(subject.placeAttributes.isEmpty())
    }

    @Test
    fun `primary category aliases and unknown values`() {
        assertEquals(
            PlacePrimaryCategory.HISTORIC_URBAN_HERITAGE,
            PlacePrimaryCategory.fromKey("Historical"),
        )
        assertEquals(
            PlacePrimaryCategory.NATURAL_HERITAGE,
            PlacePrimaryCategory.fromKey("nature"),
        )
        assertNull(PlacePrimaryCategory.fromKey("SOMETHING_FUTURE"))
    }

    @Test
    fun `place attributes parse independently from primary category`() {
        val subject = place(attributes = listOf("PILGRIMAGE", "spiritual", "Community"))

        assertEquals(
            listOf(
                PlaceAttribute.PILGRIMAGE,
                PlaceAttribute.RELIGIOUS_SPIRITUAL,
                PlaceAttribute.COMMUNITY_HERITAGE,
            ),
            subject.placeAttributes,
        )
    }

    @Test
    fun `event kind parsing is explicit and legacy Cultural stays OTHER`() {
        assertEquals(EventKind.FESTIVAL, EventKind.fromKey("festival"))
        assertEquals(EventKind.TECHNOLOGY_INNOVATION, EventKind.fromKey("TECH"))
        assertEquals(EventKind.RELIGIOUS_CELEBRATION, EventKind.fromKey("Religious"))

        // A legacy generic type must never be silently promoted to a festival.
        assertEquals(EventKind.OTHER, EventKind.fromLegacyType("Cultural"))
        assertEquals(EventKind.OTHER, EventKind.fromLegacyType(null))
        assertEquals(EventKind.TECHNOLOGY_INNOVATION, EventKind.fromLegacyType("Technical"))
    }

    @Test
    fun `festival-like kinds are the only featured eligible events`() {
        assertTrue(EventKind.FESTIVAL.isFestivalLike)
        assertTrue(EventKind.RELIGIOUS_CELEBRATION.isFestivalLike)
        assertTrue(EventKind.LOCAL_TRADITION.isFestivalLike)
        assertFalse(EventKind.WORKSHOP.isFestivalLike)
        assertFalse(EventKind.TECHNOLOGY_INNOVATION.isFestivalLike)
        assertFalse(EventKind.OTHER.isFestivalLike)
    }

    @Test
    fun `unknown event kind falls back to the conservative legacy type`() {
        val subject = Event(
            id = "event",
            title = "Event",
            description = "",
            eventKind = "FUTURE_KIND",
            type = "Technical",
        )

        assertEquals(EventKind.TECHNOLOGY_INNOVATION, subject.primaryKind)
    }

    @Test
    fun `event scope unknown is explicit rather than invented`() {
        assertEquals(GeographicScope.NATIONWIDE, GeographicScope.fromKey("national"))
        assertEquals(GeographicScope.LOCAL, GeographicScope.fromKey("municipality"))
        assertEquals(GeographicScope.REGIONAL, GeographicScope.fromKey("Province"))
        assertEquals(GeographicScope.UNKNOWN, GeographicScope.fromKey(""))
        assertEquals(GeographicScope.UNKNOWN, GeographicScope.fromKey("SOMEWHERE"))
    }

    @Test
    fun `participation and pricing tolerate aliases and unknown values`() {
        assertEquals(Participation.OPEN, Participation.fromKey("public"))
        assertEquals(Participation.REGISTRATION_REQUIRED, Participation.fromKey("registration"))
        assertEquals(Participation.UNKNOWN, Participation.fromKey("weird"))

        assertEquals(PricingModel.TICKETED, PricingModel.fromKey("paid"))
        assertEquals(PricingModel.DONATION, PricingModel.fromKey("donation_based"))
        assertEquals(PricingModel.UNKNOWN, PricingModel.fromKey(""))
    }

    @Test
    fun `event format parsing is independent from kind`() {
        assertEquals(EventFormat.CONFERENCE, EventFormat.fromKey("conference"))
        assertNull(EventFormat.fromKey("FUTURE_FORMAT"))
    }

    @Test
    fun `story categories accept editorial aliases`() {
        assertEquals(StoryCategory.ARCHITECTURE_HERITAGE, StoryCategory.fromKey("Architecture"))
        assertEquals(StoryCategory.FESTIVALS_TRADITIONS, StoryCategory.fromKey("festivals"))
        assertNull(StoryCategory.fromKey("FUTURE_CATEGORY"))

        val story = Story(
            id = "boudhanath",
            title = "Story",
            primaryCategory = "RELIGION_PHILOSOPHY",
            typologies = listOf("STUPA"),
        )

        assertEquals(StoryCategory.RELIGION_PHILOSOPHY, story.primary)
        assertEquals(listOf(HeritageTypology.STUPA), story.heritageTypologies)
    }
}

