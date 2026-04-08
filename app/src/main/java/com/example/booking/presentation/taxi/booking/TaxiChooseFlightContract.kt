package com.example.booking.presentation.taxi.booking

import android.content.Context

interface TaxiChooseFlightContract {
    interface View {
        fun showState(state: TaxiChooseFlightUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applyFilters(
            selectedAirlineIds: Set<String>,
            selectedDepartureSlots: Set<FlightTimeSlot>,
            selectedArrivalSlots: Set<FlightTimeSlot>
        )
        fun resetFilters()
        fun selectFlight(flightId: String)
    }
}

data class TaxiChooseFlightUiState(
    val title: String = "Choose your flight",
    val cards: List<TaxiFlightCardUiModel> = emptyList(),
    val airlineOptions: List<TaxiAirlineOptionUiModel> = emptyList(),
    val selectedAirlineIds: Set<String> = emptySet(),
    val selectedDepartureSlots: Set<FlightTimeSlot> = emptySet(),
    val selectedArrivalSlots: Set<FlightTimeSlot> = emptySet()
)

data class TaxiFlightCardUiModel(
    val flightId: String,
    val title: String,
    val departureLabel: String,
    val arrivalLabel: String,
    val departureTimeLabel: String,
    val arrivalTimeLabel: String,
    val departureDateLabel: String,
    val arrivalDateLabel: String
)

data class TaxiAirlineOptionUiModel(
    val airlineId: String,
    val name: String
)

enum class FlightTimeSlot(val label: String, val startHour: Int, val endHour: Int) {
    Night("00:00-05:59", 0, 5),
    Morning("06:00-11:59", 6, 11),
    Afternoon("12:00-17:59", 12, 17),
    Evening("18:00-23:59", 18, 23);

    fun contains(hour: Int): Boolean = hour in startHour..endHour
}
