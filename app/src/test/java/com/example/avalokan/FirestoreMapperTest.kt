package com.example.avalokan

import com.example.avalokan.data.event.toEventItem
import com.example.avalokan.data.place.toPlaceItem
import com.example.avalokan.data.story.toStoryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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
    }
}
