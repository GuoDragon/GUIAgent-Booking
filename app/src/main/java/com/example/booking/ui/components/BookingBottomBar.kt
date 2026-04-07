package com.example.booking.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.booking.navigation.BookingBottomDestination
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun BookingBottomBar(
    currentRoute: String?,
    destinations: List<BookingBottomDestination>,
    onDestinationSelected: (BookingBottomDestination) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = BookingWhite,
            shadowElevation = 10.dp
        ) {
            NavigationBar(
                containerColor = BookingWhite,
                tonalElevation = 0.dp
            ) {
                destinations.forEach { destination ->
                    val selected = currentRoute == destination.route
                    NavigationBarItem(
                        selected = selected,
                        onClick = { onDestinationSelected(destination) },
                        icon = {
                            Icon(
                                imageVector = if (selected) {
                                    destination.selectedIcon
                                } else {
                                    destination.unselectedIcon
                                },
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(text = destination.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = BookingBlueLight,
                            selectedTextColor = BookingBlueLight,
                            indicatorColor = BookingBlueLight.copy(alpha = 0.12f),
                            unselectedIconColor = BookingTextSecondary,
                            unselectedTextColor = BookingTextSecondary
                        )
                    )
                }
            }
        }
    }
}
