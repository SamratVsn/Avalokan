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

import androidx.room3.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.avalokan.data.AvalokanDatabase
import com.example.avalokan.data.event.EventItem
import com.example.avalokan.data.place.PlaceItem
import com.example.avalokan.data.story.StoryItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Room behaviour that the offline cache depends on: replaceAll is atomic and
 * deleteById removes exactly one row.
 */
@RunWith(AndroidJUnit4::class)
class ContentDaoTest {

    private lateinit var database: AvalokanDatabase

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        database = Room.inMemoryDatabaseBuilder(context, AvalokanDatabase::class.java)
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun replaceAllReplacesThePreviousCache() = runBlocking {
        val placeDao = database.placeDao()
        placeDao.replaceAll(listOf(place("a"), place("b")))

        placeDao.replaceAll(listOf(place("c")))

        assertEquals(listOf("c"), placeDao.observeAll().first().map { it.id })
        assertNull(placeDao.observeById("a").first())
    }

    @Test
    fun deleteByIdRemovesOnlyTheRequestedRow() = runBlocking {
        val eventDao = database.eventDao()
        eventDao.replaceAll(listOf(event("a"), event("b")))

        eventDao.deleteById("a")

        assertEquals(listOf("b"), eventDao.observeAll().first().map { it.id })
        assertNull(eventDao.observeById("a").first())
        assertEquals("b", eventDao.observeById("b").first()?.id)
    }

    @Test
    fun clearAllEmptiesACollection() = runBlocking {
        val storyDao = database.storyDao()
        storyDao.replaceAll(listOf(story("a"), story("b")))

        storyDao.clearAll()

        assertEquals(emptyList<String>(), storyDao.observeAll().first().map { it.id })
    }

    private fun place(id: String) = PlaceItem(
        id = id,
        name = "Place $id",
        description = "Description $id",
    )

    private fun event(id: String) = EventItem(
        id = id,
        title = "Event $id",
        description = "Description $id",
    )

    private fun story(id: String) = StoryItem(
        id = id,
        title = "Story $id",
    )
}
