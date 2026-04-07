package com.example.booking.presentation.flights.search

import java.time.LocalDate

interface FlightDateContract {
    interface View {
        fun showState(state: FlightDateUiState)
    }

    interface Presenter {
        fun loadData()
        fun applyDates(departureDate: LocalDate, returnDate: LocalDate)
    }
}

data class FlightDateUiState(
    val departureDate: LocalDate,
    val returnDate: LocalDate,
    val calendarMonths: List<LocalDate>
)
