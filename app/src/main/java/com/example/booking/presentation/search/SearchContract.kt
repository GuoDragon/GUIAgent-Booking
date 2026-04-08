package com.example.booking.presentation.search

import android.content.Context
import com.example.booking.presentation.flightplushotel.FlightHotelTripType
import com.example.booking.presentation.taxi.common.TaxiTripType
import com.example.booking.presentation.flights.common.FlightTripType
import java.time.LocalDate
import java.time.LocalDateTime

interface SearchContract {
    interface View {
        fun showState(state: SearchUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applyFeaturedDestination(context: Context, destination: String)
        fun selectFlightTripType(context: Context, tripType: FlightTripType)
        fun selectFlightDepartureAirport(context: Context, airportCode: String)
        fun selectFlightArrivalAirport(context: Context, airportCode: String)
        fun swapFlightAirports(context: Context)
        fun changeFlightAdultCount(context: Context, delta: Int)
        fun selectFlightCabinClass(context: Context, cabinClass: String)
        fun setFlightDirectOnly(context: Context, checked: Boolean)
        fun selectFlightHotelTripType(context: Context, tripType: FlightHotelTripType)
        fun selectFlightHotelDepartureAirport(context: Context, airportCode: String)
        fun selectFlightHotelArrivalAirport(context: Context, airportCode: String)
        fun setFlightHotelDepartureDate(context: Context, departureDate: LocalDate)
        fun changeFlightHotelPassengerCount(context: Context, delta: Int)
        fun changeFlightHotelRoomCount(context: Context, delta: Int)
        fun selectFlightHotelCabinClass(context: Context, cabinClass: String)
        fun setFlightHotelDifferentCityAndDates(
            context: Context,
            checked: Boolean,
            airportOptions: List<AirportOptionUiModel>
        )
        fun selectFlightHotelStayDestination(
            context: Context,
            destination: String,
            airportOptions: List<AirportOptionUiModel>
        )
        fun shiftFlightHotelStayDates(
            context: Context,
            days: Long,
            airportOptions: List<AirportOptionUiModel>
        )
        fun setCarReturnToSameLocation(context: Context, checked: Boolean)
        fun selectCarPickupLocation(context: Context, pickupLocation: String)
        fun setCarDriverAge(context: Context, value: String)
        fun selectTaxiTripType(context: Context, tripType: TaxiTripType)
        fun setTaxiRoute(context: Context, pickupLocation: String, destination: String)
        fun setTaxiPickupDateTime(context: Context, pickupDateTime: LocalDateTime)
        fun setTaxiReturnDateTime(context: Context, returnDateTime: LocalDateTime)
        fun setTaxiPassengerCount(context: Context, passengerCount: Int)
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
    val flightDepartureCode: String = "",
    val flightArrivalCode: String = "",
    val flightDepartureLabel: String = "",
    val flightArrivalLabel: String = "",
    val flightDateLabel: String = "",
    val flightAdultCount: Int = 1,
    val flightCabinClass: String = "",
    val flightPassengerLabel: String = "",
    val flightDirectOnly: Boolean = false,
    val flightHotelTripType: FlightHotelTripType = FlightHotelTripType.OneWay,
    val flightHotelDepartureCode: String = "",
    val flightHotelArrivalCode: String = "",
    val flightHotelDepartureLabel: String = "",
    val flightHotelArrivalLabel: String = "",
    val flightHotelDepartureDate: LocalDate = LocalDate.MIN,
    val flightHotelPassengerCount: Int = 1,
    val flightHotelCabinClass: String = "",
    val flightHotelRoomCount: Int = 1,
    val flightHotelDepartureDateLabel: String = "",
    val flightHotelStayDestinationQuery: String = "",
    val flightHotelCheckInDate: LocalDate = LocalDate.MIN,
    val flightHotelCheckOutDate: LocalDate = LocalDate.MIN,
    val flightHotelPassengerLabel: String = "",
    val flightHotelStayDestinationLabel: String = "",
    val flightHotelStayDateLabel: String = "",
    val flightHotelDifferentCityAndDates: Boolean = true,
    val carReturnToSameLocation: Boolean = true,
    val carPickupLocation: String = "",
    val carPickupLocationLabel: String = "",
    val carDateLabel: String = "",
    val carDriverAgeText: String = "",
    val taxiTripType: TaxiTripType = TaxiTripType.OneWay,
    val taxiPickupLocation: String = "",
    val taxiDestination: String = "",
    val taxiPickupDateTime: LocalDateTime = LocalDateTime.MIN,
    val taxiReturnDateTime: LocalDateTime = LocalDateTime.MIN,
    val taxiReturnDateTimeConfirmed: Boolean = false,
    val taxiPassengerCount: Int = 1,
    val taxiPickupLocationLabel: String = "",
    val taxiDestinationLabel: String = "",
    val taxiTimeLabel: String = "",
    val taxiReturnTimeLabel: String = "Add a return time",
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
    val carPickupLocations: List<String> = emptyList()
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
