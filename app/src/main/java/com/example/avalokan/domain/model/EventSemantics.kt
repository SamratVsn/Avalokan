package com.example.avalokan.domain.model

import com.example.avalokan.domain.classification.EventFormat
import com.example.avalokan.domain.classification.EventKind
import com.example.avalokan.domain.classification.Participation
import com.example.avalokan.domain.classification.PricingModel

/**
 * Context-aware event presentation semantics.
 *
 * These functions contain no Android or Compose dependencies so every rule
 * ("never show a booking CTA without a link", "TECH is not a festival") can be
 * covered by JVM unit tests. The UI only maps the returned values to strings,
 * colours and icons.
 */

enum class EventAboutTitle {
    FESTIVAL,
    CELEBRATION,
    WORKSHOP,
    EXHIBITION,
    GATHERING,
    CONFERENCE,
    EVENT,
}

enum class EventCta {
    BOOK_TICKETS,
    REGISTER,
    VISIT_WEBSITE,
    NONE,
}

enum class EventPriceLabel {
    ENTRY,
    TICKET,
    PRICE_PER_PERSON,
    DONATION,
}

data class EventPriceDisplay(
    val label: EventPriceLabel,
    val value: String,
)

/** The title above the description, chosen from the verified event type. */
fun Event.aboutTitle(): EventAboutTitle = when (primaryKind) {
    EventKind.FESTIVAL -> EventAboutTitle.FESTIVAL
    EventKind.RELIGIOUS_CELEBRATION, EventKind.LOCAL_TRADITION -> EventAboutTitle.CELEBRATION
    EventKind.WORKSHOP -> EventAboutTitle.WORKSHOP
    EventKind.EXHIBITION -> EventAboutTitle.EXHIBITION
    EventKind.COMMUNITY_GATHERING -> EventAboutTitle.GATHERING
    EventKind.TECHNOLOGY_INNOVATION -> EventAboutTitle.EVENT
    else -> if (eventFormat == EventFormat.CONFERENCE) {
        EventAboutTitle.CONFERENCE
    } else {
        EventAboutTitle.EVENT
    }
}

/**
 * A commercial action is only offered when a real web destination exists.
 * A generic external link is never labelled as ticket booking.
 */
fun Event.cta(): EventCta {
    if (!hasValidJoinLink) return EventCta.NONE
    return when {
        pricing == PricingModel.TICKETED -> EventCta.BOOK_TICKETS
        participationRequirement == Participation.REGISTRATION_REQUIRED -> EventCta.REGISTER
        else -> EventCta.VISIT_WEBSITE
    }
}

/**
 * Returns a price block only for verified pricing. Unknown fees return null so
 * the UI omits the section instead of presenting "Not available" as a price.
 */
fun Event.priceDisplay(): EventPriceDisplay? {
    val verifiedFee = verifiedFeeText()
    return when (pricing) {
        PricingModel.FREE -> EventPriceDisplay(EventPriceLabel.ENTRY, "Free")
        PricingModel.DONATION -> EventPriceDisplay(
            EventPriceLabel.DONATION,
            verifiedFee ?: "Donation based",
        )
        PricingModel.TICKETED -> verifiedFee?.let { fee ->
            val label = if (priceBasis.trim().equals("PER_PERSON", ignoreCase = true)) {
                EventPriceLabel.PRICE_PER_PERSON
            } else {
                EventPriceLabel.TICKET
            }
            EventPriceDisplay(label, fee)
        }
        PricingModel.UNKNOWN -> null
    }
}

/** Price text suitable for a compact event card, or null when unverified. */
fun Event.cardPriceText(): String? = priceDisplay()?.value

private val nonPriceValues = setOf(
    "",
    "free",
    "not available",
    "check link",
    "n/a",
    "na",
    "unknown",
    "tba",
    "-",
    "…",
    "....",
    ".....",
)

private fun Event.verifiedFeeText(): String? =
    fee.trim().takeIf { it.lowercase() !in nonPriceValues }
