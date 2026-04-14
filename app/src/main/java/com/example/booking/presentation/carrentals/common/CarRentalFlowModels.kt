package com.example.booking.presentation.carrentals.common

import java.time.LocalDateTime
import java.time.DayOfWeek
import java.time.temporal.TemporalAdjusters

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
    val pickupDateTime: LocalDateTime = defaultCarRentalPickupDateTime(),
    val returnDateTime: LocalDateTime = defaultCarRentalReturnDateTime(pickupDateTime),
    val driverAgeText: String = "30",
    val childSeatRequired: Boolean = true,
    val sortOption: CarRentalSortOption = CarRentalSortOption.Recommended,
    val filterState: CarRentalFilterState = CarRentalFilterState(),
    val selectedCarId: String? = null,
    val lastCreatedOrderId: String? = null
)

private fun defaultCarRentalPickupDateTime(): LocalDateTime {
    return LocalDateTime.now()
        .plusDays(2)
        .withHour(12)
        .withMinute(0)
        .withSecond(0)
        .withNano(0)
}

private fun defaultCarRentalReturnDateTime(pickupDateTime: LocalDateTime): LocalDateTime {
    val nextMondayAfterPickup = pickupDateTime.toLocalDate().with(TemporalAdjusters.next(DayOfWeek.MONDAY))
    return nextMondayAfterPickup.atTime(12, 0)
}
