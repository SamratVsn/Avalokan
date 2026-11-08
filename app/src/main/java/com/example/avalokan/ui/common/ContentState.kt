package com.example.avalokan.ui.common

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * The four states a content screen can be in.
 *
 * [Loading] means the repository has not produced a usable value yet. It is
 * deliberately separate from [Empty], which means the repository answered and
 * the answer contained nothing. Collapsing the two is what made a cold start
 * look like an empty catalogue, and an empty catalogue look like a failure.
 */
sealed interface ContentState<out T> {
    data object Loading : ContentState<Nothing>
    data class Content<T>(val data: T) : ContentState<T>
    data object Empty : ContentState<Nothing>
    data class Error(val message: String? = null) : ContentState<Nothing>
}

/** The content payload, or null while loading, empty or failed. */
val <T> ContentState<T>.dataOrNull: T?
    get() = (this as? ContentState.Content)?.data

/**
 * Turns a nullable single-item stream into a [ContentState].
 *
 * Room emits its cache immediately. On a cold start that first emission may be
 * `null` while Firestore is still answering, so a null is held as [Loading]
 * until [timeoutMillis] has passed. A non-null value settles the state
 * immediately. If the stream fails before any value arrives the state becomes
 * [Error]; after content has arrived, a later failure leaves the last good
 * content on screen instead of replacing it with an error.
 */
fun <T : Any> Flow<T?>.asItemContentState(
    timeoutMillis: Long = CONTENT_LOADING_TIMEOUT_MS,
): Flow<ContentState<T>> = channelFlow {
    send(ContentState.Loading)

    var sawEmission = false
    var sawContent = false
    var settled = false

    val timeout = launch {
        delay(timeoutMillis)
        settled = true
        if (!sawContent) {
            // An empty cache emission means the repository answered with no
            // items; no emission at all means it never answered.
            send(if (sawEmission) ContentState.Empty else ContentState.Error())
        }
    }

    try {
        collect { value ->
            sawEmission = true
            if (value == null) {
                if (settled) send(ContentState.Empty)
            } else {
                sawContent = true
                settled = true
                timeout.cancel()
                send(ContentState.Content(value))
            }
        }
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        timeout.cancel()
        if (!sawContent) send(ContentState.Error(e.message))
    }
}

/**
 * Collection variant of [asItemContentState]. An empty list becomes [Empty]
 * once the initial load window has passed, so a cached-empty cold start does
 * not flash an empty-state message before the network answers.
 */
fun <T : Any> Flow<List<T>>.asListContentState(
    timeoutMillis: Long = CONTENT_LOADING_TIMEOUT_MS,
): Flow<ContentState<List<T>>> =
    map { items -> items.takeIf { it.isNotEmpty() } }
        .asItemContentState(timeoutMillis)
