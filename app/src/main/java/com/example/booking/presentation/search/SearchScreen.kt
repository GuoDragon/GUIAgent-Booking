package com.example.booking.presentation.search

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.booking.presentation.attractions.common.AttractionDraftStore
import com.example.booking.presentation.carrentals.common.CarRentalDraftStore
import com.example.booking.presentation.flightplushotel.FlightHotelTripType
import com.example.booking.presentation.flightplushotel.FlightPlusHotelDraft
import com.example.booking.presentation.flightplushotel.FlightPlusHotelDraftStore
import com.example.booking.presentation.flights.common.FlightDraftStore
import com.example.booking.presentation.flights.common.FlightTripType
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.stays.input.StayGuestsSheet
import com.example.booking.presentation.taxi.common.TaxiDraftStore
import com.example.booking.presentation.taxi.common.TaxiTripType
import com.example.booking.ui.components.BookingHomeTopBar
import com.example.booking.ui.components.BookingTopBarAction
import java.time.LocalDate

@Composable
fun SearchScreen(
    onStayDestinationClick: () -> Unit,
    onStayDateClick: () -> Unit,
    onStaySearchClick: () -> Unit,
    onFlightDateClick: () -> Unit,
    onFlightSearchClick: () -> Unit,
    onFlightHotelSearchClick: () -> Unit,
    onCarDateClick: () -> Unit,
    onCarSearchClick: () -> Unit,
    onTaxiPickupClick: () -> Unit,
    onTaxiDestinationClick: () -> Unit,
    onTaxiTimeClick: () -> Unit,
    onTaxiPassengersClick: () -> Unit,
    onTaxiSearchClick: () -> Unit,
    onAttractionDestinationClick: () -> Unit,
    onAttractionDateClick: () -> Unit,
    onAttractionSearchClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val stayDraft = StayDraftStore.snapshot()
    val flightDraft = FlightDraftStore.snapshot()
    val flightHotelDraft = FlightPlusHotelDraftStore.snapshot()
    val carDraft = CarRentalDraftStore.snapshot()
    val taxiDraft = TaxiDraftStore.snapshot()
    val attractionDraft = AttractionDraftStore.snapshot()
    var selectedProductName by rememberSaveable { mutableStateOf(SearchProduct.Stays.name) }
    val selectedProduct = remember(selectedProductName) { SearchProduct.valueOf(selectedProductName) }
    var uiState by remember { mutableStateOf(SearchUiState()) }
    var showStayGuestsSheet by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : SearchContract.View {
            override fun showState(state: SearchUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { SearchPresenter(view) }

    LaunchedEffect(presenter, context, stayDraft, flightDraft, flightHotelDraft, carDraft, taxiDraft, attractionDraft) {
        presenter.loadData(context)
    }

    fun selectProduct(product: SearchProduct) {
        selectedProductName = product.name
        if (product != SearchProduct.Stays) {
            showStayGuestsSheet = false
        }
    }

    fun nextAirportCode(currentCode: String, excludedCode: String? = null): String {
        val options = uiState.flightAirports.map { it.code }.filter { it != excludedCode }
        if (options.isEmpty()) return currentCode
        val currentIndex = options.indexOf(currentCode)
        return options[(if (currentIndex == -1) 0 else currentIndex + 1) % options.size]
    }

    fun nextFromList(currentValue: String, options: List<String>): String {
        if (options.isEmpty()) return currentValue
        val currentIndex = options.indexOf(currentValue)
        return options[(if (currentIndex == -1) 0 else currentIndex + 1) % options.size]
    }

    fun nextCabin(currentValue: String): String = nextFromList(currentValue, uiState.flightCabinOptions)

    fun updateFlightHotelDraft(transform: (FlightPlusHotelDraft) -> FlightPlusHotelDraft) {
        FlightPlusHotelDraftStore.update { current ->
            normalizeFlightHotelDraft(transform(current), uiState.flightAirports)
        }
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            BookingHomeTopBar(title = "Booking.com") {
                BookingTopBarAction(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Messages"
                )
                BookingTopBarAction(
                    icon = Icons.Outlined.NotificationsNone,
                    contentDescription = "Notifications"
                )
            }
        }
    ) { innerPadding ->
        when (selectedProduct) {
            SearchProduct.Stays -> StaysSearchContent(
                uiState = uiState,
                topPadding = innerPadding.calculateTopPadding(),
                onProductSelected = ::selectProduct,
                onDestinationClick = onStayDestinationClick,
                onDateClick = onStayDateClick,
                onGuestsClick = { showStayGuestsSheet = true },
                onSearchClick = {
                    presenter.submitStaySearch(context)
                    onStaySearchClick()
                },
                onDestinationCardClick = { destination ->
                    presenter.applyFeaturedDestination(destination)
                    presenter.submitStaySearch(context)
                    onStaySearchClick()
                }
            )

            SearchProduct.Flights -> FlightsSearchContent(
                uiState = uiState,
                topPadding = innerPadding.calculateTopPadding(),
                onProductSelected = ::selectProduct,
                onTripTypeSelected = { tripType ->
                    FlightDraftStore.update { draft -> draft.copy(tripType = tripType) }
                },
                onDepartureClick = {
                    FlightDraftStore.update { draft ->
                        draft.copy(
                            departureAirportCode = nextAirportCode(
                                currentCode = draft.departureAirportCode,
                                excludedCode = draft.arrivalAirportCode
                            )
                        )
                    }
                },
                onArrivalClick = {
                    FlightDraftStore.update { draft ->
                        draft.copy(
                            arrivalAirportCode = nextAirportCode(
                                currentCode = draft.arrivalAirportCode,
                                excludedCode = draft.departureAirportCode
                            )
                        )
                    }
                },
                onSwapAirportsClick = {
                    FlightDraftStore.update { draft ->
                        draft.copy(
                            departureAirportCode = draft.arrivalAirportCode,
                            arrivalAirportCode = draft.departureAirportCode
                        )
                    }
                },
                onDateClick = onFlightDateClick,
                onAdultCountChange = { delta ->
                    FlightDraftStore.update { draft ->
                        draft.copy(adultCount = (draft.adultCount + delta).coerceIn(1, 6))
                    }
                },
                onCabinClassClick = {
                    FlightDraftStore.update { draft -> draft.copy(cabinClass = nextCabin(draft.cabinClass)) }
                },
                onDirectOnlyChanged = { checked ->
                    FlightDraftStore.update { draft -> draft.copy(directFlightsOnly = checked) }
                },
                onSearchClick = {
                    presenter.submitFlightSearch(context)
                    onFlightSearchClick()
                }
            )

            SearchProduct.FlightHotel -> FlightHotelSearchContent(
                uiState = uiState,
                topPadding = innerPadding.calculateTopPadding(),
                onProductSelected = ::selectProduct,
                onTripTypeSelected = { tripType ->
                    updateFlightHotelDraft { draft -> draft.copy(tripType = tripType) }
                },
                onDepartureClick = {
                    updateFlightHotelDraft { draft ->
                        draft.copy(
                            departureAirportCode = nextAirportCode(
                                currentCode = draft.departureAirportCode,
                                excludedCode = draft.arrivalAirportCode
                            )
                        )
                    }
                },
                onArrivalClick = {
                    updateFlightHotelDraft { draft ->
                        draft.copy(
                            arrivalAirportCode = nextAirportCode(
                                currentCode = draft.arrivalAirportCode,
                                excludedCode = draft.departureAirportCode
                            )
                        )
                    }
                },
                onDepartureDateClick = {
                    updateFlightHotelDraft { draft -> draft.copy(departureDate = draft.departureDate.plusDays(1)) }
                },
                onPassengerCountChange = { delta ->
                    updateFlightHotelDraft { draft ->
                        draft.copy(passengerCount = (draft.passengerCount + delta).coerceIn(1, 6))
                    }
                },
                onRoomCountChange = { delta ->
                    updateFlightHotelDraft { draft ->
                        draft.copy(roomCount = (draft.roomCount + delta).coerceIn(1, 4))
                    }
                },
                onCabinClassClick = {
                    updateFlightHotelDraft { draft -> draft.copy(cabinClass = nextCabin(draft.cabinClass)) }
                },
                onDifferentCityAndDatesChanged = { checked ->
                    updateFlightHotelDraft { draft -> draft.copy(differentCityAndDates = checked) }
                },
                onStayDestinationClick = {
                    val options = buildList {
                        uiState.flightAirports.firstOrNull { it.code == flightHotelDraft.arrivalAirportCode }?.city?.let(::add)
                        addAll(uiState.destinationCards.map { it.title })
                    }.distinct()
                    updateFlightHotelDraft { draft ->
                        draft.copy(stayDestinationQuery = nextFromList(draft.stayDestinationQuery, options))
                    }
                },
                onStayDatesClick = {
                    updateFlightHotelDraft { draft ->
                        draft.copy(
                            checkInDate = draft.checkInDate.plusDays(1),
                            checkOutDate = draft.checkOutDate.plusDays(1)
                        )
                    }
                },
                onSearchClick = {
                    presenter.submitFlightHotelSearch(context)
                    onFlightHotelSearchClick()
                }
            )

            SearchProduct.CarRental -> CarRentalSearchContent(
                uiState = uiState,
                topPadding = innerPadding.calculateTopPadding(),
                onProductSelected = ::selectProduct,
                onReturnToSameLocationChanged = { checked ->
                    CarRentalDraftStore.update { draft -> draft.copy(returnToSameLocation = checked) }
                },
                onPickupLocationClick = {
                    CarRentalDraftStore.update { draft ->
                        draft.copy(pickupLocation = nextFromList(draft.pickupLocation, uiState.carPickupLocations))
                    }
                },
                onDateClick = onCarDateClick,
                onDriverAgeClick = {
                    CarRentalDraftStore.update { draft ->
                        draft.copy(driverAgeBand = nextFromList(draft.driverAgeBand, uiState.carDriverAgeOptions))
                    }
                },
                onSearchClick = {
                    presenter.submitCarRentalSearch(context)
                    onCarSearchClick()
                }
            )

            SearchProduct.Taxi -> TaxiSearchContent(
                uiState = uiState,
                topPadding = innerPadding.calculateTopPadding(),
                onProductSelected = ::selectProduct,
                onTripTypeSelected = { tripType ->
                    TaxiDraftStore.update { draft ->
                        draft.copy(
                            tripType = tripType,
                            returnDateTime = if (tripType == TaxiTripType.RoundTrip) {
                                draft.returnDateTime
                            } else {
                                draft.pickupDateTime.plusHours(6)
                            }
                        )
                    }
                },
                onPickupLocationClick = onTaxiPickupClick,
                onDestinationClick = onTaxiDestinationClick,
                onTimeClick = onTaxiTimeClick,
                onPassengersClick = onTaxiPassengersClick,
                onSearchClick = {
                    presenter.submitTaxiSearch(context)
                    onTaxiSearchClick()
                }
            )

            SearchProduct.Attractions -> AttractionsSearchContent(
                uiState = uiState,
                topPadding = innerPadding.calculateTopPadding(),
                onProductSelected = ::selectProduct,
                onDestinationClick = onAttractionDestinationClick,
                onDateClick = onAttractionDateClick,
                onSearchClick = {
                    presenter.submitAttractionSearch(context)
                    onAttractionSearchClick()
                }
            )
        }

        if (showStayGuestsSheet) {
            StayGuestsSheet(
                onDismissRequest = { showStayGuestsSheet = false },
                onApplyClick = { showStayGuestsSheet = false }
            )
        }
    }
}

private fun normalizeFlightHotelDraft(
    draft: FlightPlusHotelDraft,
    airportOptions: List<AirportOptionUiModel>
): FlightPlusHotelDraft {
    val arrivalCity = airportOptions.firstOrNull { it.code == draft.arrivalAirportCode }?.city
        ?: draft.stayDestinationQuery
    val normalizedCheckIn = if (draft.differentCityAndDates) draft.checkInDate else draft.departureDate
    val desiredCheckOut = if (draft.differentCityAndDates) draft.checkOutDate else draft.departureDate.plusDays(2)
    val normalizedCheckOut = if (desiredCheckOut <= normalizedCheckIn) {
        normalizedCheckIn.plusDays(2)
    } else {
        desiredCheckOut
    }

    return draft.copy(
        passengerCount = draft.passengerCount.coerceAtLeast(1),
        roomCount = draft.roomCount.coerceAtLeast(1),
        stayDestinationQuery = if (draft.differentCityAndDates) {
            draft.stayDestinationQuery.ifBlank { arrivalCity }
        } else {
            arrivalCity
        },
        checkInDate = normalizedCheckIn,
        checkOutDate = normalizedCheckOut
    )
}
