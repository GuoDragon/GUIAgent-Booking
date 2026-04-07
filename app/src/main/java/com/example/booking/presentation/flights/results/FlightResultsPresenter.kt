package com.example.booking.presentation.flights.results

import android.content.Context
import com.example.booking.common.demo.DemoVisuals
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.SearchSignal
import com.example.booking.presentation.flights.common.FlightDraftStore
import com.example.booking.presentation.flights.common.FlightFilterState
import com.example.booking.presentation.flights.common.FlightFlowMapper
import com.example.booking.presentation.flights.common.FlightItinerary
import com.example.booking.presentation.flights.common.FlightSortOption
import java.util.UUID

class FlightResultsPresenter(
    private val view: FlightResultsContract.View
) : FlightResultsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val flights = DataRepository.loadFlights(context)
        val airlines = DataRepository.loadAirlines(context)
        val airports = DataRepository.loadAirports(context)
        val itineraries = FlightFlowMapper.sortItineraries(
            FlightFlowMapper.buildItineraries(flights, airlines, airports, draft),
            draft.sortOption
        )
        val cards = expandCards(itineraries)

        view.showState(
            FlightResultsUiState(
                routeLabel = "${FlightFlowMapper.airportLabel(draft.departureAirportCode, airports)} -> ${FlightFlowMapper.airportLabel(draft.arrivalAirportCode, airports)}",
                tripLabel = buildTripLabel(draft.adultCount, draft.cabinClass, draft.departureDate, draft.returnDate),
                resultsCount = cards.size,
                cards = cards,
                hasActiveFilters = draft.filterState != FlightFilterState()
            )
        )
    }

    override fun recordMapOpened(context: Context) {
        val draft = FlightDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "flight_map_${UUID.randomUUID()}",
                searchType = "FLIGHT_MAP_OPENED",
                destination = "${draft.departureAirportCode} -> ${draft.arrivalAirportCode}",
                checkInDate = BookingFormatters.localDateToEpochMillis(draft.departureDate),
                checkOutDate = BookingFormatters.localDateToEpochMillis(draft.returnDate),
                guestCount = draft.adultCount,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    private fun expandCards(itineraries: List<FlightItinerary>): List<FlightResultCardUiModel> {
        if (itineraries.isEmpty()) return emptyList()
        val targetCount = maxOf(6, itineraries.size * 2)
        return List(targetCount) { index ->
            itineraryToCard(itineraries[index % itineraries.size], index)
        }
    }

    private fun itineraryToCard(itinerary: FlightItinerary, index: Int): FlightResultCardUiModel {
        val outboundDate = BookingFormatters.formatShortDate(itinerary.outbound.departureTime)
        val returnDate = itinerary.returnLeg?.let { BookingFormatters.formatShortDate(it.departureTime) } ?: "One way"
        return FlightResultCardUiModel(
            cardId = "${itinerary.itineraryId}_$index",
            outboundFlightId = itinerary.outbound.flightId,
            returnFlightId = itinerary.returnLeg?.flightId,
            airlineLabel = itinerary.outbound.airlineName,
            supportingText = buildSupportingText(itinerary, index),
            outboundTimeLabel = "${BookingFormatters.formatTime(itinerary.outbound.departureTime)} - ${BookingFormatters.formatTime(itinerary.outbound.arrivalTime)}",
            outboundMetaLabel = "$outboundDate | ${FlightFlowMapper.stopLabel(itinerary.outbound.stops)}",
            returnTimeLabel = itinerary.returnLeg?.let { "${BookingFormatters.formatTime(it.departureTime)} - ${BookingFormatters.formatTime(it.arrivalTime)}" } ?: "No return selected",
            returnMetaLabel = itinerary.returnLeg?.let { "$returnDate | ${FlightFlowMapper.stopLabel(it.stops)}" } ?: "",
            priceText = BookingFormatters.formatCurrency(itinerary.totalPrice, itinerary.currency),
            badgeText = buildBadgeText(itinerary, index),
            stopsLabel = if (itinerary.returnLeg == null) {
                FlightFlowMapper.stopLabel(itinerary.outbound.stops)
            } else {
                "${FlightFlowMapper.stopLabel(itinerary.outbound.stops)} / ${FlightFlowMapper.stopLabel(itinerary.returnLeg.stops)}"
            },
            durationLabel = BookingFormatters.formatDurationMinutes(FlightFlowMapper.totalDuration(itinerary))
        )
    }

    private fun buildTripLabel(adults: Int, cabinClass: String, departureDate: java.time.LocalDate, returnDate: java.time.LocalDate): String {
        return "${BookingFormatters.formatShortLocalDate(departureDate)} - ${BookingFormatters.formatShortLocalDate(returnDate)} | $adults adult${if (adults == 1) "" else "s"} | $cabinClass"
    }

    private fun buildBadgeText(
        itinerary: FlightItinerary,
        index: Int
    ): String {
        val options = if (itinerary.outbound.stops == 0) {
            listOf("Best", "Popular", "Direct")
        } else {
            listOf("Genius", "Value", "Smart choice")
        }
        return options[DemoVisuals.stableIndex("${itinerary.itineraryId}:badge:$index", options.size)]
    }

    private fun buildSupportingText(
        itinerary: FlightItinerary,
        index: Int
    ): String {
        val options = listOf(
            "Popular route this week for ${itinerary.outbound.airlineName}.",
            "Cabin bag included in this demo fare.",
            "Seats at this price are limited today.",
            "Balanced pick for price and trip time."
        )
        return options[DemoVisuals.stableIndex("${itinerary.itineraryId}:support:$index", options.size)]
    }
}

class FlightSortPresenter(
    private val view: FlightSortContract.View
) : FlightSortContract.Presenter {

    override fun loadData() {
        val draft = FlightDraftStore.snapshot()
        view.showState(
            FlightSortUiState(
                selectedOption = draft.sortOption,
                options = FlightSortOption.entries
            )
        )
    }

    override fun applySort(option: FlightSortOption) {
        FlightDraftStore.update { draft -> draft.copy(sortOption = option) }
    }
}

class FlightFilterPresenter(
    private val view: FlightFilterContract.View
) : FlightFilterContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val airlines = DataRepository.loadAirlines(context)
        view.showState(
            FlightFilterUiState(
                airlines = airlines.map { FlightAirlineOptionUiModel(it.airlineId, it.name) },
                currentFilter = draft.filterState
            )
        )
    }

    override fun applyFilter(filterState: FlightFilterState) {
        FlightDraftStore.update { draft -> draft.copy(filterState = filterState) }
    }
}

class FlightDetailsPresenter(
    private val view: FlightDetailsContract.View
) : FlightDetailsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val itinerary = FlightFlowMapper.findItinerary(
            flights = DataRepository.loadFlights(context),
            airlines = DataRepository.loadAirlines(context),
            airports = DataRepository.loadAirports(context),
            draft = draft
        )
        if (itinerary == null) {
            view.showState(FlightDetailsUiState())
            return
        }

        view.showState(
            FlightDetailsUiState(
                title = "Your flight to ${itinerary.outbound.arrivalAirportCode}",
                subtitle = "${FlightFlowMapper.stopLabel(itinerary.outbound.stops)} | ${BookingFormatters.formatDurationMinutes(itinerary.outbound.durationMinutes)}",
                priceText = BookingFormatters.formatCurrency(itinerary.totalPrice, itinerary.currency),
                totalLabel = "Total",
                segments = buildList {
                    add(segmentFromLeg("Outbound", itinerary.outbound))
                    itinerary.returnLeg?.let { add(segmentFromLeg("Return", it)) }
                },
                canContinue = true
            )
        )
    }

    private fun segmentFromLeg(header: String, leg: com.example.booking.presentation.flights.common.FlightLeg): FlightDetailSegmentUiModel {
        return FlightDetailSegmentUiModel(
            header = "$header | ${leg.airlineName}",
            points = listOf(
                FlightDetailPointUiModel(
                    timeLabel = BookingFormatters.formatTime(leg.departureTime),
                    airportLabel = "${leg.departureAirportCode} ${leg.departureAirportName}",
                    airportMeta = leg.cabinClass,
                    supportingText = "${leg.flightNumber} | ${BookingFormatters.formatDurationMinutes(leg.durationMinutes)}"
                ),
                FlightDetailPointUiModel(
                    timeLabel = BookingFormatters.formatTime(leg.arrivalTime),
                    airportLabel = "${leg.arrivalAirportCode} ${leg.arrivalAirportName}",
                    airportMeta = if (leg.synthetic) "Return leg derived from local data" else "Scheduled arrival",
                    supportingText = if (leg.stops == 0) "Direct" else "${leg.stops} stop"
                )
            )
        )
    }
}
