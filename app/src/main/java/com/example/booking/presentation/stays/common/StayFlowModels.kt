package com.example.booking.presentation.stays.common

import java.time.LocalDate

enum class StaySortOption(val title: String) {
    EntireHomesFirst("Entire homes & apartments first"),
    TopPicks("Our top picks"),
    DistanceFromDowntown("Distance from downtown"),
    DistanceFromClosestBeach("Distance from closest beach"),
    RatingHighToLow("Property rating (5 to 0)"),
    RatingLowToHigh("Property rating (0 to 5)"),
    Genius("Genius"),
    BestReviewedFirst("Best reviewed first"),
    PriceLowToHigh("Price (lowest first)"),
    PriceHighToLow("Price (highest first)")
}

enum class StayTripPurpose(val label: String) {
    Work("Work"),
    Leisure("Leisure")
}

data class StayFilterState(
    val minBudget: Double? = null,
    val maxBudget: Double? = null,
    val selectedStarRatings: Set<Int> = emptySet(),
    val minimumReviewScore: Double? = null,
    val selectedAmenities: Set<String> = emptySet(),
    val selectedBrands: Set<String> = emptySet(),
    val accessibleByElevator: Boolean = false
)

data class StayDraft(
    val destinationQuery: String = "",
    val checkInDate: LocalDate = LocalDate.now(),
    val checkOutDate: LocalDate = LocalDate.now().plusDays(1),
    val roomCount: Int = 1,
    val adultCount: Int = 2,
    val childCount: Int = 0,
    val travelingWithPets: Boolean = false,
    val sortOption: StaySortOption = StaySortOption.TopPicks,
    val filterState: StayFilterState = StayFilterState(),
    val selectedHotelId: String? = null,
    val selectedRoomId: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneCountryCode: String = "+1",
    val phoneNumber: String = "",
    val countryOrRegion: String = "United States",
    val saveToAccount: Boolean = false,
    val tripPurpose: StayTripPurpose? = null,
    val interestedInCarRental: Boolean = false,
    val specialRequest: String = "",
    val lastCreatedOrderId: String? = null
) {
    val totalGuests: Int
        get() = adultCount + childCount
}
