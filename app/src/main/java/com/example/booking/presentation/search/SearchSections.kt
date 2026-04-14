package com.example.booking.presentation.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.LocalTaxi
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.booking.presentation.flightplushotel.FlightHotelTripType
import com.example.booking.presentation.flights.common.FlightTripType
import com.example.booking.presentation.taxi.common.TaxiTripType
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun StaysSearchContent(
    uiState: SearchUiState,
    topPadding: Dp,
    onDestinationClick: () -> Unit,
    onDateClick: () -> Unit,
    onGuestsClick: () -> Unit,
    onSearchClick: () -> Unit,
    onDestinationCardClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(22.dp),
        contentPadding = PaddingValues(top = topPadding, bottom = 28.dp)
    ) {
        item {
            StaysHeroSection(
                uiState = uiState,
                onDestinationClick = onDestinationClick,
                onDateClick = onDateClick,
                onGuestsClick = onGuestsClick,
                onSearchClick = onSearchClick
            )
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                BookingSectionHeader(title = "Continue searching")
                Spacer(modifier = Modifier.height(14.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    itemsIndexed(uiState.recentItems) { index, item ->
                        RecentSearchCard(
                            item = item,
                            accentColor = if (index % 2 == 0) Color(0xFFF2F7FF) else Color(0xFFFFF3E0)
                        )
                    }
                }
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                BookingSectionHeader(
                    title = "Explore destinations",
                    subtitle = "Tap a city to reuse the existing stay results flow."
                )
                Spacer(modifier = Modifier.height(14.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    itemsIndexed(uiState.destinationCards) { index, item ->
                        DestinationCard(
                            item = item,
                            gradient = when (index % 3) {
                                0 -> listOf(Color(0xFF4E8DFF), Color(0xFF7AB8FF))
                                1 -> listOf(Color(0xFF6EC6FF), Color(0xFFB8E3FF))
                                else -> listOf(Color(0xFF0059B2), Color(0xFF4EA1FF))
                            },
                            onClick = { onDestinationCardClick(item.title) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlightsSearchContent(
    uiState: SearchUiState,
    topPadding: Dp,
    onTripTypeSelected: (FlightTripType) -> Unit,
    onDepartureClick: () -> Unit,
    onArrivalClick: () -> Unit,
    onSwapAirportsClick: () -> Unit,
    onDateClick: () -> Unit,
    onAdultCountChange: (Int) -> Unit,
    onCabinClassClick: () -> Unit,
    onDirectOnlyChanged: (Boolean) -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BookingWhite)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = topPadding, bottom = 28.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BookingWhite)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    DarkSearchCard {
                        FlightTripTypeRow(
                            selectedTripType = uiState.flightTripType,
                            onTripTypeSelected = onTripTypeSelected
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.FlightTakeoff,
                            text = uiState.flightDepartureLabel,
                            onClick = onDepartureClick,
                            trailingContent = {
                                MiniIconChip(
                                    icon = Icons.Filled.SwapVert,
                                    contentDescription = "Swap airports",
                                    onClick = onSwapAirportsClick
                                )
                            }
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.FlightTakeoff,
                            text = uiState.flightArrivalLabel,
                            onClick = onArrivalClick
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.CalendarToday,
                            text = uiState.flightDateLabel,
                            onClick = onDateClick
                        )
                        SearchFieldRow(
                            icon = Icons.Outlined.PersonOutline,
                            text = uiState.flightPassengerLabel,
                            showDivider = false,
                            onClick = onCabinClassClick
                        )
                        InlineAdjusterRow(
                            label = "Passengers",
                            value = uiState.flightPassengerLabel.substringBefore("·").trim(),
                            onDecrease = { onAdultCountChange(-1) },
                            onIncrease = { onAdultCountChange(1) },
                            trailingText = uiState.flightPassengerLabel.substringAfter("·", "").trim(),
                            onTrailingTextClick = onCabinClassClick
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BookingPrimaryButton(text = "Search", onClick = onSearchClick)
                    }
                    DarkSwitchRow(
                        title = "Direct flights only",
                        checked = uiState.flightDirectOnly,
                        onCheckedChange = onDirectOnlyChanged,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                }
            }
            item {
                PopularCompaniesSection(
                    title = "Popular rental car companies",
                    companies = uiState.popularCarCompanies
                )
            }
            uiState.continueBookingCard?.let { card ->
                item {
                    ContinueBookingSection(card = card)
                }
            }
        }
    }
}

@Composable
fun FlightHotelSearchContent(
    uiState: SearchUiState,
    topPadding: Dp,
    onTripTypeSelected: (FlightHotelTripType) -> Unit,
    onDepartureClick: () -> Unit,
    onArrivalClick: () -> Unit,
    onDepartureDateClick: () -> Unit,
    onPassengerCountChange: (Int) -> Unit,
    onRoomCountChange: (Int) -> Unit,
    onCabinClassClick: () -> Unit,
    onDifferentCityAndDatesChanged: (Boolean) -> Unit,
    onStayDestinationClick: () -> Unit,
    onStayDatesClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BookingWhite)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = topPadding, start = 16.dp, end = 16.dp, bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        SmallToggleChip(
                            text = FlightHotelTripType.OneWay.label,
                            selected = uiState.flightHotelTripType == FlightHotelTripType.OneWay,
                            onClick = { onTripTypeSelected(FlightHotelTripType.OneWay) }
                        )
                        SmallToggleChip(
                            text = FlightHotelTripType.RoundTrip.label,
                            selected = uiState.flightHotelTripType == FlightHotelTripType.RoundTrip,
                            onClick = { onTripTypeSelected(FlightHotelTripType.RoundTrip) }
                        )
                    }
                    SearchFieldRow(
                        icon = Icons.Filled.FlightTakeoff,
                        text = uiState.flightHotelDepartureLabel,
                        onClick = onDepartureClick,
                        modifier = Modifier.padding(top = 14.dp)
                    )
                    SearchFieldRow(
                        icon = Icons.Filled.FlightTakeoff,
                        text = uiState.flightHotelArrivalLabel,
                        onClick = onArrivalClick
                    )
                    SearchFieldRow(
                        icon = Icons.Filled.CalendarToday,
                        text = uiState.flightHotelDepartureDateLabel,
                        onClick = onDepartureDateClick
                    )
                    SearchFieldRow(
                        icon = Icons.Outlined.PersonOutline,
                        text = uiState.flightHotelPassengerLabel,
                        showDivider = false,
                        onClick = onCabinClassClick
                    )
                    InlineAdjusterRow(
                        label = "Passengers",
                        value = uiState.flightHotelPassengerLabel.substringBefore(",").trim(),
                        onDecrease = { onPassengerCountChange(-1) },
                        onIncrease = { onPassengerCountChange(1) },
                        trailingText = uiState.flightHotelPassengerLabel.substringAfter(",").substringBeforeLast(",").trim(),
                        onTrailingTextClick = onCabinClassClick,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    InlineAdjusterRow(
                        label = "Rooms",
                        value = uiState.flightHotelPassengerLabel.substringAfterLast(",").trim(),
                        onDecrease = { onRoomCountChange(-1) },
                        onIncrease = { onRoomCountChange(1) },
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    SearchToggleRow(
                        title = "Need a different city and dates",
                        checked = uiState.flightHotelDifferentCityAndDates,
                        onCheckedChange = onDifferentCityAndDatesChanged,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    SearchFieldRow(
                        icon = Icons.Filled.Place,
                        text = uiState.flightHotelStayDestinationLabel,
                        onClick = onStayDestinationClick,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                    SearchFieldRow(
                        icon = Icons.Filled.Hotel,
                        text = uiState.flightHotelStayDateLabel,
                        showDivider = false,
                        onClick = onStayDatesClick
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    BookingPrimaryButton(text = "Search Flight+Hotel", onClick = onSearchClick)
                }
            }
        }
    }
}

@Composable
fun CarRentalSearchContent(
    uiState: SearchUiState,
    topPadding: Dp,
    onReturnToSameLocationChanged: (Boolean) -> Unit,
    onPickupLocationClick: () -> Unit,
    onDateClick: () -> Unit,
    onDriverAgeChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = topPadding, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BookingWhite)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = BookingWhite
                ) {
                    Column(
                        modifier = Modifier
                            .border(2.dp, Color(0xFFFEBB02), RoundedCornerShape(20.dp))
                            .padding(12.dp)
                    ) {
                        SearchToggleRow(
                            title = "Return to same location",
                            checked = uiState.carReturnToSameLocation,
                            onCheckedChange = onReturnToSameLocationChanged
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.DirectionsCar,
                            text = uiState.carPickupLocationLabel,
                            onClick = onPickupLocationClick
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.CalendarToday,
                            text = uiState.carDateLabel,
                            onClick = onDateClick
                        )
                        OutlinedTextField(
                            value = uiState.carDriverAgeText,
                            onValueChange = { onDriverAgeChange(it.filter(Char::isDigit)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            label = { Text("Driver's age") },
                            placeholder = { Text("30") },
                            singleLine = true,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.PersonOutline,
                                    contentDescription = null
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BookingPrimaryButton(text = "Search", onClick = onSearchClick)
                    }
                }
            }
        }
        item {
            PopularCompaniesSection(
                title = "Popular rental car companies",
                companies = uiState.popularCarCompanies
            )
        }
        uiState.continueBookingCard?.let { card ->
            item {
                ContinueBookingSection(card = card)
            }
        }
    }
}

@Composable
fun TaxiSearchContent(
    uiState: SearchUiState,
    topPadding: Dp,
    onTripTypeSelected: (TaxiTripType) -> Unit,
    onPickupLocationClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onTimeClick: () -> Unit,
    onReturnTimeClick: () -> Unit,
    onPassengersClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = topPadding, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BookingWhite)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = BookingWhite
                ) {
                    Column(
                        modifier = Modifier
                            .border(2.dp, Color(0xFFFEBB02), RoundedCornerShape(20.dp))
                            .padding(12.dp)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            TaxiTripType.entries.forEach { tripType ->
                                SmallToggleChip(
                                    text = tripType.label,
                                    selected = uiState.taxiTripType == tripType,
                                    onClick = { onTripTypeSelected(tripType) }
                                )
                            }
                        }
                        SearchFieldRow(
                            icon = Icons.Filled.Place,
                            text = uiState.taxiPickupLocationLabel,
                            onClick = onPickupLocationClick,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.Place,
                            text = uiState.taxiDestinationLabel,
                            onClick = onDestinationClick
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.CalendarToday,
                            text = uiState.taxiTimeLabel,
                            onClick = onTimeClick
                        )
                        if (uiState.taxiTripType == TaxiTripType.RoundTrip) {
                            SearchFieldRow(
                                icon = Icons.Filled.CalendarToday,
                                text = uiState.taxiReturnTimeLabel,
                                onClick = onReturnTimeClick
                            )
                        }
                        SearchFieldRow(
                            icon = Icons.Outlined.PersonOutline,
                            text = uiState.taxiPassengerLabel,
                            showDivider = false,
                            onClick = onPassengersClick
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BookingPrimaryButton(text = "Check prices", onClick = onSearchClick)
                    }
                }
            }
        }
        if (uiState.taxiRecentItems.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BookingSectionHeader(title = "Continue your search")
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.taxiRecentItems) { item ->
                            BookingRoundedCard(modifier = Modifier.width(220.dp)) {
                                Text(
                                    text = item.title,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = item.subtitle,
                                    color = BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Text(
                                    text = item.meta,
                                    color = BookingBlueLight,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionsSearchContent(
    uiState: SearchUiState,
    topPadding: Dp,
    onDestinationClick: () -> Unit,
    onDateClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = topPadding, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BookingWhite)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = BookingWhite
                ) {
                    Column(
                        modifier = Modifier
                            .border(2.dp, Color(0xFFFEBB02), RoundedCornerShape(20.dp))
                            .padding(12.dp)
                    ) {
                        SearchFieldRow(
                            icon = Icons.Filled.Search,
                            text = uiState.attractionDestinationLabel,
                            onClick = onDestinationClick
                        )
                        SearchFieldRow(
                            icon = Icons.Filled.CalendarToday,
                            text = uiState.attractionDateLabel,
                            showDivider = false,
                            onClick = onDateClick
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        BookingPrimaryButton(text = "Search", onClick = onSearchClick)
                    }
                }
            }
        }
        if (uiState.attractionCards.isNotEmpty()) {
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BookingSectionHeader(
                        title = "Popular things to do",
                        subtitle = "Use the local demo attractions data as the source of truth."
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(uiState.attractionCards) { card ->
                            BookingRoundedCard(modifier = Modifier.width(230.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BookingStatusChip(
                                        text = card.badgeText,
                                        containerColor = Color(0xFFE3F0FF),
                                        contentColor = BookingBlueLight
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.LocalActivity,
                                        contentDescription = null,
                                        tint = BookingBlue
                                    )
                                }
                                Text(
                                    text = card.title,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(top = 14.dp)
                                )
                                Text(
                                    text = card.subtitle,
                                    color = BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Text(
                                    text = card.priceText,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StaysHeroSection(
    uiState: SearchUiState,
    onDestinationClick: () -> Unit,
    onDateClick: () -> Unit,
    onGuestsClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BookingWhite)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            color = BookingWhite,
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .border(2.dp, Color(0xFFFEBB02), RoundedCornerShape(22.dp))
                    .padding(12.dp)
            ) {
                SearchFieldRow(icon = Icons.Filled.Search, text = uiState.stayDestinationLabel, onClick = onDestinationClick)
                SearchFieldRow(icon = Icons.Filled.CalendarToday, text = uiState.stayDateLabel, onClick = onDateClick)
                SearchFieldRow(
                    icon = Icons.Outlined.PersonOutline,
                    text = uiState.stayGuestLabel,
                    showDivider = false,
                    onClick = onGuestsClick
                )
                Spacer(modifier = Modifier.height(12.dp))
                BookingPrimaryButton(text = "Search", onClick = onSearchClick)
            }
        }
    }
}

@Composable
private fun SearchFieldRow(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    showDivider: Boolean = true,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val primaryColor = if (dark) BookingWhite else BookingTextPrimary
    val dividerColor = if (dark) Color.White.copy(alpha = 0.12f) else BookingGray
    Column(modifier = modifier.clickable(onClick = onClick)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = primaryColor)
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = primaryColor,
                modifier = Modifier.weight(1f)
            )
            trailingContent?.invoke()
        }
        if (showDivider) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(dividerColor)
            )
        }
    }
}

@Composable
private fun FlightTripTypeRow(
    selectedTripType: FlightTripType,
    onTripTypeSelected: (FlightTripType) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        FlightTripType.entries.forEach { tripType ->
            SmallToggleChip(
                text = tripType.label,
                selected = tripType == selectedTripType,
                onClick = { onTripTypeSelected(tripType) }
            )
        }
    }
}

@Composable
private fun SmallToggleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    dark: Boolean = false
) {
    val backgroundColor = when {
        selected && dark -> BookingBlueLight
        selected -> BookingBlueLight.copy(alpha = 0.12f)
        dark -> Color.White.copy(alpha = 0.08f)
        else -> BookingWhite
    }
    val contentColor = when {
        selected && dark -> BookingWhite
        selected -> BookingBlue
        dark -> BookingWhite
        else -> BookingTextPrimary
    }

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = backgroundColor,
        border = BorderStroke(
            1.dp,
            when {
                selected -> BookingBlueLight
                dark -> Color.White.copy(alpha = 0.18f)
                else -> BookingGray
            }
        )
    ) {
        Text(
            text = text,
            color = contentColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun InlineAdjusterRow(
    label: String,
    value: String,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    dark: Boolean = false,
    trailingText: String? = null,
    onTrailingTextClick: (() -> Unit)? = null
) {
    val primaryColor = if (dark) BookingWhite else BookingTextPrimary
    val secondaryColor = if (dark) BookingWhite.copy(alpha = 0.72f) else BookingTextSecondary

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = secondaryColor, modifier = Modifier.weight(1f))
        CounterButton(onClick = onDecrease, label = "-", dark = dark)
        Text(
            text = value,
            color = primaryColor,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
        CounterButton(onClick = onIncrease, label = "+", dark = dark)
        trailingText?.let {
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                modifier = Modifier.clickable(onClick = { onTrailingTextClick?.invoke() }),
                shape = RoundedCornerShape(999.dp),
                color = if (dark) Color.White.copy(alpha = 0.12f) else BookingGray,
                border = BorderStroke(1.dp, if (dark) Color.White.copy(alpha = 0.14f) else Color.Transparent)
            ) {
                Text(
                    text = it,
                    color = primaryColor,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun CounterButton(
    onClick: () -> Unit,
    label: String,
    dark: Boolean
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = if (dark) Color.White.copy(alpha = 0.12f) else BookingGray
    ) {
        Box(modifier = Modifier.size(28.dp), contentAlignment = Alignment.Center) {
            Text(text = label, color = if (dark) BookingWhite else BookingTextPrimary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SearchToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = title,
            color = BookingTextPrimary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DarkSwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, color = BookingTextPrimary, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun DarkSearchCard(content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = BookingWhite,
        border = BorderStroke(2.dp, Color(0xFFFEBB02))
    ) {
        Column(modifier = Modifier.padding(12.dp), content = { content() })
    }
}

@Composable
private fun MiniIconChip(
    icon: ImageVector,
    contentDescription: String,
    dark: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = CircleShape,
        color = if (dark) Color.White.copy(alpha = 0.12f) else BookingGray
    ) {
        Box(modifier = Modifier.size(30.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = if (dark) BookingWhite else BookingTextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PopularCompaniesSection(
    title: String,
    companies: List<String>
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            color = BookingTextPrimary,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        companies.chunked(4).forEach { rowCompanies ->
            Row(
                modifier = Modifier.padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowCompanies.forEach { company ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = BookingWhite,
                        shadowElevation = 1.dp
                    ) {
                        Text(
                            text = company,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                repeat(4 - rowCompanies.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ContinueBookingSection(card: ContinueBookingCardUiModel) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Continue with your stay",
            color = BookingTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(14.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF1B2F5C)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color.White.copy(alpha = 0.18f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Hotel,
                        contentDescription = null,
                        tint = BookingWhite,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(text = card.title, color = BookingWhite, fontWeight = FontWeight.Bold)
                    Text(
                        text = card.subtitle,
                        color = BookingWhite.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        text = card.footnote,
                        color = BookingWhite.copy(alpha = 0.72f),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentSearchCard(item: RecentSearchItem, accentColor: Color) {
    val icon = when (item.type) {
        SearchRecentType.CarRental -> Icons.Filled.DirectionsCar
        SearchRecentType.Flight -> Icons.Filled.FlightTakeoff
    }

    BookingRoundedCard(modifier = Modifier.size(width = 248.dp, height = 132.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.title, color = BookingTextPrimary, fontWeight = FontWeight.Bold)
                Text(
                    text = item.subtitle,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = item.meta,
                    color = BookingBlue,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(accentColor, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = BookingBlueLight, modifier = Modifier.size(30.dp))
            }
        }
    }
}

@Composable
private fun DestinationCard(
    item: DestinationHighlight,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(width = 176.dp, height = 208.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = BookingWhite,
        shadowElevation = 4.dp
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Brush.linearGradient(gradient))
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(52.dp)
                        .background(Color.White.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Place, contentDescription = null, tint = BookingWhite, modifier = Modifier.size(28.dp))
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(text = item.subtitle, color = BookingTextSecondary, modifier = Modifier.padding(top = 4.dp))
                Text(
                    text = item.caption,
                    color = BookingBlue,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
        }
    }
}
