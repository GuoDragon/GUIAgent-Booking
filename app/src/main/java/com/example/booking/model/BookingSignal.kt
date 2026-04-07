package com.example.booking.model

data class BookingSignal(
    val signalId: String,
    val userId: String,
    val orderType: String,
    val itemId: String,
    val itemName: String,
    val totalPrice: Double,
    val currency: String = "USD",
    val guestCount: Int = 1,
    val startDate: Long,
    val endDate: Long? = null,
    val createdAt: Long
)
