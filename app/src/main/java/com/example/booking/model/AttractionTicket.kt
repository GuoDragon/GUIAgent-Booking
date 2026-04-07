package com.example.booking.model

data class AttractionTicket(
    val ticketId: String,
    val attractionId: String,
    val ticketType: String,
    val description: String,
    val price: Double,
    val currency: String = "USD",
    val validDays: Int = 1,
    val cancellable: Boolean = true
)
