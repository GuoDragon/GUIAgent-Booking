package com.example.booking.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ListAlt
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector

object BookingRoutes {
    const val Search = "search"
    const val Saved = "saved"
    const val Orders = "orders"
    const val Account = "account"
    const val PersonalInfo = "personal_info"
    const val TravelCompanions = "travel_companions"
    const val AddTravelCompanion = "add_travel_companion"
    const val StayDestination = "stay_destination"
    const val StayDate = "stay_date"
    const val StayResults = "stay_results"
    const val StayFilter = "stay_filter"
    const val StayDetails = "stay_details"
    const val StayRoomType = "stay_room_type"
    const val StayPersonalInfo = "stay_personal_info"
    const val StayBookingOverview = "stay_booking_overview"
    const val StayBookingSuccess = "stay_booking_success"
    const val StayBookingSuccessWithOrderId = "stay_booking_success/{orderId}"
    const val FlightDate = "flight_date"
    const val FlightResults = "flight_results"
    const val FlightFilter = "flight_filter"
    const val FlightDetails = "flight_details"
    const val FlightFare = "flight_fare"
    const val FlightLuggage = "flight_luggage"
    const val FlightMealChoice = "flight_meal_choice"
    const val FlightCustomPreferences = "flight_custom_preferences"
    const val FlightTravelerDetails = "flight_traveler_details"
    const val FlightTravelerContact = "flight_traveler_contact"
    const val FlightBookingSuccess = "flight_booking_success"
    const val FlightBookingSuccessWithOrderId = "flight_booking_success/{orderId}"
    const val FlightPlusHotelHub = "flight_plus_hotel_hub"
    const val CarRentalDate = "car_rental_date"
    const val CarRentalResults = "car_rental_results"
    const val CarRentalFilter = "car_rental_filter"
    const val CarRentalDetails = "car_rental_details"
    const val CarRentalBookingSummary = "car_rental_booking_summary"
    const val CarRentalBookingSuccess = "car_rental_booking_success"
    const val CarRentalBookingSuccessWithOrderId = "car_rental_booking_success/{orderId}"
    const val TaxiPickupLocation = "taxi_pickup_location"
    const val TaxiDestination = "taxi_destination"
    const val TaxiTime = "taxi_time"
    const val TaxiPassengers = "taxi_passengers"
    const val TaxiResults = "taxi_results"
    const val TaxiAddFlightTracking = "taxi_add_flight_tracking"
    const val TaxiChooseFlight = "taxi_choose_flight"
    const val TaxiContactDetails = "taxi_contact_details"
    const val TaxiOverview = "taxi_overview"
    const val TaxiBookingSuccess = "taxi_booking_success"
    const val TaxiBookingSuccessWithOrderId = "taxi_booking_success/{orderId}"
    const val AttractionDestination = "attraction_destination"
    const val AttractionDate = "attraction_date"
    const val AttractionResults = "attraction_results"
    const val AttractionPreview = "attraction_preview"
    const val AttractionDetails = "attraction_details"
    const val AttractionTickets = "attraction_tickets"
    const val AttractionTicketDetails = "attraction_ticket_details"
    const val AttractionPersonalInfo = "attraction_personal_info"
    const val AttractionPayment = "attraction_payment"
    const val AttractionPaymentSuccess = "attraction_payment_success"
    const val AttractionPaymentSuccessWithOrderId = "attraction_payment_success/{orderId}"

    val topLevelRoutes = setOf(Search, Saved, Orders, Account)

    fun stayBookingSuccess(orderId: String): String = "$StayBookingSuccess/$orderId"
    fun flightBookingSuccess(orderId: String): String = "$FlightBookingSuccess/$orderId"
    fun carRentalBookingSuccess(orderId: String): String = "$CarRentalBookingSuccess/$orderId"
    fun taxiBookingSuccess(orderId: String): String = "$TaxiBookingSuccess/$orderId"
    fun attractionPaymentSuccess(orderId: String): String = "$AttractionPaymentSuccess/$orderId"
}

data class BookingBottomDestination(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bookingBottomDestinations = listOf(
    BookingBottomDestination(
        route = BookingRoutes.Search,
        label = "Search",
        selectedIcon = Icons.Filled.Search,
        unselectedIcon = Icons.Filled.Search
    ),
    BookingBottomDestination(
        route = BookingRoutes.Saved,
        label = "Saved",
        selectedIcon = Icons.Filled.Favorite,
        unselectedIcon = Icons.Outlined.FavoriteBorder
    ),
    BookingBottomDestination(
        route = BookingRoutes.Orders,
        label = "Trips",
        selectedIcon = Icons.Filled.ListAlt,
        unselectedIcon = Icons.Outlined.ListAlt
    ),
    BookingBottomDestination(
        route = BookingRoutes.Account,
        label = "Account",
        selectedIcon = Icons.Filled.AccountCircle,
        unselectedIcon = Icons.Outlined.PersonOutline
    )
)
