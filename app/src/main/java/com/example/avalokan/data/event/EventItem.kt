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

package com.example.avalokan.data.event

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

//Defining the basic parameters for the events

/**
 * Room persistence model. Map it to `domain.model.Event` at the repository
 * boundary; UI code should never depend on this type.
 */
@Entity(tableName = "events")
data class EventItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val meta: String = "",
    val fee: String = "",
    val action: String = "Join >",
    val type: String = "CULTURAL",
    val date: String = "",
//    Url for the image to show about the event
    val imageUrl: String? = null,
    val featured: Boolean = false,
//     Registration or ticket URL from Firestore (`joinLink`).
    val joinLink: String = "",
    // Classification. Empty values mean "not classified", not a guessed type.
    // eventKind: FESTIVAL / WORKSHOP / COMMUNITY / TECH
    val eventKind: String = "",
    // locality: NATIONWIDE / LOCAL / REGIONAL
    val locality: String = "",
    // ticketing: FREE / TICKETED / DONATION
    val ticketing: String = "",
    // Optional links to related heritage content.
    val placeId: String = "",
    val storyId: String = "",
    // Provenance. Blank means "not recorded", never "verified".
    val sourceLabel: String = "",
    val sourceUrl: String = "",
    val lastVerifiedAt: String = "",
    val verificationStatus: String = "",
    // Additional event dimensions. Type, reach, participation and pricing stay
    // independent so a technology conference can never become a festival.
    @ColumnInfo(defaultValue = "''") val priceBasis: String = "",
    @ColumnInfo(defaultValue = "''") val participation: String = "",
    @ColumnInfo(defaultValue = "''") val format: String = "",
    @ColumnInfo(defaultValue = "''") val secondaryCategories: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val tags: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val relatedPlaceIds: List<String> = emptyList(),
    @ColumnInfo(defaultValue = "''") val relatedEventIds: List<String> = emptyList()
)