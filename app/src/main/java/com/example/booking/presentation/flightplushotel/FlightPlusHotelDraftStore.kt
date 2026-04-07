package com.example.booking.presentation.flightplushotel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.time.LocalDate

enum class FlightHotelTripType(val label: String) {
    OneWay("One-way"),
    RoundTrip("Round-trip")
}

data class FlightPlusHotelDraft(
    val tripType: FlightHotelTripType = FlightHotelTripType.OneWay,
    val departureAirportCode: String = "LHR",
    val arrivalAirportCode: String = "JFK",
    val departureDate: LocalDate = LocalDate.now().plusDays(14),
    val passengerCount: Int = 2,
    val cabinClass: String = "Economy",
    val roomCount: Int = 1,
    val differentCityAndDates: Boolean = true,
    val stayDestinationQuery: String = "New York",
    val checkInDate: LocalDate = LocalDate.now().plusDays(14),
    val checkOutDate: LocalDate = LocalDate.now().plusDays(16)
)

object FlightPlusHotelDraftStore {

    private var draft by mutableStateOf(FlightPlusHotelDraft())

    fun snapshot(): FlightPlusHotelDraft = draft

    fun update(transform: (FlightPlusHotelDraft) -> FlightPlusHotelDraft) {
        draft = transform(draft)
    }
}
