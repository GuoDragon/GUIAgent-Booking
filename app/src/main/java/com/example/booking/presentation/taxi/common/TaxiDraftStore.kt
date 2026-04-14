package com.example.booking.presentation.taxi.common

import java.time.LocalDateTime

enum class TaxiTripType(val label: String) {
    OneWay("One-way"),
    RoundTrip("Round-trip")
}

data class TaxiDraft(
    val tripType: TaxiTripType = TaxiTripType.OneWay,
    val pickupLocation: String = "London Heathrow Airport",
    val destination: String = "London Heathrow Airport Hilton Hotel",
    val pickupDateTime: LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0),
    val returnDateTime: LocalDateTime = LocalDateTime.now().withSecond(0).withNano(0).plusHours(6),
    val returnDateTimeConfirmed: Boolean = false,
    val passengerCount: Int = 2,
    val selectedRouteId: String? = null,
    val selectedFlightId: String? = null,
    val contactName: String = "Alex Johnson",
    val contactEmail: String = "alex@example.com",
    val contactPhone: String = "+1 555 0148",
    val departureAirportQuery: String = "",
    val flightNumber: String = "MU721",
    val completedOrderId: String? = null
)

object TaxiDraftStore {
    private var draft = TaxiDraft()

    fun snapshot(): TaxiDraft = draft

    fun update(transform: (TaxiDraft) -> TaxiDraft) {
        draft = transform(draft)
    }

    fun prepareForSearch() {
        draft = draft.copy(selectedRouteId = null, selectedFlightId = null, completedOrderId = null)
    }

    fun selectRoute(routeId: String) {
        draft = draft.copy(selectedRouteId = routeId)
    }

    fun markBookingComplete(orderId: String) {
        draft = draft.copy(completedOrderId = orderId)
    }
}
