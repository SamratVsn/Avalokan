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

package com.example.avalokan.data.firestore

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

/**
 * One collection snapshot after Firestore documents have been mapped to the
 * Room entity type. Kept separate from Firestore so the cache policy can be
 * unit-tested without a Firebase emulator.
 */
internal data class SnapshotBatch<T>(
    val items: List<T>,
    val isFromCache: Boolean,
)

internal enum class DocumentCacheAction {
    Upsert,
    KeepCachedValue,
    Delete,
}

/**
 * Firestore may report a document as missing from its own local cache before
 * the server has confirmed the deletion. Only a server-confirmed miss is
 * allowed to delete the Room row.
 */
internal fun documentCacheAction(exists: Boolean, isFromCache: Boolean): DocumentCacheAction =
    when {
        exists -> DocumentCacheAction.Upsert
        isFromCache -> DocumentCacheAction.KeepCachedValue
        else -> DocumentCacheAction.Delete
    }

/**
 * An empty Firestore local-cache snapshot must not wipe a good Room cache
 * during a cold offline start. An empty server snapshot is authoritative.
 */
internal fun shouldReplaceCache(isFromCache: Boolean, itemCount: Int): Boolean =
    !isFromCache || itemCount > 0

internal suspend fun <T> applyDocumentSnapshot(
    exists: Boolean,
    isFromCache: Boolean,
    item: () -> T,
    upsert: suspend (T) -> Unit,
    delete: suspend () -> Unit,
) {
    when (documentCacheAction(exists, isFromCache)) {
        DocumentCacheAction.Upsert -> upsert(item())
        DocumentCacheAction.Delete -> delete()
        DocumentCacheAction.KeepCachedValue -> Unit
    }
}

/**
 * Room stays the only stream the UI sees. The Firestore listener writes into
 * Room through [onSnapshot], and listener/snapshot failures are logged instead
 * of cancelling [cached]. That is what keeps the last good cache on screen.
 */
internal fun <T> observeCachedWithSnapshots(
    cached: Flow<List<T>>,
    snapshots: Flow<SnapshotBatch<T>>,
    onSnapshot: suspend (SnapshotBatch<T>) -> Unit,
    onListenerError: (Throwable) -> Unit,
    onSnapshotError: (Throwable) -> Unit,
): Flow<List<T>> = flow {
    coroutineScope {
        val listener = launch {
            snapshots
                .catch { e -> onListenerError(e) }
                .collect { batch ->
                    try {
                        onSnapshot(batch)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        onSnapshotError(e)
                    }
                }
        }
        try {
            emitAll(cached)
        } finally {
            listener.cancel()
        }
    }
}
