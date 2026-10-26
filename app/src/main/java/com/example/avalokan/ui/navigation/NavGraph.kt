package com.example.avalokan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.avalokan.navigateTab
import com.example.avalokan.ui.detail.EventDetailScreen
import com.example.avalokan.ui.detail.PlaceDetailScreen
import com.example.avalokan.ui.detail.StoryDetailScreen
import com.example.avalokan.ui.discover.DiscoverScreen
import com.example.avalokan.ui.events.EventScreen
import com.example.avalokan.ui.home.AvalokanHome
import com.example.avalokan.ui.auth.LoginScreen
import com.example.avalokan.ui.navigation.NavDestination.Discover
import com.example.avalokan.ui.navigation.NavDestination.EditProfile
import com.example.avalokan.ui.navigation.NavDestination.EventDetail
import com.example.avalokan.ui.navigation.NavDestination.Home
import com.example.avalokan.ui.navigation.NavDestination.Events
import com.example.avalokan.ui.navigation.NavDestination.Login
import com.example.avalokan.ui.navigation.NavDestination.PlaceDetail
import com.example.avalokan.ui.navigation.NavDestination.Profile
import com.example.avalokan.ui.navigation.NavDestination.Settings
import com.example.avalokan.ui.navigation.NavDestination.StoryDetail
import com.example.avalokan.ui.profile.EditProfileScreen
import com.example.avalokan.ui.profile.ProfileScreen
import com.example.avalokan.ui.profile.ProfileViewModel
import com.example.avalokan.ui.settings.SettingsScreen
import com.example.avalokan.ui.settings.SettingsViewModel

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
            AvalokanHome(
                onStoryClick = { id -> navController.navigate(StoryDetail.createRoute(id)) },
                onPlaceClick = { id -> navController.navigate(PlaceDetail.createRoute(id)) },
                onExploreEventsClick = { navController.navigateTab(Events.route) }
            )
        }

        composable(route = Discover.route) {
            DiscoverScreen(
                onPlaceClick = { id -> navController.navigate(PlaceDetail.createRoute(id)) }
            )
        }

        composable(route = Events.route){
            EventScreen(
                onEventClick = { id -> navController.navigate(EventDetail.createRoute(id)) }
            )
        }

        composable(route = Profile.route){
            ProfileScreen(
                onSettingsClick = { navController.navigate(Settings.route) }
            )
        }

        composable(route = Settings.route){
            val settingsViewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                onBackClick = { navController.navigateUp() },
                onEditProfileClick = { navController.navigate(EditProfile.route) },
                onLoginClick = { navController.navigate(Login.route) },
                onLogoutClick = {
                    settingsViewModel.signOut()
                    navController.navigate(Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(route = EditProfile.route){ entry ->
            // Shared with Profile so edits reflect immediately on return.
            // Keyed on this destination's back stack entry so the reference is
            // created once per entry and stays valid across recompositions.
            val profileEntry = remember(entry) {
                navController.getBackStackEntry(Profile.route)
            }
            EditProfileScreen(
                onBackClick = { navController.navigateUp() },
                onSaved = { navController.navigateUp() },
                viewModel = hiltViewModel<ProfileViewModel>(profileEntry)
            )
        }

        composable(
            route = StoryDetail.route,
            arguments = listOf(navArgument(StoryDetail.ARG) { type = NavType.StringType })
        ) {
            StoryDetailScreen(
                onBackClick = { navController.navigateUp() }
            )
        }

        composable(
            route = PlaceDetail.route,
            arguments = listOf(navArgument(PlaceDetail.ARG) { type = NavType.StringType })
        ) {
            PlaceDetailScreen(
                onBackClick = { navController.navigateUp() },
            )
        }

        composable(
            route = EventDetail.route,
            arguments = listOf(navArgument(EventDetail.ARG) { type = NavType.StringType })
        ) {
            EventDetailScreen(
                onBackClick = { navController.navigateUp() },
            )
        }

        composable(route = Login.route){
            LoginScreen(
                onLoggedIn = {
                    navController.navigate(Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}