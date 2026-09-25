package com.example.avalokan

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.avalokan.ui.navigation.AvalokanNavHost
import com.example.avalokan.ui.navigation.NavDestination
import com.example.avalokan.ui.theme.AvalokanTheme

sealed class BottomNavItem(
    val destination: NavDestination,
    @DrawableRes val icon: Int
){
    object Home: BottomNavItem(NavDestination.Home, R.drawable.home)
    object Discover : BottomNavItem(NavDestination.Discover, R.drawable.discover)
    object Events : BottomNavItem(NavDestination.Events, R.drawable.event)
    object Profile : BottomNavItem(NavDestination.Profile, R.drawable.profile)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Discover,
    BottomNavItem.Events,
    BottomNavItem.Profile
)

@Composable
fun AvalokanApp(
    navController: NavHostController = rememberNavController()
){
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Home.route

    val showBottomBar = currentRoute in bottomNavItems.map { it.destination.route }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0 , 0, 0),
        bottomBar = {
            if(showBottomBar){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    AvalokanBottomNavigation(navController = navController)
                }
            }
        }
    ) { innerPadding ->
        AvalokanNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun AvalokanBottomNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        tonalElevation = 8.dp,
        shadowElevation = 16.dp
    ){
        NavigationBar(
            containerColor = Color.Transparent,
            modifier = Modifier.height(80.dp),
            windowInsets = WindowInsets(0,0,0,0)
        ){
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: NavDestination.Home.route

            bottomNavItems.forEach { item ->
                val selected = currentRoute == item.destination.route

                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = painterResource(item.icon),
                            contentDescription = item.destination.route,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = item.destination.titleRes),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if(selected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    selected = selected,
                    onClick = {
                        if (currentRoute != item.destination.route) {
                            navController.navigate(item.destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Preview
@Composable
private fun NavBarPreview(){
    AvalokanTheme() {
        AvalokanBottomNavigation(navController = rememberNavController())
    }
}