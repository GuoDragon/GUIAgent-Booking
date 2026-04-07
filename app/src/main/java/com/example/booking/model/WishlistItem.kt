package com.example.booking.model

data class WishlistItem(
    val wishlistId: String,
    val userId: String,
    val itemType: String,
    val itemId: String,
    val savedDate: Long
)
