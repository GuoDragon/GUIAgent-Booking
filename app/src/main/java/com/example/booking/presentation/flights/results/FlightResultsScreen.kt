package com.example.booking.presentation.flights.results

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.flights.common.FlightFilterState
import com.example.booking.presentation.flights.common.FlightSortOption
import com.example.booking.presentation.stays.common.StayFilterChip
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingMapNoticeDialog
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSheetHandle
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun FlightResultsScreen(
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onFlightClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightResultsUiState()) }
    var priceAlertEnabled by rememberSaveable { mutableStateOf(false) }
    var showSortSheet by rememberSaveable { mutableStateOf(false) }
    var showMapDialog by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : FlightResultsContract.View {
            override fun showState(state: FlightResultsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightResultsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(containerColor = BookingWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BookingWhite)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = BookingWhite,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = uiState.routeLabel,
                                color = BookingTextPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = uiState.tripLabel,
                                color = BookingTextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FlightActionChip("Sort", Icons.Filled.SwapVert, { showSortSheet = true }, modifier = Modifier.weight(1f))
                FlightActionChip(
                    text = if (uiState.hasActiveFilters) "Filter on" else "Filter",
                    icon = Icons.Filled.FilterList,
                    onClick = onFilterClick,
                    modifier = Modifier.weight(1f)
                )
                FlightActionChip(
                    text = "Map",
                    icon = Icons.Filled.Map,
                    onClick = {
                        presenter.recordMapOpened(context)
                        showMapDialog = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Genius benefit",
                        color = BookingBlueLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Get price alerts on your device",
                        color = BookingTextPrimary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(checked = priceAlertEnabled, onCheckedChange = { priceAlertEnabled = it })
            }

            Text(
                text = "${uiState.resultsCount} flights found",
                color = BookingTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            if (uiState.cards.isEmpty()) {
                BookingEmptyState(
                    icon = Icons.Filled.Map,
                    title = "No flights match this search",
                    description = "Try a different airport pair or remove some filters.",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(uiState.cards, key = { it.cardId }) { card ->
                        FlightResultCard(
                            card = card,
                            onClick = {
                                presenter.selectItinerary(context, card.outboundFlightId, card.returnFlightId)
                                onFlightClick()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSortSheet) {
        FlightSortSheet(
            onDismissRequest = { showSortSheet = false }
        )
    }

    if (showMapDialog) {
        BookingMapNoticeDialog(
            onDismissRequest = { showMapDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlightSortSheet(
    onDismissRequest: () -> Unit
) {
    var uiState by remember {
        mutableStateOf(
            FlightSortUiState(
                selectedOption = FlightSortOption.Best,
                options = FlightSortOption.entries
            )
        )
    }

    val view = remember {
        object : FlightSortContract.View {
            override fun showState(state: FlightSortUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightSortPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(modifier = Modifier.padding(start = 20.dp, top = 18.dp, end = 20.dp, bottom = 24.dp)) {
            BookingSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = "Sort by",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp, bottom = 10.dp)
            )
            uiState.options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            presenter.applySort(option)
                            onDismissRequest()
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = option.title, color = BookingTextPrimary)
                        Text(
                            text = when (option) {
                                FlightSortOption.Best -> "Looks at price, stops, and travel time."
                                FlightSortOption.Cheapest -> "Shows the lowest total price first."
                                FlightSortOption.Fastest -> "Puts the shortest trip duration first."
                            },
                            color = BookingTextSecondary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    RadioButton(
                        selected = option == uiState.selectedOption,
                        onClick = {
                            presenter.applySort(option)
                            onDismissRequest()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FlightFilterScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightFilterUiState()) }

    val view = remember {
        object : FlightFilterContract.View {
            override fun showState(state: FlightFilterUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightFilterPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    var selectedStops by remember(uiState.currentFilter.selectedStops) {
        mutableStateOf(uiState.currentFilter.selectedStops)
    }
    var selectedAirlines by remember(uiState.currentFilter.selectedAirlineIds) {
        mutableStateOf(uiState.currentFilter.selectedAirlineIds)
    }
    var directOnly by remember(uiState.currentFilter.directOnly) {
        mutableStateOf(uiState.currentFilter.directOnly)
    }

    Scaffold(
        containerColor = BookingWhite,
        bottomBar = {
            Surface(shadowElevation = 10.dp, color = BookingWhite) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    BookingPrimaryButton(
                        text = "Show results",
                        onClick = {
                            presenter.applyFilter(
                                FlightFilterState(
                                    selectedStops = selectedStops,
                                    selectedAirlineIds = selectedAirlines,
                                    directOnly = directOnly
                                )
                            )
                            onApplyClick()
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Close")
                }
                Text(
                    text = "Filter by",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Reset all",
                    color = BookingBlueLight,
                    modifier = Modifier.clickable {
                        selectedStops = emptySet()
                        selectedAirlines = emptySet()
                        directOnly = false
                    }
                )
            }

            Text(
                text = "Flight times",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.stopOptions.forEach { stops ->
                    StayFilterChip(
                        text = if (stops == 0) "Direct" else "$stops stop",
                        selected = stops in selectedStops,
                        onClick = {
                            selectedStops = if (stops in selectedStops) selectedStops - stops else selectedStops + stops
                        }
                    )
                }
            }

            Text(
                text = "Airlines",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 24.dp, bottom = 10.dp)
            )
            uiState.airlines.forEach { airline ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedAirlines = if (airline.airlineId in selectedAirlines) {
                                selectedAirlines - airline.airlineId
                            } else {
                                selectedAirlines + airline.airlineId
                            }
                        }
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = airline.name,
                        modifier = Modifier.weight(1f),
                        color = BookingTextPrimary
                    )
                    Checkbox(
                        checked = airline.airlineId in selectedAirlines,
                        onCheckedChange = { checked ->
                            selectedAirlines = if (checked) selectedAirlines + airline.airlineId else selectedAirlines - airline.airlineId
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Direct flights only",
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Hide any itineraries with a stop.",
                        color = BookingTextSecondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Switch(checked = directOnly, onCheckedChange = { directOnly = it })
            }
            Box(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun FlightDetailsScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightDetailsUiState()) }

    val view = remember {
        object : FlightDetailsContract.View {
            override fun showState(state: FlightDetailsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightDetailsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Your flight",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canContinue) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = uiState.totalLabel,
                    buttonText = "Select",
                    onClick = onContinueClick
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.canContinue) {
            BookingEmptyState(
                icon = Icons.Filled.Map,
                title = "Select a flight first",
                description = "Choose a flight result to open the details page.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 110.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = uiState.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.subtitle,
                            color = BookingTextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                items(uiState.segments) { segment ->
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = segment.header,
                            style = MaterialTheme.typography.titleMedium,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        segment.points.forEachIndexed { index, point ->
                            Column(modifier = Modifier.padding(top = if (index == 0) 14.dp else 18.dp)) {
                                Text(
                                    text = point.timeLabel,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = point.airportLabel,
                                    color = BookingTextPrimary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = point.airportMeta,
                                    color = BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = point.supportingText,
                                    color = BookingBlueLight,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
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
private fun FlightActionChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = BookingWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BookingGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BookingTextPrimary)
            Text(
                text = text,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun FlightResultCard(
    card: FlightResultCardUiModel,
    onClick: () -> Unit
) {
    BookingRoundedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            BookingStatusChip(
                text = card.badgeText,
                containerColor = Color(0xFFE3F0FF),
                contentColor = BookingBlueLight
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = card.priceText,
                    style = MaterialTheme.typography.titleLarge,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Total",
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = card.airlineLabel,
            color = BookingTextSecondary,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            text = card.supportingText,
            color = BookingBlueLight,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.outboundTimeLabel,
                    style = MaterialTheme.typography.titleLarge,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = card.outboundMetaLabel,
                    color = BookingTextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = card.returnTimeLabel,
                    style = MaterialTheme.typography.titleLarge,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                if (card.returnMetaLabel.isNotBlank()) {
                    Text(
                        text = card.returnMetaLabel,
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Row(
            modifier = Modifier.padding(top = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BookingStatusChip(
                text = card.stopsLabel,
                containerColor = Color(0xFFF1F6FF),
                contentColor = BookingBlue
            )
            BookingStatusChip(
                text = card.durationLabel,
                containerColor = Color(0xFFF7F7F7),
                contentColor = BookingTextPrimary
            )
        }
    }
}
