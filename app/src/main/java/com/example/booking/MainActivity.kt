package com.example.booking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.booking.data.DataRepository
import com.example.booking.navigation.BookingApp
import com.example.booking.ui.theme.BookingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataRepository.initializeRuntimeFiles(applicationContext)
        enableEdgeToEdge()
        setContent {
            BookingTheme(darkTheme = false) {
                BookingApp()
            }
        }
    }
}
