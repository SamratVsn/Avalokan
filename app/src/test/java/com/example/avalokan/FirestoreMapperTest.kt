package com.example.avalokan

import com.example.avalokan.data.event.toEventItem
import com.example.avalokan.data.firestore.asDoubleOrNull
import com.example.avalokan.data.firestore.asStringList
import com.example.avalokan.data.place.toPlaceItem
import com.example.avalokan.data.story.toStoryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Firestore documents are mapped through a field lookup so missing fields can
 * be tested without a live Firestore instance.
 */
class FirestoreMapperTest {

    @Test
    fun `event mapper uses safe defaults when fields are missing`() {
        val event = toEventItem("missing") { null }

        assertEquals("missing", event.id)
        assertEquals("", event.title)
        assertEquals("", event.description)
        assertEquals("", event.meta)
        assertEquals("", event.fee)
        assertEquals("Join >", event.action)
        assertEquals("CULTURAL", event.type)
        assertEquals("", event.date)
        assertNull(event.imageUrl)
        assertFalse(event.featured)
        assertEquals("", event.joinLink)
        assertEquals("", event.eventKind)
        assertEquals("", event.locality)
        assertEquals("", event.ticketing)
        assertEquals("", event.priceBasis)
        assertEquals("", event.participation)
        assertEquals("", event.format)
        assertTrue(event.secondaryCategories.isEmpty())
        assertTrue(event.tags.isEmpty())
        assertEquals("", event.placeId)
        assertEquals("", event.storyId)
        assertTrue(event.relatedPlaceIds.isEmpty())
        assertTrue(event.relatedEventIds.isEmpty())
        assertEquals("", event.sourceLabel)
        assertEquals("", event.sourceUrl)
        assertEquals("", event.lastVerifiedAt)
        assertEquals("", event.verificationStatus)
    }

    @Test
    fun `event mapper falls back to onJoinClick when joinLink is missing`() {
        val fields = mapOf(
            "title" to "Indra Jatra",
            "onJoinClick" to "https://example.com/join",
        )

        val event = toEventItem("indra-jatra") { fields[it] }

        assertEquals("Indra Jatra", event.title)
        assertEquals("https://example.com/join", event.joinLink)
    }

    @Test
    fun `event mapper treats a blank image as absent and unknown fields safely`() {
        val fields = mapOf<String, Any?>(
            "imageUrl" to "   ",
            "featured" to "true", // non-Boolean values must not become featured
        )

        val event = toEventItem("event") { fields[it] }

        assertNull(event.imageUrl)
        assertFalse(event.featured)
    }

    @Test
    fun `place mapper uses safe defaults when fields are missing`() {
        val place = toPlaceItem("missing") { null }

        assertEquals("missing", place.id)
        assertEquals("", place.name)
        assertEquals("", place.description)
        assertEquals("", place.meta)
        assertEquals("", place.category)
        assertEquals("", place.rating)
        assertEquals("", place.entryFee)
        assertEquals("", place.bestTime)
        assertEquals("", place.hours)
        assertEquals("", place.badge)
        assertFalse(place.featured)
        assertNull(place.imageUrl)
        assertEquals("", place.province)
        assertEquals("", place.district)
        assertNull(place.latitude)
        assertNull(place.longitude)
        assertEquals("", place.era)
        assertEquals("", place.unescoStatus)
        assertEquals("", place.heritageType)
        assertEquals("", place.significance)
        assertFalse(place.livingHeritage)
        assertEquals("", place.etiquette)
        assertEquals("", place.accessibility)
        assertEquals("", place.primaryCategory)
        assertTrue(place.secondaryCategories.isEmpty())
        assertTrue(place.typologies.isEmpty())
        assertTrue(place.attributes.isEmpty())
        assertTrue(place.tags.isEmpty())
        assertEquals("", place.locality)
        assertTrue(place.relatedPlaceIds.isEmpty())
        assertTrue(place.relatedEventIds.isEmpty())
        assertEquals("", place.sourceLabel)
        assertEquals("", place.sourceUrl)
        assertEquals("", place.lastVerifiedAt)
        assertEquals("", place.verificationStatus)
    }

    @Test
    fun `story mapper uses safe defaults when fields are missing`() {
        val story = toStoryItem("missing") { null }

        assertEquals("missing", story.id)
        assertEquals("", story.title)
        assertEquals("", story.subtitle)
        assertEquals("", story.description)
        assertNull(story.imageUrl)
        assertEquals("", story.author)
        assertEquals("", story.date)
        assertEquals("", story.readTime)
        assertEquals("", story.badge)
        assertEquals("", story.curator)
        assertEquals("", story.sourceLabel)
        assertEquals("", story.sourceUrl)
        assertEquals("", story.lastVerifiedAt)
        assertEquals("", story.verificationStatus)
        assertTrue(story.placeIds.isEmpty())
        assertEquals("", story.primaryCategory)
        assertTrue(story.secondaryCategories.isEmpty())
        assertTrue(story.typologies.isEmpty())
        assertTrue(story.tags.isEmpty())
        assertTrue(story.relatedEventIds.isEmpty())
        assertEquals("", story.period)
    }

    @Test
    fun `place mapper reads heritage context and provenance`() {
        val fields = mapOf<String, Any?>(
            "province" to "Bagmati",
            "district" to "Kathmandu",
            "latitude" to 27.7043,
            "longitude" to "85.3075",
            "era" to "Malla period",
            "unescoStatus" to "World Heritage",
            "heritageType" to "Durbar square",
            "significance" to "Royal palace complex",
            "livingHeritage" to true,
            "etiquette" to "Remove shoes before entering temples.",
            "accessibility" to "Step-free entry to the outer square.",
            "sourceLabel" to "Department of Archaeology",
            "sourceUrl" to "https://example.com/source",
            "lastVerifiedAt" to "2026-10-01",
            "verificationStatus" to "VERIFIED",
        )

        val place = toPlaceItem("kathmandu-durbar") { fields[it] }

        assertEquals("Bagmati", place.province)
        assertEquals("Kathmandu", place.district)
        assertEquals(27.7043, place.latitude!!, 0.000001)
        assertEquals(85.3075, place.longitude!!, 0.000001)
        assertEquals("Malla period", place.era)
        assertEquals("World Heritage", place.unescoStatus)
        assertEquals("Durbar square", place.heritageType)
        assertEquals("Royal palace complex", place.significance)
        assertTrue(place.livingHeritage)
        assertEquals("Remove shoes before entering temples.", place.etiquette)
        assertEquals("Step-free entry to the outer square.", place.accessibility)
        assertEquals("Department of Archaeology", place.sourceLabel)
        assertEquals("https://example.com/source", place.sourceUrl)
        assertEquals("2026-10-01", place.lastVerifiedAt)
        assertEquals("VERIFIED", place.verificationStatus)
    }

    @Test
    fun `event mapper reads classification and links`() {
        val fields = mapOf<String, Any?>(
            "eventKind" to "FESTIVAL",
            "locality" to "NATIONWIDE",
            "ticketing" to "FREE",
            "placeId" to "kathmandu-durbar",
            "storyId" to "boudhanath",
            "sourceLabel" to "Kathmandu Metropolitan City",
            "sourceUrl" to "https://example.com/event-source",
            "lastVerifiedAt" to "2026-09-20",
            "verificationStatus" to "VERIFIED",
        )

        val event = toEventItem("indra-jatra") { fields[it] }

        assertEquals("FESTIVAL", event.eventKind)
        assertEquals("NATIONWIDE", event.locality)
        assertEquals("FREE", event.ticketing)
        assertEquals("kathmandu-durbar", event.placeId)
        assertEquals("boudhanath", event.storyId)
        assertEquals("Kathmandu Metropolitan City", event.sourceLabel)
        assertEquals("https://example.com/event-source", event.sourceUrl)
        assertEquals("2026-09-20", event.lastVerifiedAt)
        assertEquals("VERIFIED", event.verificationStatus)
    }

    @Test
    fun `story mapper reads provenance and related places`() {
        val fields = mapOf<String, Any?>(
            "curator" to "Avalokan editorial desk",
            "sourceLabel" to "Interview with local caretakers",
            "sourceUrl" to "https://example.com/story-source",
            "lastVerifiedAt" to "2026-09-12",
            "verificationStatus" to "VERIFIED",
            "placeIds" to listOf("boudhanath", "kathmandu-durbar"),
        )

        val story = toStoryItem("boudhanath") { fields[it] }

        assertEquals("Avalokan editorial desk", story.curator)
        assertEquals("Interview with local caretakers", story.sourceLabel)
        assertEquals("https://example.com/story-source", story.sourceUrl)
        assertEquals("2026-09-12", story.lastVerifiedAt)
        assertEquals("VERIFIED", story.verificationStatus)
        assertEquals(listOf("boudhanath", "kathmandu-durbar"), story.placeIds)
    }

    @Test
    fun `story place ids accept a single string and drop blank entries`() {
        val fromArray = toStoryItem("story") {
            listOf(" boudhanath ", "", null)
        }
        val fromString = toStoryItem("story") { " boudhanath " }
        val fromBlank = toStoryItem("story") { "   " }

        assertEquals(listOf("boudhanath"), fromArray.placeIds)
        assertEquals(listOf("boudhanath"), fromString.placeIds)
        assertTrue(fromBlank.placeIds.isEmpty())
    }

    @Test
    fun `place mapper forbids inventing classification when no audit entry exists`() {
        // "missing" is not in the audited catalog: every classification field
        // must stay blank rather than being guessed from the category text.
        val place = toPlaceItem("missing") { null }

        assertNull(place.primaryCategory.takeIf { it.isNotBlank() })
        assertTrue(place.typologies.isEmpty())
        assertTrue(place.attributes.isEmpty())
    }

    @Test
    fun `place mapper reads explicit classification fields and preserves unknown keys`() {
        val fields = mapOf<String, Any?>(
            "primaryCategory" to "BUDDHIST_HERITAGE_SITE",
            "secondaryCategories" to listOf("SACRED_LANDSCAPE"),
            "typologies" to listOf("STUPA", "SOMETHING_NEW"),
            "attributes" to listOf("RELIGIOUS_SPIRITUAL", "PILGRIMAGE"),
            "tags" to listOf("Buddhist Pilgrimage"),
            "locality" to "Kathmandu",
            "relatedPlaceIds" to listOf("swayambhunath-stupa"),
            "relatedEventIds" to "buddha-jayanti",
        )

        val place = toPlaceItem("boudha-stupa") { fields[it] }

        assertEquals("BUDDHIST_HERITAGE_SITE", place.primaryCategory)
        assertEquals(listOf("SACRED_LANDSCAPE"), place.secondaryCategories)
        // Unknown taxonomy keys are preserved verbatim so a later app version
        // can understand them without a data migration.
        assertEquals(listOf("STUPA", "SOMETHING_NEW"), place.typologies)
        assertEquals(listOf("RELIGIOUS_SPIRITUAL", "PILGRIMAGE"), place.attributes)
        assertEquals(listOf("Buddhist Pilgrimage"), place.tags)
        assertEquals("Kathmandu", place.locality)
        assertEquals(listOf("swayambhunath-stupa"), place.relatedPlaceIds)
        assertEquals(listOf("buddha-jayanti"), place.relatedEventIds)
    }

    @Test
    fun `place mapper prefers Firestore fields over the audited legacy catalog`() {
        val fields = mapOf<String, Any?>(
            "primaryCategory" to "ARCHAEOLOGICAL_SITE",
            "typologies" to listOf("SHIKHARA"),
            "locality" to "Patan",
        )

        // boudha-stupa has a catalog seed; explicit Firestore values must win.
        val place = toPlaceItem("boudha-stupa") { fields[it] }

        assertEquals("ARCHAEOLOGICAL_SITE", place.primaryCategory)
        assertEquals(listOf("SHIKHARA"), place.typologies)
        assertEquals("Patan", place.locality)
    }

    @Test
    fun `place mapper falls back to the audited legacy catalog`() {
        val place = toPlaceItem("boudha-stupa") { null }

        assertEquals("BUDDHIST_HERITAGE_SITE", place.primaryCategory)
        assertTrue(place.typologies.contains("STUPA"))
        assertEquals("Kathmandu", place.locality)
        assertEquals("World Heritage", place.unescoStatus)
    }

    @Test
    fun `event mapper prefers explicit classification over the legacy catalog`() {
        val fields = mapOf<String, Any?>(
            "eventKind" to "WORKSHOP",
            "locality" to "LOCAL",
            "ticketing" to "TICKETED",
            "priceBasis" to "PER_PERSON",
            "participation" to "REGISTRATION_REQUIRED",
            "format" to "WORKSHOP",
            "tags" to listOf("Pottery"),
            "relatedEventIds" to listOf("tihar"),
        )

        // tihar has a catalog seed (FESTIVAL/NATIONWIDE/FREE); explicit fields win.
        val event = toEventItem("tihar") { fields[it] }

        assertEquals("WORKSHOP", event.eventKind)
        assertEquals("LOCAL", event.locality)
        assertEquals("TICKETED", event.ticketing)
        assertEquals("PER_PERSON", event.priceBasis)
        assertEquals("REGISTRATION_REQUIRED", event.participation)
        assertEquals("WORKSHOP", event.format)
        assertEquals(listOf("Pottery"), event.tags)
        assertEquals(listOf("tihar"), event.relatedEventIds)
    }

    @Test
    fun `event mapper falls back to the audited legacy catalog`() {
        val event = toEventItem("tihar") { null }

        assertEquals("FESTIVAL", event.eventKind)
        assertEquals("NATIONWIDE", event.locality)
        assertEquals("FREE", event.ticketing)
        assertEquals("OPEN", event.participation)
    }

    @Test
    fun `unknown document ids never receive a guessed event classification`() {
        val event = toEventItem("event") { null }

        assertEquals("", event.eventKind)
        assertEquals("", event.locality)
        assertEquals("", event.ticketing)
    }

    @Test
    fun `story mapper prefers explicit classification over the legacy catalog`() {
        val fields = mapOf<String, Any?>(
            "primaryCategory" to "ARCHITECTURE_HERITAGE",
            "typologies" to listOf("PAGODA"),
            "tags" to listOf("Wood carving"),
            "period" to "Malla period",
        )

        val story = toStoryItem("boudhanath") { fields[it] }

        assertEquals("ARCHITECTURE_HERITAGE", story.primaryCategory)
        assertEquals(listOf("PAGODA"), story.typologies)
        assertEquals(listOf("Wood carving"), story.tags)
        assertEquals("Malla period", story.period)
    }

    @Test
    fun `story mapper falls back to the audited legacy catalog`() {
        val story = toStoryItem("boudhanath") { null }

        assertEquals("RELIGION_PHILOSOPHY", story.primaryCategory)
        assertTrue(story.typologies.contains("STUPA"))
        assertEquals(listOf("boudha-stupa"), story.placeIds)
    }

    @Test
    fun `double coercion accepts numbers, numeric strings and rejects junk`() {
        assertEquals(27.7, (27.7 as Any?).asDoubleOrNull()!!, 0.000001)
        assertEquals(27.0, (27L as Any?).asDoubleOrNull()!!, 0.000001)
        assertEquals(27.7, (" 27.7 " as Any?).asDoubleOrNull()!!, 0.000001)
        assertNull(null.asDoubleOrNull())
        assertNull("not a number".asDoubleOrNull())
        assertNull(true.asDoubleOrNull())
    }
}
