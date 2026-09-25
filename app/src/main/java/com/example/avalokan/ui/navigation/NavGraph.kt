package com.example.avalokan.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.avalokan.ui.discover.DiscoverScreen
import com.example.avalokan.ui.events.EventScreen
import com.example.avalokan.ui.home.AvalokanHome
import com.example.avalokan.ui.navigation.NavDestination.Discover
import com.example.avalokan.ui.navigation.NavDestination.Home
import com.example.avalokan.ui.navigation.NavDestination.Events
import com.example.avalokan.ui.navigation.NavDestination.Profile
import com.example.avalokan.ui.profile.ProfileScreen

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
            ProfileScreen()
        }
    }
}