package com.example.booking.model

data class User(
    val userId: String,
    val firstName: String,
    val lastName: String,
    val email: String? = null,
    val phone: String? = null,
    val avatar: String? = null,
    val nationality: String? = null
)
