package com.example.booking.model

data class TaxiRoute(
    val routeId: String,
    val pickupLocation: String,
    val destination: String,
    val vehicleType: String,
    val maxPassengers: Int,
    val price: Double,
    val currency: String = "USD",
    val estimatedDurationMinutes: Int
)
