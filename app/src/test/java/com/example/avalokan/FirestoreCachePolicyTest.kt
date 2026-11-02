package com.example.avalokan

import app.cash.turbine.test
import com.example.avalokan.data.firestore.SnapshotBatch
import com.example.avalokan.data.firestore.applyDocumentSnapshot
import com.example.avalokan.data.firestore.observeCachedWithSnapshots
import com.example.avalokan.data.firestore.shouldReplaceCache
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class FirestoreCachePolicyTest {

    @Test
    fun `server-confirmed missing document deletes the cached row`() = runTest {
        var upserted: String? = null
        var deleted = false

        applyDocumentSnapshot(
            exists = false,
            isFromCache = false,
            item = { "should not be mapped" },
            upsert = { upserted = it },
            delete = { deleted = true },
        )

        assertTrue(deleted)
        assertNull(upserted)
    }

    @Test
    fun `missing document from local cache keeps the cached row`() = runTest {
        var mapped = false
        var upserted: String? = null
        var deleted = false

        applyDocumentSnapshot(
            exists = false,
            isFromCache = true,
            item = {
                mapped = true
                "should not be mapped"
            },
            upsert = { upserted = it },
            delete = { deleted = true },
        )

        assertFalse(mapped)
        assertFalse(deleted)
        assertNull(upserted)
    }

    @Test
    fun `existing document is upserted instead of deleted`() = runTest {
        var upserted: String? = null
        var deleted = false

        applyDocumentSnapshot(
            exists = true,
            isFromCache = false,
            item = { "server row" },
            upsert = { upserted = it },
            delete = { deleted = true },
        )

        assertEquals("server row", upserted)
        assertFalse(deleted)
    }

    @Test
    fun `empty local-cache snapshot does not wipe the Room cache`() {
        assertFalse(shouldReplaceCache(isFromCache = true, itemCount = 0))
    }

    @Test
    fun `populated local-cache snapshot and empty server snapshot can replace the cache`() {
        assertTrue(shouldReplaceCache(isFromCache = true, itemCount = 1))
        assertTrue(shouldReplaceCache(isFromCache = false, itemCount = 0))
    }

    @Test
    fun `failed listener keeps the cached emission and reports the error`() = runTest {
        val listenerFailure = CompletableDeferred<Throwable>()
        val applied = mutableListOf<SnapshotBatch<String>>()
        val cached = flow {
            emit(listOf("cached"))
            awaitCancellation()
        }
        val snapshots = flow<SnapshotBatch<String>> {
            throw IOException("listen failed")
        }

        observeCachedWithSnapshots(
            cached = cached,
            snapshots = snapshots,
            onSnapshot = { applied += it },
            onListenerError = { listenerFailure.complete(it) },
            onSnapshotError = { error("snapshot callback must not run") },
        ).test {
            assertEquals(listOf("cached"), awaitItem())

            val error = listenerFailure.await()
            assertTrue(error is IOException)
            assertTrue(applied.isEmpty())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
