/*
 * Copyright (C) 2026 Samrat Parajuli
 *
 * This file is part of Avalokan.
 *
 * Avalokan is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * Avalokan is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Avalokan. If not, see <https://www.gnu.org/licenses/>.
 *
 * SPDX-License-Identifier: AGPL-3.0-or-later
 */

package com.example.avalokan

import com.example.avalokan.domain.model.Place
import com.example.avalokan.domain.model.PlaceSort
import com.example.avalokan.domain.model.sortedFor
import org.junit.Assert.assertEquals
import org.junit.Test

class PlaceSortTest {

    private fun place(
        id: String,
        name: String,
        district: String = "",
        province: String = "",
        unescoStatus: String = ""
    ) = Place(
        id = id,
        name = name,
        description = "",
        district = district,
        province = province,
        unescoStatus = unescoStatus
    )

    private val curated = listOf(
        place("zeta", "zeta temple", district = "Kathmandu"),
        place("alpha", "Alpha stupa", district = "Bhaktapur"),
        place("no-region", "No Region Palace"),
        place("province-only", "Province Garden", province = "Gandaki"),
        place("unesco", "Unesco Site", district = "Lumbini", unescoStatus = "World Heritage Site")
    )

    @Test
    fun `recommended keeps the curated order`() {
        assertEquals(
            curated.map { it.id },
            curated.sortedFor(PlaceSort.RECOMMENDED).map { it.id }
        )
    }

    @Test
    fun `name sorts case insensitively with id as the tie breaker`() {
        assertEquals(
            listOf("alpha", "no-region", "province-only", "unesco", "zeta"),
            curated.sortedFor(PlaceSort.NAME).map { it.id }
        )
    }

    @Test
    fun `name falls back to the id when two places share a name`() {
        val sameName = listOf(
            place("b", "Same"),
            place("a", "same")
        )
        assertEquals(
            listOf("a", "b"),
            sameName.sortedFor(PlaceSort.NAME).map { it.id }
        )
    }

    @Test
    fun `region groups by district then province and leaves blanks last`() {
        val places = listOf(
            place("kathmandu", "Kathmandu Place", district = "Kathmandu"),
            place("blank", "Blank Region"),
            place("bhaktapur", "Bhaktapur Place", district = "Bhaktapur", province = "Bagmati"),
            place("gandaki", "Gandaki Place", province = "Gandaki")
        )
        assertEquals(
            listOf("bhaktapur", "gandaki", "kathmandu", "blank"),
            places.sortedFor(PlaceSort.REGION).map { it.id }
        )
    }

    @Test
    fun `region label reads district then province and skips duplicates`() {
        val place = place(
            "one",
            "One",
            district = "Kathmandu",
            province = "Bagmati"
        )
        assertEquals("Kathmandu • Bagmati", place.regionLabel)

        val duplicate = place("two", "Two", district = "Bagmati", province = "Bagmati")
        assertEquals("Bagmati", duplicate.regionLabel)

        assertEquals("", place("three", "Three").regionLabel)
    }

    @Test
    fun `unesco listed places come first, then name order`() {
        assertEquals(
            listOf("unesco", "alpha", "no-region", "province-only", "zeta"),
            curated.sortedFor(PlaceSort.UNESCO).map { it.id }
        )
    }

    @Test
    fun `unesco is detected from tags as well as status`() {
        val tagged = Place(
            id = "tagged",
            name = "Tagged Place",
            description = "",
            tags = listOf("UNESCO")
        )
        assertEquals(
            listOf("tagged", "plain"),
            listOf(tagged, place("plain", "Plain")).sortedFor(PlaceSort.UNESCO).map { it.id }
        )
    }

    @Test
    fun `unknown or missing saved values fall back to recommended`() {
        assertEquals(PlaceSort.RECOMMENDED, PlaceSort.fromName(null))
        assertEquals(PlaceSort.RECOMMENDED, PlaceSort.fromName(""))
        assertEquals(PlaceSort.RECOMMENDED, PlaceSort.fromName("SOMETHING_ELSE"))
        assertEquals(PlaceSort.UNESCO, PlaceSort.fromName("UNESCO"))
    }
}
