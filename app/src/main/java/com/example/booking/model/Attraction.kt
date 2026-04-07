package com.example.booking.model

data class Attraction(
    val attractionId: String,
    val name: String,
    val city: String,
    val country: String,
    val category: String,
    val description: String,
    val rating: Double,
    val reviewCount: Int,
    val imageUrl: String? = null,
    val fromPrice: Double,
    val currency: String = "USD"
)
