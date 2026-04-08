package com.example.booking.presentation.carrentals.common

import java.time.LocalDateTime

enum class CarRentalSortOption(val title: String) {
    Recommended("Recommended"),
    PriceLowToHigh("Price (lowest first)"),
    GeniusDiscountsFirst("Genius discounts first"),
    ReviewScoreHighestFirst("Review score (highest first)")
}

data class CarRentalFilterState(
    val selectedLocations: Set<String> = emptySet(),
    val selectedCategories: Set<String> = emptySet(),
    val freeCancellationOnly: Boolean = false,
    val unlimitedMileageOnly: Boolean = false,
    val maxPricePerDay: Double? = null
)

data class CarRentalDraft(
    val returnToSameLocation: Boolean = true,
    val pickupLocation: String = "London Heathrow Airport (LHR)",
    val pickupDateTime: LocalDateTime = LocalDateTime.now().plusDays(14).withHour(10).withMinute(0),
    val returnDateTime: LocalDateTime = LocalDateTime.now().plusDays(16).withHour(10).withMinute(0),
    val driverAgeText: String = "30",
    val sortOption: CarRentalSortOption = CarRentalSortOption.Recommended,
    val filterState: CarRentalFilterState = CarRentalFilterState(),
    val selectedCarId: String? = null,
    val lastCreatedOrderId: String? = null
)
