package com.example.booking.presentation.flightplushotel

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.presentation.flights.common.FlightDraftStore
import com.example.booking.presentation.flights.common.FlightFlowMapper
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.stays.common.StayFlowMapper

class FlightPlusHotelPresenter(
    private val view: FlightPlusHotelContract.View
) : FlightPlusHotelContract.Presenter {

    override fun loadData(context: Context) {
        val flights = DataRepository.loadFlights(context)
        val airlines = DataRepository.loadAirlines(context)
        val airports = DataRepository.loadAirports(context)
        val hotels = DataRepository.loadHotels(context)
        val flightDraft = FlightDraftStore.snapshot()
        val stayDraft = StayDraftStore.snapshot()
        val itinerary = FlightFlowMapper.sortItineraries(
            FlightFlowMapper.buildItineraries(flights, airlines, airports, flightDraft),
            flightDraft.sortOption
        ).firstOrNull()
        val matchingHotels = StayFlowMapper.sortHotels(
            StayFlowMapper.filterHotels(hotels, stayDraft),
            stayDraft.sortOption
        )

        view.showState(
            FlightPlusHotelUiState(
                flightTitle = itinerary?.let {
                    "${it.outbound.departureAirportCode} -> ${it.outbound.arrivalAirportCode}"
                } ?: "No flight preview",
                flightSubtitle = itinerary?.let {
                    "${BookingFormatters.formatShortDate(it.outbound.departureTime)} | ${it.outbound.airlineName}"
                } ?: "Adjust the inline fields to broaden your search.",
                flightPrice = itinerary?.let {
                    BookingFormatters.formatCurrency(it.totalPrice, it.currency)
                } ?: "",
                flightCountLabel = "Flight results reuse the dedicated Flights flow.",
                stayTitle = matchingHotels.firstOrNull()?.name ?: "No stay preview",
                staySubtitle = matchingHotels.firstOrNull()?.address ?: "Adjust the destination to view stay results.",
                stayPrice = matchingHotels.firstOrNull()?.let {
                    BookingFormatters.formatCurrency(it.pricePerNight, it.currency)
                } ?: "",
                stayCountLabel = "Stay results reuse the existing Stays flow."
            )
        )
    }
}
