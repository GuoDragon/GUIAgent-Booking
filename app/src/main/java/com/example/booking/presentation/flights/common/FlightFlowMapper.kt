package com.example.booking.presentation.flights.common

import com.example.booking.common.format.BookingFormatters
import com.example.booking.model.Airline
import com.example.booking.model.Airport
import com.example.booking.model.Flight
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

object FlightFlowMapper {

    fun buildItineraries(
        flights: List<Flight>,
        airlines: List<Airline>,
        airports: List<Airport>,
        draft: FlightDraft
    ): List<FlightItinerary> {
        val airlineMap = airlines.associateBy { it.airlineId }
        val airportMap = airports.associateBy { it.code }
        val outboundFlights = flights
            .filter { flight ->
                flight.departureAirportCode == draft.departureAirportCode &&
                    flight.arrivalAirportCode == draft.arrivalAirportCode &&
                    (draft.cabinClass.equals("All", ignoreCase = true) ||
                        flight.cabinClass.equals(draft.cabinClass, ignoreCase = true))
            }
            .filter { !draft.directFlightsOnly || it.stops == 0 }
            .filter { draft.filterState.selectedStops.isEmpty() || it.stops in draft.filterState.selectedStops }
            .filter { draft.filterState.selectedAirlineIds.isEmpty() || it.airlineId in draft.filterState.selectedAirlineIds }
            .sortedBy { it.price }

        return outboundFlights.map { outbound ->
            val mappedOutbound = outbound.toLeg(airlineMap, airportMap)
            val returnLeg = when (draft.tripType) {
                FlightTripType.RoundTrip -> {
                    val matchingReturn = flights
                        .filter { it.departureAirportCode == draft.arrivalAirportCode && it.arrivalAirportCode == draft.departureAirportCode }
                        .filter { draft.filterState.selectedAirlineIds.isEmpty() || it.airlineId in draft.filterState.selectedAirlineIds }
                        .sortedBy { it.price }
                        .firstOrNull()
                    matchingReturn?.toLeg(airlineMap, airportMap)
                        ?: outbound.toSyntheticReturnLeg(airlineMap, airportMap, draft.returnDate)
                }

                else -> null
            }

            FlightItinerary(
                itineraryId = listOfNotNull(mappedOutbound.flightId, returnLeg?.flightId ?: returnLeg?.departureAirportCode).joinToString("_"),
                outbound = mappedOutbound,
                returnLeg = returnLeg,
                totalPrice = mappedOutbound.price + (returnLeg?.price ?: 0.0),
                currency = mappedOutbound.currency
            )
        }
    }

    fun sortItineraries(itineraries: List<FlightItinerary>, option: FlightSortOption): List<FlightItinerary> {
        return when (option) {
            FlightSortOption.Best -> itineraries.sortedWith(
                compareBy<FlightItinerary> { it.outbound.stops }
                    .thenBy { it.totalPrice }
                    .thenBy { totalDuration(it) }
            )

            FlightSortOption.Cheapest -> itineraries.sortedBy { it.totalPrice }
            FlightSortOption.Fastest -> itineraries.sortedBy { totalDuration(it) }
        }
    }

    fun findItinerary(
        flights: List<Flight>,
        airlines: List<Airline>,
        airports: List<Airport>,
        draft: FlightDraft
    ): FlightItinerary? {
        val itineraries = buildItineraries(flights, airlines, airports, draft)
        return itineraries.firstOrNull { itinerary ->
            itinerary.outbound.flightId == draft.selectedOutboundFlightId &&
                itinerary.returnLeg?.flightId == draft.selectedReturnFlightId
        } ?: itineraries.firstOrNull { it.outbound.flightId == draft.selectedOutboundFlightId }
    }

    fun cycleAirport(currentCode: String, airports: List<Airport>, excludedCode: String? = null): String {
        val options = airports.map { it.code }.filter { it != excludedCode }
        if (options.isEmpty()) {
            return currentCode
        }
        val currentIndex = options.indexOf(currentCode)
        return options[(currentIndex + 1).floorMod(options.size)]
    }

    fun airportLabel(code: String, airports: List<Airport>): String {
        val airport = airports.firstOrNull { it.code == code }
        return if (airport == null) code else "${airport.code} ${airport.city}"
    }

    fun airportDisplayName(code: String, airports: List<Airport>): String {
        val airport = airports.firstOrNull { it.code == code }
        return if (airport == null) code else "${airport.code} - ${airport.name}"
    }

    fun itineraryRouteLabel(itinerary: FlightItinerary): String {
        return "${itinerary.outbound.departureAirportCode} ${itinerary.outbound.departureAirportName} -> ${itinerary.outbound.arrivalAirportCode} ${itinerary.outbound.arrivalAirportName}"
    }

    fun stopLabel(stops: Int): String {
        return if (stops == 0) "Direct" else "$stops stop"
    }

    fun totalDuration(itinerary: FlightItinerary): Int {
        return itinerary.outbound.durationMinutes + (itinerary.returnLeg?.durationMinutes ?: 0)
    }

    fun seatRows(itinerary: FlightItinerary): List<String> {
        val rows = mutableListOf(
            "${itinerary.outbound.departureAirportCode} -> ${itinerary.outbound.arrivalAirportCode}"
        )
        itinerary.returnLeg?.let { rows += "${it.returnLegCode()}" }
        return rows
    }

    private fun Flight.toLeg(
        airlineMap: Map<String, Airline>,
        airportMap: Map<String, Airport>
    ): FlightLeg {
        return FlightLeg(
            flightId = flightId,
            airlineId = airlineId,
            airlineName = airlineMap[airlineId]?.name ?: airlineId,
            flightNumber = flightNumber,
            departureAirportCode = departureAirportCode,
            departureAirportName = airportMap[departureAirportCode]?.name ?: departureAirportCode,
            arrivalAirportCode = arrivalAirportCode,
            arrivalAirportName = airportMap[arrivalAirportCode]?.name ?: arrivalAirportCode,
            departureTime = departureTime,
            arrivalTime = arrivalTime,
            durationMinutes = durationMinutes,
            stops = stops,
            cabinClass = cabinClass,
            price = price,
            currency = currency,
            synthetic = false
        )
    }

    private fun Flight.toSyntheticReturnLeg(
        airlineMap: Map<String, Airline>,
        airportMap: Map<String, Airport>,
        returnDate: LocalDate
    ): FlightLeg {
        val departureDateTime = LocalDateTime.of(returnDate, LocalTime.of(9, 30))
        val arrivalDateTime = departureDateTime.plusMinutes(durationMinutes.toLong())
        return FlightLeg(
            flightId = null,
            airlineId = airlineId,
            airlineName = airlineMap[airlineId]?.name ?: airlineId,
            flightNumber = "$flightNumber-R",
            departureAirportCode = arrivalAirportCode,
            departureAirportName = airportMap[arrivalAirportCode]?.name ?: arrivalAirportCode,
            arrivalAirportCode = departureAirportCode,
            arrivalAirportName = airportMap[departureAirportCode]?.name ?: departureAirportCode,
            departureTime = BookingFormatters.localDateTimeToEpochMillis(departureDateTime),
            arrivalTime = BookingFormatters.localDateTimeToEpochMillis(arrivalDateTime),
            durationMinutes = durationMinutes,
            stops = stops,
            cabinClass = cabinClass,
            price = price,
            currency = currency,
            synthetic = true
        )
    }

    private fun Int.floorMod(other: Int): Int {
        return ((this % other) + other) % other
    }

    private fun FlightLeg.returnLegCode(): String {
        return "$departureAirportCode -> $arrivalAirportCode"
    }
}
