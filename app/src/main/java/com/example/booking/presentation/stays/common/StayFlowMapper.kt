package com.example.booking.presentation.stays.common

import com.example.booking.model.Hotel
import com.example.booking.model.HotelRoom
import java.util.Locale

object StayFlowMapper {

    fun filterHotels(hotels: List<Hotel>, draft: StayDraft): List<Hotel> {
        val query = draft.destinationQuery.trim().lowercase(Locale.ENGLISH)
        val filteredByQuery = if (query.isBlank()) {
            hotels
        } else {
            hotels.filter { hotel ->
                listOf(hotel.city, hotel.country, hotel.name, hotel.address)
                    .any { value -> value.lowercase(Locale.ENGLISH).contains(query) }
            }
        }

        return filteredByQuery.filter { hotel ->
            val filter = draft.filterState
            val inBudget = (filter.minBudget == null || hotel.pricePerNight >= filter.minBudget) &&
                (filter.maxBudget == null || hotel.pricePerNight <= filter.maxBudget)
            val starMatch = filter.selectedStarRatings.isEmpty() || hotel.starRating in filter.selectedStarRatings
            val reviewMatch = filter.minimumReviewScore == null || hotel.rating >= filter.minimumReviewScore
            val amenityMatch = filter.selectedAmenities.isEmpty() ||
                filter.selectedAmenities.all { amenity -> hotel.amenities.contains(amenity) }
            val brandMatch = filter.selectedBrands.isEmpty() || hotel.name in filter.selectedBrands
            val accessibilityMatch = !filter.accessibleByElevator ||
                hotel.amenities.any { it.contains("Elevator", ignoreCase = true) || it.contains("Lift", ignoreCase = true) }

            inBudget && starMatch && reviewMatch && amenityMatch && brandMatch && accessibilityMatch
        }
    }

    fun sortHotels(hotels: List<Hotel>, sortOption: StaySortOption): List<Hotel> {
        return when (sortOption) {
            StaySortOption.TopPicks,
            StaySortOption.EntireHomesFirst,
            StaySortOption.DistanceFromDowntown,
            StaySortOption.DistanceFromClosestBeach,
            StaySortOption.Genius -> hotels.sortedWith(compareByDescending<Hotel> { it.rating }.thenBy { it.pricePerNight })

            StaySortOption.RatingHighToLow,
            StaySortOption.BestReviewedFirst -> hotels.sortedWith(
                compareByDescending<Hotel> { it.rating }.thenByDescending { it.reviewCount }
            )

            StaySortOption.RatingLowToHigh -> hotels.sortedBy { it.rating }
            StaySortOption.PriceLowToHigh -> hotels.sortedBy { it.pricePerNight }
            StaySortOption.PriceHighToLow -> hotels.sortedByDescending { it.pricePerNight }
        }
    }

    fun findHotel(hotels: List<Hotel>, hotelId: String?): Hotel? {
        return hotels.firstOrNull { it.hotelId == hotelId }
    }

    fun findRoom(rooms: List<HotelRoom>, roomId: String?): HotelRoom? {
        return rooms.firstOrNull { it.roomId == roomId }
    }

    fun roomsForHotel(rooms: List<HotelRoom>, hotelId: String?): List<HotelRoom> {
        return rooms.filter { it.hotelId == hotelId }
    }

    fun hotelBrands(hotels: List<Hotel>): List<String> {
        return hotels.map { it.name }.sorted()
    }

    fun hotelAmenities(hotels: List<Hotel>): List<String> {
        return hotels.flatMap { it.amenities }.distinct().sorted()
    }
}
