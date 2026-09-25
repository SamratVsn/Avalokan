package com.example.avalokan.ui.navigation

import androidx.annotation.StringRes
import com.example.avalokan.R

sealed interface NavDestination {
    val route: String
    @get:StringRes val titleRes: Int

    data object Home : NavDestination {
        override val route = "home"
        override val titleRes = R.string.home
    }
    data object Discover : NavDestination {
        override val route = "discover"
        override val titleRes = R.string.discover
    }
    data object Events : NavDestination {
        override val route = "events"
        override val titleRes = R.string.events
    }
    data object Profile : NavDestination {
        override val route = "profile"
        override val titleRes = R.string.profile
    }

    data object EventDetail : NavDestination {
        const val ARG = "eventId"
        override val route = "event_detail/{eventId}"
        override val titleRes = R.string.event_detail
        fun createRoute(eventId: String) = "event_detail/$eventId"
    }

    data object PlaceDetail : NavDestination {
        const val ARG = "placeId"
        override val route = "place_detail/{placeId}"
        override val titleRes = R.string.place_detail
        fun createRoute(placeId: String) = "place_detail/$placeId"
    }

    data object StoryDetail : NavDestination {
        const val ARG = "storyId"
        override val route = "story_detail/{storyId}"
        override val titleRes = R.string.story_detail
        fun createRoute(storyId: String) = "story_detail/$storyId"
    }
}