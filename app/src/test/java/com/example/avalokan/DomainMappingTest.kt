package com.example.avalokan

import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.event.toDomain
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.place.toDomain
import com.example.avalokan.data.story.StoryItem
import com.example.avalokan.data.story.toDomain
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.Story
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Pins the data-to-domain boundary. A Room column rename should be handled in
 * these mappers without changing what the UI receives.
 */
class DomainMappingTest {

    @Test
    fun `place entity maps every field to the domain model`() {
        val entity = PlaceItem(
            id = "kathmandu-durbar",
            name = "Kathmandu Durbar Square",
            description = "Royal palace square",
            meta = "Kathmandu • Historical Site",
            category = "Historical",
            rating = "4.8",
            entryFee = "Rs. 1000",
            bestTime = "Oct – Mar",
            hours = "9 AM – 5 PM",
            badge = "UNESCO Site",
            featured = true,
            imageUrl = "https://example.com/place.jpg"
        )

        assertEquals(
            Place(
                id = "kathmandu-durbar",
                name = "Kathmandu Durbar Square",
                description = "Royal palace square",
                meta = "Kathmandu • Historical Site",
                category = "Historical",
                rating = "4.8",
                entryFee = "Rs. 1000",
                bestTime = "Oct – Mar",
                hours = "9 AM – 5 PM",
                badge = "UNESCO Site",
                featured = true,
                imageUrl = "https://example.com/place.jpg"
            ),
            entity.toDomain()
        )
    }

    @Test
    fun `event entity maps every field to the domain model`() {
        val entity = EventItem(
            id = "indra-jatra",
            title = "Indra Jatra",
            description = "Chariot procession",
            meta = "Kathmandu • Cultural",
            fee = "Free",
            action = "Book >",
            type = "RELIGIOUS",
            date = "Sept 17, 2026",
            imageUrl = "https://example.com/event.jpg",
            featured = true,
            joinLink = "https://example.com/join"
        )

        assertEquals(
            Event(
                id = "indra-jatra",
                title = "Indra Jatra",
                description = "Chariot procession",
                meta = "Kathmandu • Cultural",
                fee = "Free",
                action = "Book >",
                type = "RELIGIOUS",
                date = "Sept 17, 2026",
                imageUrl = "https://example.com/event.jpg",
                featured = true,
                joinLink = "https://example.com/join"
            ),
            entity.toDomain()
        )
    }

    @Test
    fun `story entity maps every field to the domain model`() {
        val entity = StoryItem(
            id = "boudhanath",
            title = "The Sacred Echoes of Boudhanath",
            subtitle = "A living mandala",
            description = "Prayer wheels and butter lamps.",
            imageUrl = "https://example.com/story.jpg",
            author = "Samrat Parajuli",
            date = "Sept 12, 2026",
            readTime = "6 min read",
            badge = "Cultural Insight"
        )

        assertEquals(
            Story(
                id = "boudhanath",
                title = "The Sacred Echoes of Boudhanath",
                subtitle = "A living mandala",
                description = "Prayer wheels and butter lamps.",
                imageUrl = "https://example.com/story.jpg",
                author = "Samrat Parajuli",
                date = "Sept 12, 2026",
                readTime = "6 min read",
                badge = "Cultural Insight"
            ),
            entity.toDomain()
        )
    }
}
