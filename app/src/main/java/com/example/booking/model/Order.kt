package com.example.booking.model

data class Order(
    val orderId: String,
    val userId: String,
    val orderType: String,
    val status: String,
    val itemId: String,
    val itemName: String,
    val bookingDate: Long,
    val startDate: Long,
    val endDate: Long? = null,
    val totalPrice: Double,
    val currency: String = "USD",
    val guestCount: Int = 1
)
