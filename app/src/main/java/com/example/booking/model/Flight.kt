package com.example.booking.model

data class Flight(
    val flightId: String,
    val airlineId: String,
    val flightNumber: String,
    val departureAirportCode: String,
    val arrivalAirportCode: String,
    val departureTime: Long,
    val arrivalTime: Long,
    val durationMinutes: Int,
    val price: Double,
    val currency: String = "USD",
    val cabinClass: String,
    val seatsAvailable: Int,
    val stops: Int = 0
)
