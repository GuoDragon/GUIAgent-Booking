package com.example.booking.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.booking.ui.components.BookingBottomBar
import com.example.booking.ui.theme.BookingWhite

@Composable
fun BookingApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in BookingRoutes.topLevelRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BookingWhite,
        bottomBar = {
            if (showBottomBar) {
                BookingBottomBar(
                    currentRoute = currentRoute,
                    destinations = bookingBottomDestinations,
                    onDestinationSelected = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        BookingNavGraph(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        )
    }
}
