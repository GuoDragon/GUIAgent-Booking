package com.example.booking.model

data class Cruise(
    val cruiseId: String,
    val cruiseLine: String,
    val shipName: String,
    val destination: String,
    val departurePort: String,
    val durationNights: Int,
    val fromPrice: Double,
    val currency: String = "USD",
    val rating: Double,
    val imageUrl: String? = null
)
