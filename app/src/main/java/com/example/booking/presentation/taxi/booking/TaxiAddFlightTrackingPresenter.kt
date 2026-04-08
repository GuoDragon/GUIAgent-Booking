package com.example.booking.presentation.taxi.booking

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.presentation.taxi.common.TaxiDraftStore

class TaxiAddFlightTrackingPresenter(
    private val view: TaxiAddFlightTrackingContract.View
) : TaxiAddFlightTrackingContract.Presenter {

    override fun loadData(context: Context) {
        val draft = TaxiDraftStore.snapshot()
        val flights = DataRepository.loadFlights(context)
        val airports = DataRepository.loadAirports(context).associateBy { it.code }
        val airlines = DataRepository.loadAirlines(context).associateBy { it.airlineId }
        val selectedFlight = flights.firstOrNull { it.flightId == draft.selectedFlightId }

        val selectedFlightTitle = selectedFlight?.let { flight ->
            val airlineName = airlines[flight.airlineId]?.name ?: flight.airlineId
            "${flight.flightNumber} · $airlineName"
        }.orEmpty()
        val selectedFlightSubtitle = selectedFlight?.let { flight ->
            val departureCity = airports[flight.departureAirportCode]?.city ?: flight.departureAirportCode
            val arrivalCity = airports[flight.arrivalAirportCode]?.city ?: flight.arrivalAirportCode
            "$departureCity -> $arrivalCity · ${BookingFormatters.formatTime(flight.departureTime)} - ${BookingFormatters.formatTime(flight.arrivalTime)}"
        }.orEmpty()

        view.showState(
            TaxiAddFlightTrackingUiState(
                pickupAirportLabel = resolvePickupAirportLabel(context, draft.pickupLocation),
                departureAirportQuery = draft.departureAirportQuery,
                selectedFlightTitle = selectedFlightTitle,
                selectedFlightSubtitle = selectedFlightSubtitle
            )
        )
    }

    override fun saveDepartureAirportQuery(query: String) {
        TaxiDraftStore.update { draft ->
            draft.copy(departureAirportQuery = query.trim())
        }
    }

    private fun resolvePickupAirportLabel(
        context: Context,
        pickupLocation: String
    ): String {
        val normalizedPickupLocation = pickupLocation.lowercase()
        val match = DataRepository.loadAirports(context).firstOrNull { airport ->
            normalizedPickupLocation.contains(airport.name.lowercase()) ||
                normalizedPickupLocation.contains(airport.city.lowercase()) ||
                airport.name.lowercase().contains(normalizedPickupLocation)
        }
        return if (match != null) {
            "${match.name} (${match.code})"
        } else {
            pickupLocation
        }
    }
}
