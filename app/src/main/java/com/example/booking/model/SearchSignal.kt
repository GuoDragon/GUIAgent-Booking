package com.example.booking.model

data class SearchSignal(
    val signalId: String,
    val searchType: String,
    val destination: String? = null,
    val checkInDate: Long? = null,
    val checkOutDate: Long? = null,
    val guestCount: Int? = null,
    val occurredAt: Long
)
