package com.example.avalokan

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.detail.EventDetailViewModel
import com.example.avalokan.ui.navigation.NavDestination
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class EventDetailViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun viewModel(
        eventId: String = "dashain-festival",
        repository: FakeEventRepository = FakeEventRepository(),
    ) = EventDetailViewModel(
        repository,
        FakePreferencesStore(),
        SavedStateHandle(mapOf(NavDestination.EventDetail.ARG to eventId)),
    )

    @Test
    fun `event begins loading and becomes content`() = runTest {
        val vm = viewModel()
        vm.eventState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(TestEvents.first()), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a missing event becomes empty after the load window`() = runTest {
        val vm = viewModel(eventId = "does-not-exist")
        vm.eventState.test {
            assertEquals(ContentState.Loading, awaitItem())
            expectNoEvents()
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a failed event stream becomes an error`() = runTest {
        val vm = viewModel(
            repository = FakeEventRepository(
                eventFlow = flow { throw IOException("offline") }
            )
        )
        vm.eventState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is ContentState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
