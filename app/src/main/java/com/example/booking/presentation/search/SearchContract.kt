package com.example.booking.presentation.search

import android.content.Context
import com.example.booking.presentation.flightplushotel.FlightHotelTripType
import com.example.booking.presentation.taxi.common.TaxiTripType
import com.example.booking.presentation.flights.common.FlightTripType

interface SearchContract {
    interface View {
        fun showState(state: SearchUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applyFeaturedDestination(destination: String)
        fun submitStaySearch(context: Context)
        fun submitFlightSearch(context: Context)
        fun submitFlightHotelSearch(context: Context)
        fun submitCarRentalSearch(context: Context)
        fun submitTaxiSearch(context: Context)
        fun submitAttractionSearch(context: Context)
    }
}

enum class SearchProduct(val title: String) {
    Stays("Stays"),
    Flights("Flights"),
    FlightHotel("Flight + Hotel"),
    CarRental("Car rental"),
    Taxi("Taxi"),
    Attractions("Attractions")
}

enum class SearchRecentType {
    CarRental,
    Flight
}

data class SearchUiState(
    val stayDestinationLabel: String = "Destination, landmark, or property",
    val stayDateLabel: String = "",
    val stayGuestLabel: String = "",
    val flightTripType: FlightTripType = FlightTripType.RoundTrip,
    val flightDepartureLabel: String = "",
    val flightArrivalLabel: String = "",
    val flightDateLabel: String = "",
    val flightPassengerLabel: String = "",
    val flightDirectOnly: Boolean = false,
    val flightHotelTripType: FlightHotelTripType = FlightHotelTripType.OneWay,
    val flightHotelDepartureLabel: String = "",
    val flightHotelArrivalLabel: String = "",
    val flightHotelDepartureDateLabel: String = "",
    val flightHotelPassengerLabel: String = "",
    val flightHotelStayDestinationLabel: String = "",
    val flightHotelStayDateLabel: String = "",
    val flightHotelDifferentCityAndDates: Boolean = true,
    val carReturnToSameLocation: Boolean = true,
    val carPickupLocationLabel: String = "",
    val carDateLabel: String = "",
    val carDriverAgeLabel: String = "",
    val taxiTripType: TaxiTripType = TaxiTripType.OneWay,
    val taxiPickupLocationLabel: String = "",
    val taxiDestinationLabel: String = "",
    val taxiTimeLabel: String = "",
    val taxiPassengerLabel: String = "",
    val taxiRecentItems: List<TaxiRecentItem> = emptyList(),
    val attractionDestinationLabel: String = "",
    val attractionDateLabel: String = "",
    val attractionCards: List<AttractionHighlightCard> = emptyList(),
    val recentItems: List<RecentSearchItem> = emptyList(),
    val destinationCards: List<DestinationHighlight> = emptyList(),
    val popularCarCompanies: List<String> = emptyList(),
    val continueBookingCard: ContinueBookingCardUiModel? = null,
    val flightAirports: List<AirportOptionUiModel> = emptyList(),
    val flightCabinOptions: List<String> = listOf("Economy", "Premium Economy", "Business", "First"),
    val carPickupLocations: List<String> = emptyList(),
    val carDriverAgeOptions: List<String> = listOf("18-24", "25-29", "30-65", "66+")
)

data class RecentSearchItem(
    val type: SearchRecentType,
    val title: String,
    val subtitle: String,
    val meta: String
)

data class DestinationHighlight(
    val title: String,
    val subtitle: String,
    val caption: String
)

data class ContinueBookingCardUiModel(
    val title: String,
    val subtitle: String,
    val footnote: String
)

data class TaxiRecentItem(
    val title: String,
    val subtitle: String,
    val meta: String
)

data class AttractionHighlightCard(
    val title: String,
    val subtitle: String,
    val priceText: String,
    val badgeText: String
)

data class AirportOptionUiModel(
    val code: String,
    val label: String,
    val city: String
)
