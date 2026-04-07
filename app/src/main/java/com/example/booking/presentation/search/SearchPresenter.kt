package com.example.booking.presentation.search

import android.content.Context
import com.example.booking.presentation.attractions.common.AttractionDraftStore
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.SearchSignal
import com.example.booking.presentation.carrentals.common.CarRentalDraftStore
import com.example.booking.presentation.flightplushotel.FlightHotelTripType
import com.example.booking.presentation.flightplushotel.FlightPlusHotelDraftStore
import com.example.booking.presentation.flights.common.FlightDraftStore
import com.example.booking.presentation.flights.common.FlightTripType
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.taxi.common.TaxiDraftStore
import java.util.UUID
import kotlin.math.max

class SearchPresenter(
    private val view: SearchContract.View
) : SearchContract.Presenter {

    override fun loadData(context: Context) {
        val stayDraft = StayDraftStore.snapshot()
        val flightDraft = FlightDraftStore.snapshot()
        val flightHotelDraft = FlightPlusHotelDraftStore.snapshot()
        val carDraft = CarRentalDraftStore.snapshot()
        val taxiDraft = TaxiDraftStore.snapshot()
        val attractionDraft = AttractionDraftStore.snapshot()

        val hotels = DataRepository.loadHotels(context)
        val flights = DataRepository.loadFlights(context)
        val airports = DataRepository.loadAirports(context)
        val carRentals = DataRepository.loadCarRentals(context)
        val taxiRoutes = DataRepository.loadTaxiRoutes(context)
        val attractions = DataRepository.loadAttractions(context)

        val airportMap = airports.associateBy { it.code }

        val recentItems = buildList {
            carRentals.firstOrNull()?.let { rental ->
                add(
                    RecentSearchItem(
                        type = SearchRecentType.CarRental,
                        title = "${rental.companyName} ${rental.carModel}",
                        subtitle = rental.pickupLocation,
                        meta = "From ${BookingFormatters.formatCurrency(rental.pricePerDay, rental.currency)} per day"
                    )
                )
            }
            flights.firstOrNull()?.let { flight ->
                val departureCity = airportMap[flight.departureAirportCode]?.city ?: flight.departureAirportCode
                val arrivalCity = airportMap[flight.arrivalAirportCode]?.city ?: flight.arrivalAirportCode
                add(
                    RecentSearchItem(
                        type = SearchRecentType.Flight,
                        title = "${flight.departureAirportCode} - ${flight.arrivalAirportCode}",
                        subtitle = "$departureCity to $arrivalCity",
                        meta = BookingFormatters.formatDateRange(
                            startMillis = flight.departureTime,
                            endMillis = flight.arrivalTime
                        )
                    )
                )
            }
        }

        val destinationCards = hotels
            .groupBy { it.city }
            .entries
            .sortedByDescending { it.value.size }
            .take(6)
            .map { entry ->
                val sampleHotel = entry.value.first()
                val lowestPrice = entry.value.minOf { it.pricePerNight }
                DestinationHighlight(
                    title = entry.key,
                    subtitle = sampleHotel.country,
                    caption = "${entry.value.size} stays from ${
                        BookingFormatters.formatCurrency(lowestPrice, sampleHotel.currency)
                    }"
                )
            }

        val continueHotel = hotels
            .firstOrNull { hotel ->
                stayDraft.destinationQuery.isBlank() ||
                    hotel.city.equals(stayDraft.destinationQuery, ignoreCase = true) ||
                    hotel.name.contains(stayDraft.destinationQuery, ignoreCase = true)
            }
            ?: hotels.firstOrNull()

        val flightHotelArrivalCity = airportMap[flightHotelDraft.arrivalAirportCode]?.city
            ?: flightHotelDraft.stayDestinationQuery

        val taxiRecentItems = taxiRoutes
            .take(4)
            .map { route ->
                TaxiRecentItem(
                    title = route.destination,
                    subtitle = route.pickupLocation,
                    meta = "From ${BookingFormatters.formatCurrency(route.price, route.currency)} | ${route.estimatedDurationMinutes} min"
                )
            }

        val attractionCards = attractions
            .sortedWith(compareByDescending<com.example.booking.model.Attraction> { it.rating }.thenBy { it.fromPrice })
            .take(6)
            .mapIndexed { index, attraction ->
                AttractionHighlightCard(
                    title = attraction.name,
                    subtitle = "${attraction.city}, ${attraction.country}",
                    priceText = "From ${BookingFormatters.formatCurrency(attraction.fromPrice, attraction.currency)}",
                    badgeText = if (index % 2 == 0) "Genius" else "Top rated"
                )
            }

        view.showState(
            SearchUiState(
                stayDestinationLabel = stayDraft.destinationQuery.ifBlank { "Destination, landmark, or property" },
                stayDateLabel = BookingFormatters.formatStayDateRange(stayDraft.checkInDate, stayDraft.checkOutDate),
                stayGuestLabel = BookingFormatters.formatGuestSummary(
                    rooms = stayDraft.roomCount,
                    adults = stayDraft.adultCount,
                    children = stayDraft.childCount
                ),
                flightTripType = flightDraft.tripType,
                flightDepartureLabel = airportLabel(flightDraft.departureAirportCode, airportMap),
                flightArrivalLabel = airportLabel(flightDraft.arrivalAirportCode, airportMap),
                flightDateLabel = BookingFormatters.formatStayDateRange(
                    flightDraft.departureDate,
                    flightDraft.returnDate
                ),
                flightPassengerLabel = buildFlightPassengerLabel(flightDraft.adultCount, flightDraft.cabinClass),
                flightDirectOnly = flightDraft.directFlightsOnly,
                flightHotelTripType = flightHotelDraft.tripType,
                flightHotelDepartureLabel = airportLabel(flightHotelDraft.departureAirportCode, airportMap),
                flightHotelArrivalLabel = airportLabel(flightHotelDraft.arrivalAirportCode, airportMap),
                flightHotelDepartureDateLabel = BookingFormatters.formatLongLocalDate(flightHotelDraft.departureDate),
                flightHotelPassengerLabel = buildFlightHotelPassengerLabel(
                    passengers = flightHotelDraft.passengerCount,
                    cabinClass = flightHotelDraft.cabinClass,
                    rooms = flightHotelDraft.roomCount
                ),
                flightHotelStayDestinationLabel = if (flightHotelDraft.differentCityAndDates) {
                    flightHotelDraft.stayDestinationQuery.ifBlank { flightHotelArrivalCity }
                } else {
                    flightHotelArrivalCity
                },
                flightHotelStayDateLabel = BookingFormatters.formatStayDateRange(
                    if (flightHotelDraft.differentCityAndDates) {
                        flightHotelDraft.checkInDate
                    } else {
                        flightHotelDraft.departureDate
                    },
                    if (flightHotelDraft.differentCityAndDates) {
                        flightHotelDraft.checkOutDate
                    } else {
                        flightHotelDraft.departureDate.plusDays(2)
                    }
                ),
                flightHotelDifferentCityAndDates = flightHotelDraft.differentCityAndDates,
                carReturnToSameLocation = carDraft.returnToSameLocation,
                carPickupLocationLabel = carDraft.pickupLocation,
                carDateLabel = buildCarRentalDateLabel(carDraft),
                carDriverAgeLabel = "Driver's age: ${carDraft.driverAgeBand}",
                taxiTripType = taxiDraft.tripType,
                taxiPickupLocationLabel = taxiDraft.pickupLocation,
                taxiDestinationLabel = taxiDraft.destination,
                taxiTimeLabel = buildTaxiTimeLabel(taxiDraft),
                taxiPassengerLabel = "${taxiDraft.passengerCount} passenger" + if (taxiDraft.passengerCount == 1) "" else "s",
                taxiRecentItems = taxiRecentItems,
                attractionDestinationLabel = attractionDraft.destinationQuery,
                attractionDateLabel = if (attractionDraft.selectedDate != java.time.LocalDate.MIN) {
                    BookingFormatters.formatLongLocalDate(attractionDraft.selectedDate)
                } else {
                    "Any dates"
                },
                attractionCards = attractionCards,
                recentItems = recentItems,
                destinationCards = destinationCards,
                popularCarCompanies = carRentals.map { it.companyName }.distinct().take(8),
                continueBookingCard = continueHotel?.let { hotel ->
                    ContinueBookingCardUiModel(
                        title = hotel.name,
                        subtitle = hotel.address,
                        footnote = "${BookingFormatters.formatStayDateRange(stayDraft.checkInDate, stayDraft.checkOutDate)} · ${
                            max(1, stayDraft.adultCount)
                        } adult${if (stayDraft.adultCount == 1) "" else "s"}"
                    )
                },
                flightAirports = airports.map { airport ->
                    AirportOptionUiModel(
                        code = airport.code,
                        label = airportLabel(airport.code, airportMap),
                        city = airport.city
                    )
                },
                carPickupLocations = carRentals.map { it.pickupLocation }.distinct().sorted()
            )
        )
    }

    override fun applyFeaturedDestination(destination: String) {
        StayDraftStore.update { draft ->
            draft.copy(destinationQuery = destination.trim())
        }
    }

    override fun submitStaySearch(context: Context) {
        StayDraftStore.prepareForSearch()
        val draft = StayDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "stay_search_${UUID.randomUUID()}",
                searchType = "STAY_SEARCH_SUBMITTED",
                destination = draft.destinationQuery.ifBlank { "All destinations" },
                checkInDate = BookingFormatters.localDateToEpochMillis(draft.checkInDate),
                checkOutDate = BookingFormatters.localDateToEpochMillis(draft.checkOutDate),
                guestCount = draft.totalGuests,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    override fun submitFlightSearch(context: Context) {
        FlightDraftStore.prepareForSearch()
        val draft = FlightDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "flight_search_${UUID.randomUUID()}",
                searchType = "FLIGHT_SEARCH_SUBMITTED",
                destination = "${draft.departureAirportCode} -> ${draft.arrivalAirportCode}",
                checkInDate = BookingFormatters.localDateToEpochMillis(draft.departureDate),
                checkOutDate = draft.returnDate.let(BookingFormatters::localDateToEpochMillis),
                guestCount = draft.adultCount,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    override fun submitFlightHotelSearch(context: Context) {
        val combinedDraft = FlightPlusHotelDraftStore.snapshot()
        val airports = DataRepository.loadAirports(context).associateBy { it.code }
        val arrivalCity = airports[combinedDraft.arrivalAirportCode]?.city
            ?: combinedDraft.stayDestinationQuery

        FlightDraftStore.update { draft ->
            draft.copy(
                tripType = when (combinedDraft.tripType) {
                    FlightHotelTripType.OneWay -> FlightTripType.OneWay
                    FlightHotelTripType.RoundTrip -> FlightTripType.RoundTrip
                },
                departureAirportCode = combinedDraft.departureAirportCode,
                arrivalAirportCode = combinedDraft.arrivalAirportCode,
                departureDate = combinedDraft.departureDate,
                returnDate = if (combinedDraft.tripType == FlightHotelTripType.RoundTrip) {
                    combinedDraft.checkOutDate
                } else {
                    combinedDraft.departureDate.plusDays(2)
                },
                adultCount = max(1, combinedDraft.passengerCount),
                cabinClass = combinedDraft.cabinClass
            )
        }
        FlightDraftStore.prepareForSearch()

        StayDraftStore.update { draft ->
            draft.copy(
                destinationQuery = if (combinedDraft.differentCityAndDates) {
                    combinedDraft.stayDestinationQuery.ifBlank { arrivalCity }
                } else {
                    arrivalCity
                },
                checkInDate = if (combinedDraft.differentCityAndDates) {
                    combinedDraft.checkInDate
                } else {
                    combinedDraft.departureDate
                },
                checkOutDate = if (combinedDraft.differentCityAndDates) {
                    combinedDraft.checkOutDate
                } else {
                    combinedDraft.departureDate.plusDays(2)
                },
                roomCount = max(1, combinedDraft.roomCount),
                adultCount = max(1, combinedDraft.passengerCount),
                childCount = 0
            )
        }
        StayDraftStore.prepareForSearch()

        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "flight_hotel_search_${UUID.randomUUID()}",
                searchType = "FLIGHT_HOTEL_SEARCH_SUBMITTED",
                destination = "${combinedDraft.departureAirportCode} -> ${combinedDraft.arrivalAirportCode} / ${
                    if (combinedDraft.differentCityAndDates) {
                        combinedDraft.stayDestinationQuery.ifBlank { arrivalCity }
                    } else {
                        arrivalCity
                    }
                }",
                checkInDate = BookingFormatters.localDateToEpochMillis(combinedDraft.departureDate),
                checkOutDate = BookingFormatters.localDateToEpochMillis(
                    if (combinedDraft.differentCityAndDates) {
                        combinedDraft.checkOutDate
                    } else {
                        combinedDraft.departureDate.plusDays(2)
                    }
                ),
                guestCount = max(1, combinedDraft.passengerCount),
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    override fun submitCarRentalSearch(context: Context) {
        CarRentalDraftStore.prepareForSearch()
        val draft = CarRentalDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "car_rental_search_${UUID.randomUUID()}",
                searchType = "CAR_RENTAL_SEARCH_SUBMITTED",
                destination = draft.pickupLocation,
                checkInDate = BookingFormatters.localDateTimeToEpochMillis(draft.pickupDateTime),
                checkOutDate = BookingFormatters.localDateTimeToEpochMillis(draft.returnDateTime),
                guestCount = 1,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    override fun submitTaxiSearch(context: Context) {
        TaxiDraftStore.prepareForSearch()
        val draft = TaxiDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "taxi_search_${UUID.randomUUID()}",
                searchType = "TAXI_SEARCH_SUBMITTED",
                destination = "${draft.pickupLocation} -> ${draft.destination}",
                checkInDate = BookingFormatters.localDateTimeToEpochMillis(draft.pickupDateTime),
                checkOutDate = if (draft.tripType == com.example.booking.presentation.taxi.common.TaxiTripType.RoundTrip) {
                    BookingFormatters.localDateTimeToEpochMillis(draft.returnDateTime)
                } else {
                    null
                },
                guestCount = draft.passengerCount,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    override fun submitAttractionSearch(context: Context) {
        AttractionDraftStore.prepareForSearch()
        val draft = AttractionDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "attraction_search_${UUID.randomUUID()}",
                searchType = "ATTRACTION_SEARCH_SUBMITTED",
                destination = draft.destinationQuery,
                checkInDate = BookingFormatters.localDateToEpochMillis(draft.selectedDate),
                checkOutDate = null,
                guestCount = 1,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    private fun airportLabel(
        code: String,
        airportMap: Map<String, com.example.booking.model.Airport>
    ): String {
        val airport = airportMap[code] ?: return code
        return "${airport.code} ${airport.city}"
    }

    private fun buildFlightPassengerLabel(adults: Int, cabinClass: String): String {
        return "$adults adult${if (adults == 1) "" else "s"} · $cabinClass"
    }

    private fun buildFlightHotelPassengerLabel(
        passengers: Int,
        cabinClass: String,
        rooms: Int
    ): String {
        return "$passengers passenger${if (passengers == 1) "" else "s"}, $cabinClass, $rooms room${if (rooms == 1) "" else "s"}"
    }

    private fun buildCarRentalDateLabel(
        draft: com.example.booking.presentation.carrentals.common.CarRentalDraft
    ): String {
        return "${BookingFormatters.formatShortLocalDate(draft.pickupDateTime.toLocalDate())} at ${
            BookingFormatters.formatTime(draft.pickupDateTime)
        } - ${BookingFormatters.formatShortLocalDate(draft.returnDateTime.toLocalDate())} at ${
            BookingFormatters.formatTime(draft.returnDateTime)
        }"
    }

    private fun buildTaxiTimeLabel(
        draft: com.example.booking.presentation.taxi.common.TaxiDraft
    ): String {
        return if (draft.tripType == com.example.booking.presentation.taxi.common.TaxiTripType.RoundTrip) {
            "${BookingFormatters.formatShortLocalDate(draft.pickupDateTime.toLocalDate())} ${
                BookingFormatters.formatTime(draft.pickupDateTime)
            } - ${BookingFormatters.formatShortLocalDate(draft.returnDateTime.toLocalDate())} ${
                BookingFormatters.formatTime(draft.returnDateTime)
            }"
        } else {
            "${BookingFormatters.formatShortLocalDate(draft.pickupDateTime.toLocalDate())} at ${
                BookingFormatters.formatTime(draft.pickupDateTime)
            }"
        }
    }
}
