package com.example.avalokan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.avalokan.ui.detail.EventDetailScreen
import com.example.avalokan.ui.detail.PlaceDetailScreen
import com.example.avalokan.ui.detail.StoryDetailScreen
import com.example.avalokan.ui.discover.DiscoverScreen
import com.example.avalokan.ui.events.EventScreen
import com.example.avalokan.ui.home.AvalokanHome
import com.example.avalokan.ui.navigation.NavDestination.Discover
import com.example.avalokan.ui.navigation.NavDestination.EventDetail
import com.example.avalokan.ui.navigation.NavDestination.Home
import com.example.avalokan.ui.navigation.NavDestination.Events
import com.example.avalokan.ui.navigation.NavDestination.PlaceDetail
import com.example.avalokan.ui.navigation.NavDestination.Profile
import com.example.avalokan.ui.navigation.NavDestination.Settings
import com.example.avalokan.ui.navigation.NavDestination.StoryDetail
import com.example.avalokan.ui.profile.ProfileScreen
import com.example.avalokan.ui.settings.SettingsScreen

@Composable
fun AvalokanNavHost(
    navController: NavHostController,
    modifier : Modifier = Modifier
){
    NavHost(
        navController = navController,
        startDestination = Home.route,
        modifier = modifier
    ){
        composable(route = Home.route){
            AvalokanHome()
        }

        composable(route = Discover.route) {
            DiscoverScreen()
        }

        composable(route = Events.route){
            EventScreen()
        }

        composable(route = Profile.route){
            ProfileScreen(
                onSettingsClick = { navController.navigate(Settings.route) }
            )
        }

        composable(route = Settings.route){
            SettingsScreen(
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(
            route = StoryDetail.route,
            arguments = listOf(navArgument(StoryDetail.ARG) { type = NavType.StringType })
        ) { entry ->
            StoryDetailScreen(
                storyId = entry.arguments?.getString(StoryDetail.ARG).orEmpty(),
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(
            route = PlaceDetail.route,
            arguments = listOf(navArgument(PlaceDetail.ARG) { type = NavType.StringType })
        ) { entry ->
            PlaceDetailScreen(
                placeId = entry.arguments?.getString(PlaceDetail.ARG).orEmpty(),
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(
            route = EventDetail.route,
            arguments = listOf(navArgument(EventDetail.ARG) { type = NavType.StringType })
        ) { entry ->
            EventDetailScreen(
                eventId = entry.arguments?.getString(EventDetail.ARG).orEmpty(),
                onBackClick = { navController.navigateUp() }
            )
        }
    }
}