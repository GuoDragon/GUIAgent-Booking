package com.example.booking.model

data class Airline(
    val airlineId: String,
    val name: String,
    val iataCode: String,
    val logoUrl: String? = null
)
