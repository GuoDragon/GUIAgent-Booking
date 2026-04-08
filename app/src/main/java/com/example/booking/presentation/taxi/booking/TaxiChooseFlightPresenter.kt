package com.example.booking.presentation.taxi.booking

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.Airline
import com.example.booking.model.Airport
import com.example.booking.model.Flight
import com.example.booking.presentation.taxi.common.TaxiDraftStore
import java.time.Instant
import java.time.ZoneId

class TaxiChooseFlightPresenter(
    private val view: TaxiChooseFlightContract.View
) : TaxiChooseFlightContract.Presenter {

    private var candidateFlights: List<Flight> = emptyList()
    private var airportMap: Map<String, Airport> = emptyMap()
    private var airlineMap: Map<String, Airline> = emptyMap()
    private var selectedAirlineIds: Set<String> = emptySet()
    private var selectedDepartureSlots: Set<FlightTimeSlot> = emptySet()
    private var selectedArrivalSlots: Set<FlightTimeSlot> = emptySet()

    override fun loadData(context: Context) {
        val draft = TaxiDraftStore.snapshot()
        airportMap = DataRepository.loadAirports(context).associateBy { it.code }
        airlineMap = DataRepository.loadAirlines(context).associateBy { it.airlineId }

        val flights = DataRepository.loadFlights(context)
        val pickupAirportCode = resolveAirportCode(draft.pickupLocation)
        val flightsToPickup = flights.filter { flight ->
            pickupAirportCode.isNotBlank() && flight.arrivalAirportCode == pickupAirportCode
        }
        val baselineFlights = if (flightsToPickup.isNotEmpty()) {
            flightsToPickup
        } else {
            flights
        }

        val departureQuery = draft.departureAirportQuery.trim()
        val queryMatchedFlights = if (departureQuery.isBlank()) {
            baselineFlights
        } else {
            baselineFlights.filter { flight ->
                matchesDepartureQuery(flight, departureQuery)
            }
        }
        candidateFlights = (if (queryMatchedFlights.isNotEmpty()) queryMatchedFlights else baselineFlights)
            .sortedBy { it.departureTime }

        selectedAirlineIds = emptySet()
        selectedDepartureSlots = emptySet()
        selectedArrivalSlots = emptySet()
        emitState()
    }

    override fun applyFilters(
        selectedAirlineIds: Set<String>,
        selectedDepartureSlots: Set<FlightTimeSlot>,
        selectedArrivalSlots: Set<FlightTimeSlot>
    ) {
        this.selectedAirlineIds = selectedAirlineIds
        this.selectedDepartureSlots = selectedDepartureSlots
        this.selectedArrivalSlots = selectedArrivalSlots
        emitState()
    }

    override fun resetFilters() {
        selectedAirlineIds = emptySet()
        selectedDepartureSlots = emptySet()
        selectedArrivalSlots = emptySet()
        emitState()
    }

    override fun selectFlight(flightId: String) {
        val selectedFlight = candidateFlights.firstOrNull { it.flightId == flightId } ?: return
        val departureAirport = airportMap[selectedFlight.departureAirportCode]
        TaxiDraftStore.update { draft ->
            draft.copy(
                selectedFlightId = selectedFlight.flightId,
                flightNumber = selectedFlight.flightNumber,
                departureAirportQuery = departureAirport?.name ?: selectedFlight.departureAirportCode
            )
        }
    }

    private fun emitState() {
        val visibleFlights = candidateFlights.filter { flight ->
            val airlineMatch = selectedAirlineIds.isEmpty() || selectedAirlineIds.contains(flight.airlineId)
            val departureMatch = selectedDepartureSlots.isEmpty() || selectedDepartureSlots.any { slot ->
                slot.contains(epochMillisHour(flight.departureTime))
            }
            val arrivalMatch = selectedArrivalSlots.isEmpty() || selectedArrivalSlots.any { slot ->
                slot.contains(epochMillisHour(flight.arrivalTime))
            }
            airlineMatch && departureMatch && arrivalMatch
        }

        val airlineOptions = candidateFlights
            .map { it.airlineId }
            .distinct()
            .mapNotNull { airlineId ->
                airlineMap[airlineId]?.let { airline ->
                    TaxiAirlineOptionUiModel(
                        airlineId = airlineId,
                        name = airline.name
                    )
                }
            }
            .sortedBy { it.name }

        view.showState(
            TaxiChooseFlightUiState(
                cards = visibleFlights.map(::mapCard),
                airlineOptions = airlineOptions,
                selectedAirlineIds = selectedAirlineIds,
                selectedDepartureSlots = selectedDepartureSlots,
                selectedArrivalSlots = selectedArrivalSlots
            )
        )
    }

    private fun mapCard(flight: Flight): TaxiFlightCardUiModel {
        val departureAirport = airportMap[flight.departureAirportCode]
        val arrivalAirport = airportMap[flight.arrivalAirportCode]
        val airlineName = airlineMap[flight.airlineId]?.name ?: flight.airlineId

        return TaxiFlightCardUiModel(
            flightId = flight.flightId,
            title = "${flight.flightNumber} · $airlineName",
            departureLabel = "${departureAirport?.city ?: flight.departureAirportCode} (${flight.departureAirportCode})",
            arrivalLabel = "${arrivalAirport?.city ?: flight.arrivalAirportCode} (${flight.arrivalAirportCode})",
            departureTimeLabel = BookingFormatters.formatTime(flight.departureTime),
            arrivalTimeLabel = BookingFormatters.formatTime(flight.arrivalTime),
            departureDateLabel = BookingFormatters.formatFullDate(flight.departureTime),
            arrivalDateLabel = BookingFormatters.formatFullDate(flight.arrivalTime)
        )
    }

    private fun matchesDepartureQuery(
        flight: Flight,
        departureQuery: String
    ): Boolean {
        val airport = airportMap[flight.departureAirportCode]
        val query = departureQuery.lowercase()
        return flight.departureAirportCode.lowercase().contains(query) ||
            flight.flightNumber.lowercase().contains(query) ||
            airport?.city?.lowercase()?.contains(query) == true ||
            airport?.name?.lowercase()?.contains(query) == true ||
            airlineMap[flight.airlineId]?.name?.lowercase()?.contains(query) == true
    }

    private fun resolveAirportCode(location: String): String {
        val normalizedLocation = location.lowercase()
        return airportMap.values.firstOrNull { airport ->
            normalizedLocation.contains(airport.code.lowercase()) ||
                normalizedLocation.contains(airport.city.lowercase()) ||
                normalizedLocation.contains(airport.name.lowercase())
        }?.code.orEmpty()
    }

    private fun epochMillisHour(epochMillis: Long): Int {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .hour
    }
}
