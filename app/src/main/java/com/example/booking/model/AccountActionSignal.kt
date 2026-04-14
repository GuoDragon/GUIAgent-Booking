package com.example.booking.model

data class AccountActionSignal(
    val signalId: String,
    val userId: String,
    val actionType: String,
    val occurredAt: Long,
    val displayMessage: String = "",
    val amount: Double? = null,
    val currency: String? = null,
    val affectedOrderCount: Int? = null,
    val extra: Map<String, String> = emptyMap()
)
