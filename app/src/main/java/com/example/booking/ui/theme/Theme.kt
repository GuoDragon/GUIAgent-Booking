package com.example.booking.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BookingBlue,
    secondary = BookingBlueLight,
    tertiary = BookingYellow,
    background = Color(0xFF1C1B1F),
    surface = Color(0xFF1C1B1F)
)

private val LightColorScheme = lightColorScheme(
    primary = BookingBlue,
    secondary = BookingBlueLight,
    tertiary = BookingYellow,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = BookingTextPrimary,
    onSurface = BookingTextPrimary
)

@Composable
fun BookingTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
