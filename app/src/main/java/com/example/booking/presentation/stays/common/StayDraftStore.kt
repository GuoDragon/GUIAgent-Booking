package com.example.booking.presentation.stays.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object StayDraftStore {

    private var draft by mutableStateOf(StayDraft())

    fun snapshot(): StayDraft = draft

    fun update(transform: (StayDraft) -> StayDraft) {
        draft = transform(draft)
    }

    fun prepareForSearch() {
        draft = draft.copy(
            selectedHotelId = null,
            selectedRoomId = null,
            lastCreatedOrderId = null
        )
    }

    fun selectHotel(hotelId: String) {
        draft = draft.copy(
            selectedHotelId = hotelId,
            selectedRoomId = null
        )
    }

    fun selectRoom(roomId: String) {
        draft = draft.copy(selectedRoomId = roomId)
    }

    fun markBookingComplete(orderId: String) {
        draft = draft.copy(lastCreatedOrderId = orderId)
    }
}
