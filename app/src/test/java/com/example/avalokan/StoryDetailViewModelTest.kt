package com.example.avalokan

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.avalokan.ui.common.ContentState
import com.example.avalokan.ui.detail.StoryDetailViewModel
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
class StoryDetailViewModelTest {

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
        storyId: String = "boudhanath",
        repository: FakeStoryRepository = FakeStoryRepository(),
    ) = StoryDetailViewModel(
        repository,
        SavedStateHandle(mapOf(NavDestination.StoryDetail.ARG to storyId)),
    )

    @Test
    fun `story begins loading and becomes content`() = runTest {
        val vm = viewModel()
        vm.storyState.test {
            assertEquals(ContentState.Loading, awaitItem())
            assertEquals(ContentState.Content(TestStories.first()), awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a missing story becomes empty after the load window`() = runTest {
        val vm = viewModel(storyId = "does-not-exist")
        vm.storyState.test {
            assertEquals(ContentState.Loading, awaitItem())
            expectNoEvents()
            advanceUntilIdle()
            assertEquals(ContentState.Empty, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a failed story stream becomes an error`() = runTest {
        val vm = viewModel(
            repository = FakeStoryRepository(
                storyFlow = flow { throw IOException("offline") }
            )
        )
        vm.storyState.test {
            assertEquals(ContentState.Loading, awaitItem())
            advanceUntilIdle()
            assertTrue(awaitItem() is ContentState.Error)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
