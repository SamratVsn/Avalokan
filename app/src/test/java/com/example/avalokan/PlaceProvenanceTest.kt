package com.example.avalokan

import com.example.avalokan.domain.model.Place
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pins the interim provenance rules: practical data and ratings are only
 * publishable when the place carries a citation.
 */
class PlaceProvenanceTest {

    private fun place(
        rating: String = "4.8",
        sourceLabel: String = "",
        sourceUrl: String = "",
        lastVerifiedAt: String = "",
        verificationStatus: String = "",
    ) = Place(
        id = "kathmandu-durbar",
        name = "Kathmandu Durbar Square",
        description = "Royal palace square",
        rating = rating,
        entryFee = "Rs. 1,000",
        hours = "9 AM – 5 PM",
        sourceLabel = sourceLabel,
        sourceUrl = sourceUrl,
        lastVerifiedAt = lastVerifiedAt,
        verificationStatus = verificationStatus,
    )

    @Test
    fun `rating without a source is not publishable`() {
        assertNull(place().sourcedRating)
        assertFalse(place().canShowPracticalInfo)
    }

    @Test
    fun `rating with a source is publishable`() {
        val subject = place(sourceLabel = "Department of Archaeology")

        assertEquals("4.8", subject.sourcedRating)
        assertTrue(subject.canShowPracticalInfo)
    }

    @Test
    fun `a source url alone counts as provenance`() {
        val subject = place(sourceUrl = "https://example.com/source")

        assertEquals("4.8", subject.sourcedRating)
        assertTrue(subject.canShowPracticalInfo)
    }

    @Test
    fun `a verification date alone allows practical info but not a rating`() {
        val subject = place(lastVerifiedAt = "2026-10-01")

        assertTrue(subject.canShowPracticalInfo)
        assertNull(subject.sourcedRating)
    }

    @Test
    fun `outdated practical info is hidden even when a source exists`() {
        val subject = place(
            sourceLabel = "Department of Archaeology",
            verificationStatus = "OUTDATED",
        )

        assertFalse(subject.canShowPracticalInfo)
        assertNull(subject.sourcedRating)
    }

    @Test
    fun `an explicitly unverified source does not make data publishable`() {
        val subject = place(
            sourceLabel = "Unconfirmed visitor report",
            verificationStatus = "UNVERIFIED",
        )

        assertFalse(subject.canShowPracticalInfo)
        assertNull(subject.sourcedRating)
    }

    @Test
    fun `blank values never count as provenance`() {
        val subject = place(sourceLabel = "   ", sourceUrl = "  ", lastVerifiedAt = " ")

        assertFalse(subject.hasProvenance)
        assertFalse(subject.canShowPracticalInfo)
        assertNull(subject.sourcedRating)
    }
}
