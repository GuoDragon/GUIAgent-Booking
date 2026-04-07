package com.example.booking.model

data class CarRental(
    val carId: String,
    val companyName: String,
    val carModel: String,
    val category: String,
    val pricePerDay: Double,
    val currency: String = "USD",
    val seats: Int,
    val doors: Int,
    val transmission: String,
    val fuelType: String,
    val pickupLocation: String,
    val imageUrl: String? = null,
    val unlimitedMileage: Boolean = true,
    val freeCancellation: Boolean = true
)
