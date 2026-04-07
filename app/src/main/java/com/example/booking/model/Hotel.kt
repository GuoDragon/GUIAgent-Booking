package com.example.booking.model

data class Hotel(
    val hotelId: String,
    val name: String,
    val city: String,
    val country: String,
    val address: String,
    val starRating: Int,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val pricePerNight: Double,
    val currency: String = "USD",
    val amenities: List<String> = emptyList(),
    val imageUrl: String? = null,
    val latitude: Double,
    val longitude: Double
)
