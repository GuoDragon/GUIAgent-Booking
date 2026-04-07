package com.example.booking.presentation.carrentals.common

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object CarRentalDraftStore {

    private var draft by mutableStateOf(CarRentalDraft())

    fun snapshot(): CarRentalDraft = draft

    fun update(transform: (CarRentalDraft) -> CarRentalDraft) {
        draft = transform(draft)
    }

    fun prepareForSearch() {
        draft = draft.copy(
            selectedCarId = null,
            lastCreatedOrderId = null
        )
    }

    fun selectCar(carId: String) {
        draft = draft.copy(selectedCarId = carId)
    }

    fun markBookingComplete(orderId: String) {
        draft = draft.copy(lastCreatedOrderId = orderId)
    }
}
