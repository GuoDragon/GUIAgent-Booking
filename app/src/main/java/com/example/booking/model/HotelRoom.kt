package com.example.booking.model

data class HotelRoom(
    val roomId: String,
    val hotelId: String,
    val roomType: String,
    val description: String,
    val maxGuests: Int,
    val pricePerNight: Double,
    val currency: String = "USD",
    val bedType: String,
    val amenities: List<String> = emptyList(),
    val available: Boolean = true,
    val imageUrl: String? = null
)
