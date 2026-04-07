package com.example.booking.presentation.carrentals.search

import java.time.LocalDateTime

interface CarRentalDateContract {
    interface View {
        fun showState(state: CarRentalDateUiState)
    }

    interface Presenter {
        fun loadData()
        fun applyDates(pickupDateTime: LocalDateTime, returnDateTime: LocalDateTime)
    }
}

data class CarRentalDateUiState(
    val pickupDateTime: LocalDateTime,
    val returnDateTime: LocalDateTime,
    val calendarMonths: List<java.time.LocalDate>
)
