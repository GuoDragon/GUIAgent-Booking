package com.example.booking.model

data class HotelReviewSignal(
    val signalId: String,
    val userId: String,
    val orderId: String,
    val hotelId: String,
    val hotelName: String,
    val rating: Int,
    val comment: String,
    val updatedAt: Long
)
