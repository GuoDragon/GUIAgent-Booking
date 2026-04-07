package com.example.booking.model

data class WishlistSignal(
    val signalId: String,
    val userId: String,
    val actionType: String,
    val itemType: String,
    val itemId: String,
    val occurredAt: Long
)
