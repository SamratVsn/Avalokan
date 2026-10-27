package com.example.avalokan

import com.example.avalokan.data.security.safeHttpUrl
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SafeUriTest {

    @Test
    fun `allows http and https and normalizes bare hosts`() {
        assertEquals("https://example.com/path", safeHttpUrl("example.com/path"))
        assertEquals("https://example.com", safeHttpUrl("https://example.com"))
    }

    @Test
    fun `blocks dangerous schemes`() {
        assertNull(safeHttpUrl("javascript:alert(1)"))
        assertNull(safeHttpUrl("file:///etc/passwd"))
        assertNull(safeHttpUrl("intent://evil#Intent;scheme=https;end"))
    }

    @Test
    fun `blank input is rejected`() {
        assertNull(safeHttpUrl("   "))
    }
}
