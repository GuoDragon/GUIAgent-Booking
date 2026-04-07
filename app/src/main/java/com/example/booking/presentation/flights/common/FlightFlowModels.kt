package com.example.booking.presentation.flights.common

import java.time.LocalDate

enum class FlightTripType(val label: String) {
    RoundTrip("Round-trip"),
    OneWay("One-way"),
    MultiCity("Multi-city")
}

enum class FlightSortOption(val title: String) {
    Best("Best"),
    Cheapest("Cheapest"),
    Fastest("Fastest")
}

enum class FlightFareOption(
    val title: String,
    val priceOffset: Double,
    val baggageLabel: String,
    val changePolicy: String,
    val refundPolicy: String
) {
    Basic(
        title = "Economy Basic",
        priceOffset = 0.0,
        baggageLabel = "1 carry-on bag (7 kg)",
        changePolicy = "No flight change allowed",
        refundPolicy = "No refund if you cancel"
    ),
    Value(
        title = "Economy Value",
        priceOffset = 120.0,
        baggageLabel = "1 carry-on bag + 1 checked bag (25 kg)",
        changePolicy = "Flight change allowed for a fee",
        refundPolicy = "No refund if you cancel"
    ),
    Comfort(
        title = "Economy Comfort",
        priceOffset = 260.0,
        baggageLabel = "1 carry-on bag + 1 checked bag (30 kg)",
        changePolicy = "Seat choice and flight change for a fee",
        refundPolicy = "Partial refund if you cancel"
    )
}

enum class FlightFlexibleTicketOption(val title: String, val priceOffset: Double) {
    Standard("Standard ticket", 0.0),
    Flexible("Flexible ticket", 90.0)
}

data class FlightFilterState(
    val selectedStops: Set<Int> = emptySet(),
    val selectedAirlineIds: Set<String> = emptySet(),
    val directOnly: Boolean = false
)

data class FlightDraft(
    val tripType: FlightTripType = FlightTripType.RoundTrip,
    val departureAirportCode: String = "LHR",
    val arrivalAirportCode: String = "JFK",
    val departureDate: LocalDate = LocalDate.now().plusDays(14),
    val returnDate: LocalDate = LocalDate.now().plusDays(21),
    val adultCount: Int = 1,
    val cabinClass: String = "Economy",
    val directFlightsOnly: Boolean = false,
    val sortOption: FlightSortOption = FlightSortOption.Best,
    val filterState: FlightFilterState = FlightFilterState(),
    val selectedOutboundFlightId: String? = null,
    val selectedReturnFlightId: String? = null,
    val selectedFareOption: FlightFareOption = FlightFareOption.Basic,
    val noAdditionalBaggage: Boolean = false,
    val selectedMeal: String = "No preference",
    val flexibleTicketOption: FlightFlexibleTicketOption = FlightFlexibleTicketOption.Standard,
    val travelerFirstName: String = "",
    val travelerLastName: String = "",
    val travelerGender: String = "",
    val contactEmail: String = "",
    val contactPhoneCountryCode: String = "+1",
    val contactPhoneNumber: String = "",
    val lastCreatedOrderId: String? = null
)

data class FlightLeg(
    val flightId: String?,
    val airlineId: String,
    val airlineName: String,
    val flightNumber: String,
    val departureAirportCode: String,
    val departureAirportName: String,
    val arrivalAirportCode: String,
    val arrivalAirportName: String,
    val departureTime: Long,
    val arrivalTime: Long,
    val durationMinutes: Int,
    val stops: Int,
    val cabinClass: String,
    val price: Double,
    val currency: String,
    val synthetic: Boolean = false
)

data class FlightItinerary(
    val itineraryId: String,
    val outbound: FlightLeg,
    val returnLeg: FlightLeg?,
    val totalPrice: Double,
    val currency: String
)
