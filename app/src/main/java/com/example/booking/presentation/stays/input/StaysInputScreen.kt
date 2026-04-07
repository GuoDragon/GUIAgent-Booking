package com.example.booking.presentation.stays.input

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.booking.common.format.BookingFormatters
import com.example.booking.presentation.stays.common.StayFilterChip
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.presentation.stays.common.StaySummaryInfoCard
import com.example.booking.presentation.stays.common.StayCounterRow
import com.example.booking.presentation.stays.common.StayDividerSpacer
import com.example.booking.presentation.stays.common.StaySwitchRow
import com.example.booking.ui.components.BookingFloatingBackButton
import com.example.booking.ui.components.BookingSheetHandle
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

@Composable
fun StayDestinationScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var uiState by remember { mutableStateOf(StayDestinationUiState()) }
    var query by rememberSaveable { mutableStateOf("") }

    val view = remember {
        object : StayDestinationContract.View {
            override fun showState(state: StayDestinationUiState) {
                uiState = state
                if (query != state.query) {
                    query = state.query
                }
            }
        }
    }
    val presenter = remember(view) { StayDestinationPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context, query)
    }

    LaunchedEffect(query) {
        presenter.loadData(context, query)
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    Scaffold(
        containerColor = BookingWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = BookingWhite,
                    shadowElevation = 3.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(BookingWhite)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            placeholder = { Text("Enter destination") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(
                                onSearch = {
                                    presenter.applyDestination(query)
                                    keyboardController?.hide()
                                    onBackClick()
                                }
                            )
                        )
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp)
                        .clickable { },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = BookingGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = null,
                                tint = BookingBlueLight
                            )
                        }
                    }
                    Text(
                        text = "Around current location",
                        color = BookingBlueLight,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
            if (query.isNotBlank()) {
                item {
                    DestinationSuggestionRow(
                        icon = Icons.Filled.Search,
                        title = query,
                        subtitle = "Use this destination",
                        onClick = {
                            presenter.applyDestination(query)
                            keyboardController?.hide()
                            onBackClick()
                        }
                    )
                }
            }
            item {
                Text(
                    text = "Continue your search",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = BookingTextPrimary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
                )
            }
            items(uiState.recentSuggestions) { suggestion ->
                DestinationSuggestionRow(
                    icon = when (suggestion.type) {
                        StayDestinationSuggestionType.Recent -> Icons.Filled.History
                        StayDestinationSuggestionType.City -> Icons.Filled.Search
                        StayDestinationSuggestionType.Property -> Icons.Filled.Hotel
                    },
                    title = suggestion.title,
                    subtitle = suggestion.subtitle,
                    onClick = {
                        presenter.applyDestination(suggestion.title)
                        keyboardController?.hide()
                        onBackClick()
                    }
                )
            }
            item {
                Text(
                    text = "Properties and destinations",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BookingTextPrimary,
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
                )
            }
            items(uiState.propertySuggestions) { suggestion ->
                DestinationSuggestionRow(
                    icon = if (suggestion.type == StayDestinationSuggestionType.Property) {
                        Icons.Filled.Hotel
                    } else {
                        Icons.Filled.Search
                    },
                    title = suggestion.title,
                    subtitle = suggestion.subtitle,
                    onClick = {
                        presenter.applyDestination(suggestion.title)
                        keyboardController?.hide()
                        onBackClick()
                    }
                )
            }
        }
    }
}

@Composable
fun StayDateScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    var uiState by remember {
        mutableStateOf(
            StayDateUiState(
                checkInDate = LocalDate.now(),
                checkOutDate = LocalDate.now().plusDays(1),
                calendarMonths = emptyList()
            )
        )
    }
    var selectedCheckIn by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var selectedCheckOut by rememberSaveable { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
    var selectingCheckOut by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : StayDateContract.View {
            override fun showState(state: StayDateUiState) {
                uiState = state
                selectedCheckIn = state.checkInDate.toString()
                selectedCheckOut = state.checkOutDate.toString()
                selectingCheckOut = false
            }
        }
    }
    val presenter = remember(view) { StayDatePresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    val checkInDate = remember(selectedCheckIn) { LocalDate.parse(selectedCheckIn) }
    val checkOutDate = remember(selectedCheckOut) { LocalDate.parse(selectedCheckOut) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BookingWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            StaySummaryInfoCard(
                title = "Current stay draft",
                description = BookingFormatters.formatSearchDateSummary(checkInDate, checkOutDate)
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = BookingWhite
        ) {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Box(
                    modifier = Modifier
                        .size(width = 54.dp, height = 4.dp)
                        .background(BookingGray, CircleShape)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = "Select dates",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StayFilterChip(
                        text = "Calendar",
                        selected = true,
                        onClick = {}
                    )
                    StayFilterChip(
                        text = "I'm flexible",
                        selected = false,
                        onClick = {}
                    )
                }
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    items(uiState.calendarMonths) { month ->
                        CalendarMonth(
                            monthStart = month,
                            selectedCheckIn = checkInDate,
                            selectedCheckOut = checkOutDate,
                            onDateSelected = { date ->
                                if (!selectingCheckOut) {
                                    selectedCheckIn = date.toString()
                                    selectedCheckOut = date.toString()
                                    selectingCheckOut = true
                                } else if (date <= checkInDate) {
                                    selectedCheckIn = date.toString()
                                    selectedCheckOut = date.toString()
                                    selectingCheckOut = true
                                } else {
                                    selectedCheckOut = date.toString()
                                    selectingCheckOut = false
                                }
                            }
                        )
                    }
                }
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listOf("Exact dates", "+/- 1 day", "+/- 2 days", "+/- 3 days")) { label ->
                        StayFilterChip(
                            text = label,
                            selected = label == "Exact dates",
                            onClick = {}
                        )
                    }
                }
                StayFooterBar(
                    priceLine = BookingFormatters.formatSearchDateSummary(checkInDate, checkOutDate),
                    subLine = BookingFormatters.formatNightCount(checkInDate, checkOutDate),
                    buttonText = "Select dates",
                    modifier = Modifier.padding(top = 10.dp)
                ) {
                    presenter.applyDates(checkInDate, checkOutDate)
                    onApplyClick()
                }
            }
        }

        BookingFloatingBackButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StayGuestsSheet(
    onDismissRequest: () -> Unit,
    onApplyClick: () -> Unit
) {
    var roomCount by rememberSaveable { mutableStateOf(1) }
    var adultCount by rememberSaveable { mutableStateOf(2) }
    var childCount by rememberSaveable { mutableStateOf(0) }
    var travelingWithPets by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : StayGuestsContract.View {
            override fun showState(state: StayGuestsUiState) {
                roomCount = state.roomCount
                adultCount = state.adultCount
                childCount = state.childCount
                travelingWithPets = state.travelingWithPets
            }
        }
    }
    val presenter = remember(view) { StayGuestsPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 24.dp)
        ) {
            BookingSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = "Select rooms and guests",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp)
            )
            StayGuestsEditorContent(
                roomCount = roomCount,
                adultCount = adultCount,
                childCount = childCount,
                travelingWithPets = travelingWithPets,
                onRoomCountChange = { roomCount = it },
                onAdultCountChange = { adultCount = it },
                onChildCountChange = { childCount = it },
                onTravelingWithPetsChange = { travelingWithPets = it },
                onApplyClick = {
                    presenter.applySelection(roomCount, adultCount, childCount, travelingWithPets)
                    onApplyClick()
                }
            )
        }
    }
}

@Composable
private fun StayGuestsEditorContent(
    roomCount: Int,
    adultCount: Int,
    childCount: Int,
    travelingWithPets: Boolean,
    onRoomCountChange: (Int) -> Unit,
    onAdultCountChange: (Int) -> Unit,
    onChildCountChange: (Int) -> Unit,
    onTravelingWithPetsChange: (Boolean) -> Unit,
    onApplyClick: () -> Unit
) {
    StayCounterRow(
        title = "Rooms",
        value = roomCount,
        minValue = 1,
        onDecrement = { onRoomCountChange((roomCount - 1).coerceAtLeast(1)) },
        onIncrement = { onRoomCountChange(roomCount + 1) }
    )
    StayCounterRow(
        title = "Adults",
        value = adultCount,
        minValue = 1,
        onDecrement = { onAdultCountChange((adultCount - 1).coerceAtLeast(1)) },
        onIncrement = { onAdultCountChange(adultCount + 1) }
    )
    StayCounterRow(
        title = "Children",
        subtitle = "0 - 17 years old",
        value = childCount,
        minValue = 0,
        onDecrement = { onChildCountChange((childCount - 1).coerceAtLeast(0)) },
        onIncrement = { onChildCountChange(childCount + 1) }
    )
    StaySwitchRow(
        title = "Traveling with pets?",
        subtitle = "Assistance animals are not considered pets.",
        checked = travelingWithPets,
        onCheckedChange = onTravelingWithPetsChange
    )
    StayFooterBar(
        priceLine = BookingFormatters.formatGuestSummary(roomCount, adultCount, childCount),
        subLine = if (travelingWithPets) "Traveling with pets" else "No pets added",
        buttonText = "Apply",
        modifier = Modifier.padding(top = 8.dp),
        onClick = onApplyClick
    )
}

@Composable
private fun DestinationSuggestionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(36.dp),
                shape = RoundedCornerShape(12.dp),
                color = BookingGray
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = BookingBlueLight)
                }
            }
            Column(modifier = Modifier.padding(start = 12.dp)) {
                Text(text = title, color = BookingTextPrimary, fontWeight = FontWeight.Medium)
                Text(
                    text = subtitle,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        StayDividerSpacer()
    }
}

@Composable
private fun CalendarMonth(
    monthStart: LocalDate,
    selectedCheckIn: LocalDate,
    selectedCheckOut: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val yearMonth = YearMonth.from(monthStart)
    val firstGridDate = monthStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val dates = (0 until 35).map { firstGridDate.plusDays(it.toLong()) }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = BookingFormatters.formatMonthYear(monthStart),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                Text(
                    text = day,
                    color = BookingTextSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
        dates.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { date ->
                    val inMonth = date.month == yearMonth.month
                    val selected = date == selectedCheckIn || date == selectedCheckOut
                    val inRange = date > selectedCheckIn && date < selectedCheckOut
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .height(42.dp)
                            .clickable(enabled = inMonth) { onDateSelected(date) },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            selected -> BookingBlueLight
                            inRange -> BookingBlueLight.copy(alpha = 0.12f)
                            else -> BookingWhite
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = when {
                                    !inMonth -> BookingTextSecondary.copy(alpha = 0.3f)
                                    selected -> BookingWhite
                                    else -> BookingTextPrimary
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

