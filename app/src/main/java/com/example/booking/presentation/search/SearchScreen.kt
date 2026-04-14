package com.example.booking.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.flightplushotel.FlightHotelTripType
import com.example.booking.presentation.stays.input.StayGuestsSheet
import com.example.booking.presentation.taxi.common.TaxiTripType
import com.example.booking.ui.components.BookingHomeTopBar
import com.example.booking.ui.components.BookingTopBarAction
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingWhite

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
    onTaxiSearchClick: () -> Unit,
    onAttractionDestinationClick: () -> Unit,
    onAttractionDateClick: () -> Unit,
    onAttractionSearchClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var selectedProductName by rememberSaveable { mutableStateOf(SearchProduct.Stays.name) }
    val selectedProduct = remember(selectedProductName) { SearchProduct.valueOf(selectedProductName) }
    var uiState by remember { mutableStateOf(SearchUiState()) }
    var showStayGuestsSheet by rememberSaveable { mutableStateOf(false) }
    var showTaxiRoutePlanner by rememberSaveable { mutableStateOf(false) }
    var taxiRoutePlannerFocusName by rememberSaveable { mutableStateOf(TaxiRoutePlannerFocus.Pickup.name) }
    var showTaxiOutboundTimeSheet by rememberSaveable { mutableStateOf(false) }
    var showTaxiReturnTimeSheet by rememberSaveable { mutableStateOf(false) }
    var showTaxiPassengersSheet by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : SearchContract.View {
            override fun showState(state: SearchUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { SearchPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    fun selectProduct(product: SearchProduct) {
        selectedProductName = product.name
        if (product != SearchProduct.Stays) {
            showStayGuestsSheet = false
        }
        if (product != SearchProduct.Taxi) {
            showTaxiRoutePlanner = false
            showTaxiOutboundTimeSheet = false
            showTaxiReturnTimeSheet = false
            showTaxiPassengersSheet = false
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .background(BookingWhite)
        ) {
            SearchProductTopTabs(
                selectedProduct = selectedProduct,
                onProductSelected = ::selectProduct,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                when (selectedProduct) {
                    SearchProduct.Stays -> StaysSearchContent(
                        uiState = uiState,
                        topPadding = 0.dp,
                        onDestinationClick = onStayDestinationClick,
                        onDateClick = onStayDateClick,
                        onGuestsClick = { showStayGuestsSheet = true },
                        onSearchClick = {
                            presenter.submitStaySearch(context)
                            onStaySearchClick()
                        },
                        onDestinationCardClick = { destination ->
                            presenter.applyFeaturedDestination(context, destination)
                            presenter.submitStaySearch(context)
                            onStaySearchClick()
                        }
                    )

                    SearchProduct.Flights -> FlightsSearchContent(
                        uiState = uiState,
                        topPadding = 0.dp,
                        onTripTypeSelected = { tripType ->
                            presenter.selectFlightTripType(context, tripType)
                        },
                        onDepartureClick = {
                            presenter.selectFlightDepartureAirport(
                                context = context,
                                airportCode = nextAirportCode(
                                    currentCode = uiState.flightDepartureCode,
                                    excludedCode = uiState.flightArrivalCode
                                )
                            )
                        },
                        onArrivalClick = {
                            presenter.selectFlightArrivalAirport(
                                context = context,
                                airportCode = nextAirportCode(
                                    currentCode = uiState.flightArrivalCode,
                                    excludedCode = uiState.flightDepartureCode
                                )
                            )
                        },
                        onSwapAirportsClick = {
                            presenter.swapFlightAirports(context)
                        },
                        onDateClick = onFlightDateClick,
                        onAdultCountChange = { delta ->
                            presenter.changeFlightAdultCount(context, delta)
                        },
                        onCabinClassClick = {
                            presenter.selectFlightCabinClass(
                                context = context,
                                cabinClass = nextCabin(uiState.flightCabinClass)
                            )
                        },
                        onDirectOnlyChanged = { checked ->
                            presenter.setFlightDirectOnly(context, checked)
                        },
                        onSearchClick = {
                            presenter.submitFlightSearch(context)
                            onFlightSearchClick()
                        }
                    )

                    SearchProduct.FlightHotel -> FlightHotelSearchContent(
                        uiState = uiState,
                        topPadding = 0.dp,
                        onTripTypeSelected = { tripType ->
                            presenter.selectFlightHotelTripType(context, tripType)
                        },
                        onDepartureClick = {
                            presenter.selectFlightHotelDepartureAirport(
                                context = context,
                                airportCode = nextAirportCode(
                                    currentCode = uiState.flightHotelDepartureCode,
                                    excludedCode = uiState.flightHotelArrivalCode
                                )
                            )
                        },
                        onArrivalClick = {
                            presenter.selectFlightHotelArrivalAirport(
                                context = context,
                                airportCode = nextAirportCode(
                                    currentCode = uiState.flightHotelArrivalCode,
                                    excludedCode = uiState.flightHotelDepartureCode
                                )
                            )
                        },
                        onDepartureDateClick = {
                            presenter.setFlightHotelDepartureDate(
                                context = context,
                                departureDate = uiState.flightHotelDepartureDate.plusDays(1)
                            )
                        },
                        onPassengerCountChange = { delta ->
                            presenter.changeFlightHotelPassengerCount(context, delta)
                        },
                        onRoomCountChange = { delta ->
                            presenter.changeFlightHotelRoomCount(context, delta)
                        },
                        onCabinClassClick = {
                            presenter.selectFlightHotelCabinClass(
                                context = context,
                                cabinClass = nextCabin(uiState.flightHotelCabinClass)
                            )
                        },
                        onDifferentCityAndDatesChanged = { checked ->
                            presenter.setFlightHotelDifferentCityAndDates(
                                context = context,
                                checked = checked,
                                airportOptions = uiState.flightAirports
                            )
                        },
                        onStayDestinationClick = {
                            val options = buildList {
                                uiState.flightAirports.firstOrNull { it.code == uiState.flightHotelArrivalCode }?.city?.let(::add)
                                addAll(uiState.destinationCards.map { it.title })
                            }.distinct()
                            presenter.selectFlightHotelStayDestination(
                                context = context,
                                destination = nextFromList(uiState.flightHotelStayDestinationQuery, options),
                                airportOptions = uiState.flightAirports
                            )
                        },
                        onStayDatesClick = {
                            presenter.shiftFlightHotelStayDates(
                                context = context,
                                days = 1,
                                airportOptions = uiState.flightAirports
                            )
                        },
                        onSearchClick = {
                            presenter.submitFlightHotelSearch(context)
                            onFlightHotelSearchClick()
                        }
                    )

                    SearchProduct.CarRental -> CarRentalSearchContent(
                        uiState = uiState,
                        topPadding = 0.dp,
                        onReturnToSameLocationChanged = { checked ->
                            presenter.setCarReturnToSameLocation(context, checked)
                        },
                        onPickupLocationClick = {
                            presenter.selectCarPickupLocation(
                                context = context,
                                pickupLocation = nextFromList(uiState.carPickupLocation, uiState.carPickupLocations)
                            )
                        },
                        onDateClick = onCarDateClick,
                        onDriverAgeChange = { value ->
                            presenter.setCarDriverAge(context, value)
                        },
                        onSearchClick = {
                            presenter.submitCarRentalSearch(context)
                            onCarSearchClick()
                        }
                    )

                    SearchProduct.Taxi -> TaxiSearchContent(
                        uiState = uiState,
                        topPadding = 0.dp,
                        onTripTypeSelected = { tripType ->
                            presenter.selectTaxiTripType(context, tripType)
                            if (tripType != TaxiTripType.RoundTrip) {
                                showTaxiReturnTimeSheet = false
                            }
                        },
                        onPickupLocationClick = {
                            taxiRoutePlannerFocusName = TaxiRoutePlannerFocus.Pickup.name
                            showTaxiRoutePlanner = true
                        },
                        onDestinationClick = {
                            taxiRoutePlannerFocusName = TaxiRoutePlannerFocus.Destination.name
                            showTaxiRoutePlanner = true
                        },
                        onTimeClick = { showTaxiOutboundTimeSheet = true },
                        onReturnTimeClick = { showTaxiReturnTimeSheet = true },
                        onPassengersClick = { showTaxiPassengersSheet = true },
                        onSearchClick = {
                            presenter.submitTaxiSearch(context)
                            onTaxiSearchClick()
                        }
                    )

                    SearchProduct.Attractions -> AttractionsSearchContent(
                        uiState = uiState,
                        topPadding = 0.dp,
                        onDestinationClick = onAttractionDestinationClick,
                        onDateClick = onAttractionDateClick,
                        onSearchClick = {
                            presenter.submitAttractionSearch(context)
                            onAttractionSearchClick()
                        }
                    )
                }
            }
        }

        if (showStayGuestsSheet) {
            StayGuestsSheet(
                onDismissRequest = { showStayGuestsSheet = false },
                onApplyClick = { showStayGuestsSheet = false }
            )
        }

        if (showTaxiRoutePlanner) {
            TaxiRoutePlannerDialog(
                initialPickupLocation = uiState.taxiPickupLocation,
                initialDestination = uiState.taxiDestination,
                focus = TaxiRoutePlannerFocus.valueOf(taxiRoutePlannerFocusName),
                onDismissRequest = { showTaxiRoutePlanner = false },
                onConfirm = { pickupLocation, destination ->
                    presenter.setTaxiRoute(context, pickupLocation, destination)
                }
            )
        }

        if (showTaxiOutboundTimeSheet) {
            val nowOption = java.time.LocalDateTime.now().withSecond(0).withNano(0)
            val tomorrowNoonOption = java.time.LocalDate.now().plusDays(1).atTime(12, 0)
            val dayAfterTomorrowMorningOption = java.time.LocalDate.now().plusDays(2).atTime(8, 0)
            TaxiScheduleTimeSheet(
                title = "Schedule outbound ride",
                helperText = "When do you want to be picked up?",
                initialDateTime = uiState.taxiPickupDateTime,
                quickOptions = listOf(
                    TaxiQuickTimeOption(label = "Now", dateTime = nowOption),
                    TaxiQuickTimeOption(label = "Tomorrow 12:00", dateTime = tomorrowNoonOption),
                    TaxiQuickTimeOption(label = "Day+2 08:00", dateTime = dayAfterTomorrowMorningOption)
                ),
                onDismissRequest = { showTaxiOutboundTimeSheet = false },
                onConfirm = { outboundDateTime ->
                    presenter.setTaxiPickupDateTime(context, outboundDateTime)
                }
            )
        }

        if (showTaxiReturnTimeSheet && uiState.taxiTripType == TaxiTripType.RoundTrip) {
            val tomorrowNoonOption = java.time.LocalDate.now().plusDays(1).atTime(12, 0)
            val dayAfterTomorrowMorningOption = java.time.LocalDate.now().plusDays(2).atTime(8, 0)
            TaxiScheduleTimeSheet(
                title = "Schedule return ride",
                helperText = "When do you want to be picked up?",
                initialDateTime = uiState.taxiReturnDateTime,
                quickOptions = listOf(
                    TaxiQuickTimeOption(label = "Tomorrow 12:00", dateTime = tomorrowNoonOption),
                    TaxiQuickTimeOption(label = "Day+2 08:00", dateTime = dayAfterTomorrowMorningOption)
                ),
                onDismissRequest = { showTaxiReturnTimeSheet = false },
                onConfirm = { returnDateTime ->
                    presenter.setTaxiReturnDateTime(context, returnDateTime)
                }
            )
        }

        if (showTaxiPassengersSheet) {
            TaxiPassengersSheet(
                initialPassengers = uiState.taxiPassengerCount,
                onDismissRequest = { showTaxiPassengersSheet = false },
                onDoneClick = { passengers ->
                    presenter.setTaxiPassengerCount(context, passengers)
                }
            )
        }
    }
}

@Composable
private fun SearchProductTopTabs(
    selectedProduct: SearchProduct,
    onProductSelected: (SearchProduct) -> Unit,
    modifier: Modifier = Modifier
) {
    val products = listOf(
        SearchProduct.Stays to Icons.Filled.Hotel,
        SearchProduct.Flights to Icons.Filled.FlightTakeoff,
        SearchProduct.FlightHotel to Icons.Filled.Luggage,
        SearchProduct.CarRental to Icons.Filled.DirectionsCar,
        SearchProduct.Taxi to Icons.Filled.LocalTaxi,
        SearchProduct.Attractions to Icons.Filled.LocalActivity
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        products.chunked(3).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { (product, icon) ->
                    SearchProductTopChip(
                        icon = icon,
                        title = product.title,
                        selected = product == selectedProduct,
                        modifier = Modifier.weight(1f),
                        onClick = { onProductSelected(product) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchProductTopChip(
    icon: ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = if (selected) BookingBlueLight.copy(alpha = 0.12f) else BookingWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (selected) BookingBlueLight else BookingGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) BookingBlue else BookingTextPrimary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (selected) BookingBlue else BookingTextPrimary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
