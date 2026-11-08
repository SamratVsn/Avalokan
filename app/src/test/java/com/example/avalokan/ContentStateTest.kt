package com.example.avalokan

import app.cash.turbine.test
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.common.asListContentState
import com.example.avalokan.ui.common.asItemContentState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ContentStateTest {

    @Test
    fun `a non-empty collection is content immediately`() = runTest {
        flowOf(listOf(1, 2)).asListContentState().test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(listOf(1, 2)), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `an empty collection is held as loading until the timeout, then empty`() = runTest {
        flowOf(emptyList<Int>()).asListContentState().test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `a stream that never emits becomes an error after the timeout`() = runTest {
        MutableSharedFlow<List<Int>>().asListContentState().test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is ContentState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a stream that fails before content becomes an error`() = runTest {
        flow<List<Int>> { throw IOException("offline") }.asListContentState().test {
            assertEquals(ContentState.Loading, awaitItem())
            assertTrue(awaitItem() is ContentState.Error)
            awaitComplete()
        }
    }

    @Test
    fun `a single item becomes content immediately`() = runTest {
        flowOf<Int?>(7).asItemContentState().test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(7), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `a missing single item becomes empty after the timeout`() = runTest {
        flowOf<Int?>(null).asItemContentState().test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            awaitComplete()
        }
    }
}
