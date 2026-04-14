package com.example.booking.presentation.carrentals.common

import com.example.booking.model.CarRental
import kotlin.math.max

object CarRentalFlowMapper {

    fun sortCars(cars: List<CarRental>, option: CarRentalSortOption): List<CarRental> {
        return when (option) {
            CarRentalSortOption.Recommended -> cars.sortedWith(compareByDescending<CarRental> { reviewScore(it) }.thenBy { it.pricePerDay })
            CarRentalSortOption.PriceLowToHigh -> cars.sortedBy { it.pricePerDay }
            CarRentalSortOption.GeniusDiscountsFirst -> cars.sortedWith(compareByDescending<CarRental> { geniusDiscount(it) }.thenBy { it.pricePerDay })
            CarRentalSortOption.ReviewScoreHighestFirst -> cars.sortedByDescending { reviewScore(it) }
        }
    }

    fun filterCars(cars: List<CarRental>, draft: CarRentalDraft): List<CarRental> {
        val filter = draft.filterState
        return cars.filter { car ->
            val pickupLocationMatch = draft.pickupLocation.isBlank() ||
                car.pickupLocation.equals(draft.pickupLocation, ignoreCase = true)
            val locationMatch = filter.selectedLocations.isEmpty() || filter.selectedLocations.any { it in car.pickupLocation }
            val categoryMatch = filter.selectedCategories.isEmpty() || car.category in filter.selectedCategories
            val cancellationMatch = !filter.freeCancellationOnly || car.freeCancellation
            val mileageMatch = !filter.unlimitedMileageOnly || car.unlimitedMileage
            val priceMatch = filter.maxPricePerDay == null || car.pricePerDay <= filter.maxPricePerDay
            pickupLocationMatch && locationMatch && categoryMatch && cancellationMatch && mileageMatch && priceMatch
        }
    }

    fun reviewScore(car: CarRental): Double {
        return 7.2 + ((car.carId.filter { it.isDigit() }.toIntOrNull() ?: 0) % 18) / 10.0
    }

    fun reviewCount(car: CarRental): Int {
        return 1100 + (car.carId.filter { it.isDigit() }.toIntOrNull() ?: 0) * 87
    }

    fun geniusDiscount(car: CarRental): Double {
        return if (car.freeCancellation) 0.12 else 0.05
    }

    fun originalPrice(car: CarRental): Double {
        return car.pricePerDay / (1 - geniusDiscount(car))
    }

    fun locationOptions(cars: List<CarRental>): List<String> {
        return cars.map { pickupMode(it.pickupLocation) }.distinct().sorted()
    }

    fun categoryOptions(cars: List<CarRental>): List<String> {
        return cars.map { it.category }.distinct().sorted()
    }

    fun pickupMode(location: String): String {
        val lowered = location.lowercase()
        return when {
            "airport" in lowered -> "Airport"
            else -> "City"
        }
    }

    fun rentalDays(draft: CarRentalDraft): Int {
        return max(1, draft.returnDateTime.toLocalDate().toEpochDay().minus(draft.pickupDateTime.toLocalDate().toEpochDay()).toInt())
    }
}
