package com.example.avalokan

import com.example.avalokan.domain.classification.EventKind
import com.example.avalokan.domain.classification.PricingModel
import com.example.avalokan.domain.model.Event
import com.example.avalokan.domain.model.EventAboutTitle
import com.example.avalokan.domain.model.EventCta
import com.example.avalokan.domain.model.EventPriceLabel
import com.example.avalokan.domain.model.aboutTitle
import com.example.avalokan.domain.model.cta
import com.example.avalokan.domain.model.priceDisplay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the context-aware event rules. These are the acceptance tests from the
 * task: no invented prices, no booking CTA without a link, and no festival
 * language for non-festival events.
 */
class EventSemanticsTest {

    private fun event(
        kind: EventKind = EventKind.FESTIVAL,
        pricing: PricingModel = PricingModel.UNKNOWN,
        fee: String = "",
        link: String = "",
        participation: String = "UNKNOWN",
        format: String = "",
        priceBasis: String = "",
        featured: Boolean = false,
        type: String = "CULTURAL",
    ) = Event(
        id = "event",
        title = "Event",
        description = "Description",
        fee = fee,
        joinLink = link,
        eventKind = kind.key,
        ticketing = pricing.key,
        participation = participation,
        format = format,
        priceBasis = priceBasis,
        featured = featured,
        type = type,
    )

    @Test
    fun `ticketed event with a booking link shows ticket price and book CTA`() {
        val subject = event(
            pricing = PricingModel.TICKETED,
            fee = "Rs. 500",
            link = "https://example.com/tickets",
        )

        val price = subject.priceDisplay()

        assertNotNull(price)
        assertEquals(EventPriceLabel.TICKET, price!!.label)
        assertEquals("Rs. 500", price.value)
        assertEquals(EventCta.BOOK_TICKETS, subject.cta())
    }

    @Test
    fun `ticketed event without a link shows no booking CTA`() {
        val subject = event(pricing = PricingModel.TICKETED, fee = "Rs. 500")

        assertNotNull(subject.priceDisplay())
        assertEquals(EventCta.NONE, subject.cta())
    }

    @Test
    fun `price per person is only used when explicitly verified`() {
        val perPerson = event(
            pricing = PricingModel.TICKETED,
            fee = "Rs. 500",
            link = "https://example.com/tickets",
            priceBasis = "PER_PERSON",
        )
        val unspecified = event(
            pricing = PricingModel.TICKETED,
            fee = "Rs. 500",
            link = "https://example.com/tickets",
        )

        assertEquals(EventPriceLabel.PRICE_PER_PERSON, perPerson.priceDisplay()!!.label)
        assertEquals(EventPriceLabel.TICKET, unspecified.priceDisplay()!!.label)
    }

    @Test
    fun `free event with a link gets a neutral website CTA, never booking`() {
        val subject = event(
            pricing = PricingModel.FREE,
            link = "https://example.com/event",
        )

        val price = subject.priceDisplay()

        assertEquals(EventPriceLabel.ENTRY, price!!.label)
        assertEquals("Free", price.value)
        assertEquals(EventCta.VISIT_WEBSITE, subject.cta())
    }

    @Test
    fun `free event without a link has no CTA`() {
        val subject = event(pricing = PricingModel.FREE)

        assertNotNull(subject.priceDisplay())
        assertEquals(EventCta.NONE, subject.cta())
    }

    @Test
    fun `unknown pricing is never presented as a price`() {
        val unknown = event(pricing = PricingModel.UNKNOWN, fee = "Not Available")
        val blank = event(pricing = PricingModel.UNKNOWN)

        assertNull(unknown.priceDisplay())
        assertNull(blank.priceDisplay())
    }

    @Test
    fun `ticketed event with an unverified fee string has no price block`() {
        val subject = event(
            pricing = PricingModel.TICKETED,
            fee = "Check Link",
            link = "https://example.com/tickets",
        )

        assertNull(subject.priceDisplay())
        // The link may still be useful, but it is not a verified booking price.
        assertEquals(EventCta.BOOK_TICKETS, subject.cta())
    }

    @Test
    fun `donation based event is not shown as a mandatory fee`() {
        val subject = event(
            pricing = PricingModel.DONATION,
            fee = "Donation Based",
            link = "https://example.com/support",
        )

        val price = subject.priceDisplay()

        assertEquals(EventPriceLabel.DONATION, price!!.label)
        assertEquals("Donation Based", price.value)
        assertEquals(EventCta.VISIT_WEBSITE, subject.cta())
    }

    @Test
    fun `registration is used only for verified registration requirements`() {
        val subject = event(
            pricing = PricingModel.FREE,
            link = "https://example.com/register",
            participation = "REGISTRATION_REQUIRED",
        )

        assertEquals(EventCta.REGISTER, subject.cta())
    }

    @Test
    fun `technology event never uses festival language`() {
        val subject = event(
            kind = EventKind.TECHNOLOGY_INNOVATION,
            format = "CONFERENCE",
            type = "Technical",
            featured = true,
            link = "https://example.com/ai-conf",
        )

        assertEquals(EventAboutTitle.EVENT, subject.aboutTitle())
        assertEquals(EventKind.TECHNOLOGY_INNOVATION, subject.primaryKind)
        assertFalse(subject.isFeaturedEligible)
    }

    @Test
    fun `legacy Technical type maps to technology, not cultural`() {
        val legacy = Event(
            id = "ai-conf",
            title = "AI Conf",
            description = "Conference",
            type = "Technical",
        )

        assertEquals(EventKind.TECHNOLOGY_INNOVATION, legacy.primaryKind)
    }

    @Test
    fun `workshop uses workshop wording`() {
        val subject = event(kind = EventKind.WORKSHOP)

        assertEquals(EventAboutTitle.WORKSHOP, subject.aboutTitle())
    }

    @Test
    fun `community gathering uses gathering wording`() {
        val subject = event(kind = EventKind.COMMUNITY_GATHERING)

        assertEquals(EventAboutTitle.GATHERING, subject.aboutTitle())
    }

    @Test
    fun `festival with featured status is eligible for featured presentation`() {
        val subject = event(kind = EventKind.FESTIVAL, featured = true)

        assertTrue(subject.isFeaturedEligible)
        assertEquals(EventAboutTitle.FESTIVAL, subject.aboutTitle())
    }

    @Test
    fun `non-festival featured event is not eligible for featured presentation`() {
        val subject = event(kind = EventKind.WORKSHOP, featured = true)

        assertFalse(subject.isFeaturedEligible)
    }

    @Test
    fun `cultural legacy type is not silently promoted to a festival`() {
        val legacy = Event(
            id = "unknown",
            title = "Community Meetup",
            description = "Description",
            type = "Cultural",
        )

        assertEquals(EventKind.OTHER, legacy.primaryKind)
        assertEquals(EventAboutTitle.EVENT, legacy.aboutTitle())
        assertFalse(legacy.isFeaturedEligible)
    }
}
