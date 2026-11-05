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
            imageUrl = "https://example.com/place.jpg",
            province = "Bagmati",
            district = "Kathmandu",
            latitude = 27.7043,
            longitude = 85.3075,
            era = "Malla period",
            unescoStatus = "World Heritage",
            heritageType = "Durbar square",
            significance = "Royal palace complex",
            livingHeritage = true,
            etiquette = "Remove shoes before entering temples.",
            accessibility = "Step-free entry to the outer square.",
            sourceLabel = "Department of Archaeology",
            sourceUrl = "https://example.com/source",
            lastVerifiedAt = "2026-10-01",
            verificationStatus = "VERIFIED"
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
                imageUrl = "https://example.com/place.jpg",
                province = "Bagmati",
                district = "Kathmandu",
                latitude = 27.7043,
                longitude = 85.3075,
                era = "Malla period",
                unescoStatus = "World Heritage",
                heritageType = "Durbar square",
                significance = "Royal palace complex",
                livingHeritage = true,
                etiquette = "Remove shoes before entering temples.",
                accessibility = "Step-free entry to the outer square.",
                sourceLabel = "Department of Archaeology",
                sourceUrl = "https://example.com/source",
                lastVerifiedAt = "2026-10-01",
                verificationStatus = "VERIFIED"
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
            joinLink = "https://example.com/join",
            eventKind = "FESTIVAL",
            locality = "NATIONWIDE",
            ticketing = "FREE",
            placeId = "kathmandu-durbar",
            storyId = "boudhanath",
            sourceLabel = "Kathmandu Metropolitan City",
            sourceUrl = "https://example.com/event-source",
            lastVerifiedAt = "2026-09-20",
            verificationStatus = "VERIFIED"
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
                joinLink = "https://example.com/join",
                eventKind = "FESTIVAL",
                locality = "NATIONWIDE",
                ticketing = "FREE",
                placeId = "kathmandu-durbar",
                storyId = "boudhanath",
                sourceLabel = "Kathmandu Metropolitan City",
                sourceUrl = "https://example.com/event-source",
                lastVerifiedAt = "2026-09-20",
                verificationStatus = "VERIFIED"
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
            badge = "Cultural Insight",
            curator = "Avalokan editorial desk",
            sourceLabel = "Interview with local caretakers",
            sourceUrl = "https://example.com/story-source",
            lastVerifiedAt = "2026-09-12",
            verificationStatus = "VERIFIED",
            placeIds = listOf("boudhanath", "kathmandu-durbar")
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
                badge = "Cultural Insight",
                curator = "Avalokan editorial desk",
                sourceLabel = "Interview with local caretakers",
                sourceUrl = "https://example.com/story-source",
                lastVerifiedAt = "2026-09-12",
                verificationStatus = "VERIFIED",
                placeIds = listOf("boudhanath", "kathmandu-durbar")
            ),
            entity.toDomain()
        )
    }
}
