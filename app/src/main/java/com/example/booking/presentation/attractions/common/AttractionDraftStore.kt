package com.example.booking.presentation.attractions.common

import java.time.LocalDate

data class AttractionDraft(
    val destinationQuery: String = "Paris, France",
    val selectedDate: LocalDate = LocalDate.now().plusDays(1),
    val selectedAttractionId: String? = null,
    val selectedTicketId: String? = null,
    val travelerName: String = "Alex Johnson",
    val travelerEmail: String = "alex@example.com",
    val travelerPhone: String = "+1 555 0148",
    val completedOrderId: String? = null
)

object AttractionDraftStore {
    private var draft = AttractionDraft()

    fun snapshot(): AttractionDraft = draft

    fun update(transform: (AttractionDraft) -> AttractionDraft) {
        draft = transform(draft)
    }

    fun prepareForSearch() {
        draft = draft.copy(selectedAttractionId = null, selectedTicketId = null, completedOrderId = null)
    }

    fun selectAttraction(attractionId: String) {
        draft = draft.copy(selectedAttractionId = attractionId, selectedTicketId = null)
    }

    fun selectTicket(ticketId: String) {
        draft = draft.copy(selectedTicketId = ticketId)
    }

    fun markBookingComplete(orderId: String) {
        draft = draft.copy(completedOrderId = orderId)
    }
}
