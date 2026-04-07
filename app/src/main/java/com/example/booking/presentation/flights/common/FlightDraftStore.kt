package com.example.booking.presentation.flights.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object FlightDraftStore {

    private var draft by mutableStateOf(FlightDraft())

    fun snapshot(): FlightDraft = draft

    fun update(transform: (FlightDraft) -> FlightDraft) {
        draft = transform(draft)
    }

    fun prepareForSearch() {
        draft = draft.copy(
            selectedOutboundFlightId = null,
            selectedReturnFlightId = null,
            lastCreatedOrderId = null
        )
    }

    fun selectItinerary(outboundFlightId: String?, returnFlightId: String?) {
        draft = draft.copy(
            selectedOutboundFlightId = outboundFlightId,
            selectedReturnFlightId = returnFlightId
        )
    }

    fun markBookingComplete(orderId: String) {
        draft = draft.copy(lastCreatedOrderId = orderId)
    }
}
