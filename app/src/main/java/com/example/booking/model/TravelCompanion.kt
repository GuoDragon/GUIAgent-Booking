package com.example.booking.model

data class TravelCompanion(
    val companionId: String,
    val userId: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: String? = null,
    val nationality: String? = null,
    val passportNumber: String? = null,
    val email: String? = null
)
