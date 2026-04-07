package com.example.booking.common.resolver

import com.example.booking.model.Airport
import com.example.booking.model.Attraction
import com.example.booking.model.CarRental
import com.example.booking.model.Cruise
import com.example.booking.model.Flight
import com.example.booking.model.Hotel
import com.example.booking.model.WishlistItem
import java.util.Locale

data class SavedGroup(
    val title: String,
    val count: Int
)

object WishlistResolver {

    fun buildGroups(
        wishlistItems: List<WishlistItem>,
        hotels: List<Hotel>,
        flights: List<Flight>,
        airports: List<Airport>,
        attractions: List<Attraction>,
        carRentals: List<CarRental>,
        cruises: List<Cruise>
    ): List<SavedGroup> {
        val hotelMap = hotels.associateBy { it.hotelId }
        val flightMap = flights.associateBy { it.flightId }
        val airportMap = airports.associateBy { it.code }
        val attractionMap = attractions.associateBy { it.attractionId }
        val carRentalMap = carRentals.associateBy { it.carId }
        val cruiseMap = cruises.associateBy { it.cruiseId }

        val groups = linkedMapOf<String, Int>()
        wishlistItems.forEach { item ->
            val title = resolveTitle(
                item = item,
                hotelMap = hotelMap,
                flightMap = flightMap,
                airportMap = airportMap,
                attractionMap = attractionMap,
                attractions = attractions,
                carRentalMap = carRentalMap,
                cruiseMap = cruiseMap
            )
            groups[title] = (groups[title] ?: 0) + 1
        }

        return groups.map { (title, count) -> SavedGroup(title = title, count = count) }
    }

    private fun resolveTitle(
        item: WishlistItem,
        hotelMap: Map<String, Hotel>,
        flightMap: Map<String, Flight>,
        airportMap: Map<String, Airport>,
        attractionMap: Map<String, Attraction>,
        attractions: List<Attraction>,
        carRentalMap: Map<String, CarRental>,
        cruiseMap: Map<String, Cruise>
    ): String {
        return when (item.itemType.uppercase(Locale.ENGLISH)) {
            "HOTEL" -> hotelMap[item.itemId]?.city ?: "Saved stays"
            "FLIGHT" -> flightMap[item.itemId]
                ?.arrivalAirportCode
                ?.let(airportMap::get)
                ?.city
                ?: "Saved flights"
            "ATTRACTION" -> resolveAttraction(item.itemId, attractionMap, attractions)?.city
                ?: "Saved attractions"
            "CAR_RENTAL" -> carRentalMap[item.itemId]?.pickupLocation ?: "Saved car rentals"
            "CRUISE" -> cruiseMap[item.itemId]?.destination ?: "Saved cruises"
            else -> "Saved items"
        }
    }

    private fun resolveAttraction(
        itemId: String,
        attractionMap: Map<String, Attraction>,
        attractions: List<Attraction>
    ): Attraction? {
        attractionMap[itemId]?.let { return it }
        val suffix = itemId.filter { it.isDigit() }
        return attractions.firstOrNull { attraction ->
            attraction.attractionId.filter { it.isDigit() } == suffix
        }
    }
}
