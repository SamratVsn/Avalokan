package com.example.avalokan

import com.example.avalokan.data.Converters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun `empty list round-trips as an empty column`() {
        assertEquals("", converters.fromStringList(emptyList()))
        assertTrue(converters.toStringList("").isEmpty())
        assertTrue(converters.toStringList(null).isEmpty())
    }

    @Test
    fun `list of ids round-trips without loss`() {
        val ids = listOf("boudhanath", "kathmandu-durbar", "patan-durbar")

        assertEquals(ids, converters.toStringList(converters.fromStringList(ids)))
    }

    @Test
    fun `ids containing separators round-trip safely`() {
        val ids = listOf("place,with,commas", "percent%sign", "plain")

        assertEquals(ids, converters.toStringList(converters.fromStringList(ids)))
    }

    @Test
    fun `null list is written as an empty column`() {
        assertEquals("", converters.fromStringList(null))
    }
}
