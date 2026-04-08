package com.example.booking.presentation.taxi.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSheetHandle
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun TaxiChooseFlightScreen(
    onBackClick: () -> Unit,
    onFlightSelected: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(TaxiChooseFlightUiState()) }
    var showAirlinesSheet by remember { mutableStateOf(false) }
    var showFlightTimesSheet by remember { mutableStateOf(false) }

    val view = remember {
        object : TaxiChooseFlightContract.View {
            override fun showState(state: TaxiChooseFlightUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiChooseFlightPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = uiState.title, onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    text = "All",
                    selected = uiState.selectedAirlineIds.isEmpty() &&
                        uiState.selectedDepartureSlots.isEmpty() &&
                        uiState.selectedArrivalSlots.isEmpty(),
                    onClick = { presenter.resetFilters() }
                )
                FilterChip(
                    text = "Airlines",
                    selected = uiState.selectedAirlineIds.isNotEmpty(),
                    onClick = { showAirlinesSheet = true },
                    trailingIcon = Icons.Filled.KeyboardArrowDown
                )
                FilterChip(
                    text = "Flight times",
                    selected = uiState.selectedDepartureSlots.isNotEmpty() || uiState.selectedArrivalSlots.isNotEmpty(),
                    onClick = { showFlightTimesSheet = true },
                    trailingIcon = Icons.Filled.KeyboardArrowDown
                )
            }

            if (uiState.cards.isEmpty()) {
                BookingEmptyState(
                    icon = Icons.Filled.FlightTakeoff,
                    title = "No flights available",
                    description = "Adjust filters or departure keywords and try again.",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.cards, key = { it.flightId }) { card ->
                        TaxiChooseFlightCard(
                            card = card,
                            onClick = {
                                presenter.selectFlight(card.flightId)
                                onFlightSelected()
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAirlinesSheet) {
        AirlinesFilterSheet(
            options = uiState.airlineOptions,
            selectedAirlineIds = uiState.selectedAirlineIds,
            onDismissRequest = { showAirlinesSheet = false },
            onReset = {
                presenter.applyFilters(
                    selectedAirlineIds = emptySet(),
                    selectedDepartureSlots = uiState.selectedDepartureSlots,
                    selectedArrivalSlots = uiState.selectedArrivalSlots
                )
                showAirlinesSheet = false
            },
            onApply = { selectedAirlines ->
                presenter.applyFilters(
                    selectedAirlineIds = selectedAirlines,
                    selectedDepartureSlots = uiState.selectedDepartureSlots,
                    selectedArrivalSlots = uiState.selectedArrivalSlots
                )
                showAirlinesSheet = false
            }
        )
    }

    if (showFlightTimesSheet) {
        FlightTimesFilterSheet(
            selectedDepartureSlots = uiState.selectedDepartureSlots,
            selectedArrivalSlots = uiState.selectedArrivalSlots,
            onDismissRequest = { showFlightTimesSheet = false },
            onReset = {
                presenter.applyFilters(
                    selectedAirlineIds = uiState.selectedAirlineIds,
                    selectedDepartureSlots = emptySet(),
                    selectedArrivalSlots = emptySet()
                )
                showFlightTimesSheet = false
            },
            onApply = { departures, arrivals ->
                presenter.applyFilters(
                    selectedAirlineIds = uiState.selectedAirlineIds,
                    selectedDepartureSlots = departures,
                    selectedArrivalSlots = arrivals
                )
                showFlightTimesSheet = false
            }
        )
    }
}

@Composable
private fun TaxiChooseFlightCard(
    card: TaxiFlightCardUiModel,
    onClick: () -> Unit
) {
    BookingRoundedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Text(
            text = card.title,
            color = BookingTextPrimary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = card.departureLabel, color = BookingTextSecondary)
                Text(
                    text = card.departureTimeLabel,
                    color = BookingTextPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = card.departureDateLabel,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = BookingTextSecondary
            )
            Column(horizontalAlignment = Alignment.End) {
                Text(text = card.arrivalLabel, color = BookingTextSecondary)
                Text(
                    text = card.arrivalTimeLabel,
                    color = BookingTextPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = card.arrivalDateLabel,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (selected) BookingBlueLight.copy(alpha = 0.12f) else BookingWhite,
        border = BorderStroke(1.dp, if (selected) BookingBlueLight else BookingGray),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (selected) BookingBlue else BookingTextPrimary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
            trailingIcon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = if (selected) BookingBlue else BookingTextSecondary,
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AirlinesFilterSheet(
    options: List<TaxiAirlineOptionUiModel>,
    selectedAirlineIds: Set<String>,
    onDismissRequest: () -> Unit,
    onReset: () -> Unit,
    onApply: (Set<String>) -> Unit
) {
    var localSelection by remember(selectedAirlineIds) { mutableStateOf(selectedAirlineIds) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 24.dp)
        ) {
            BookingSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = "Airlines",
                style = MaterialTheme.typography.headlineSmall,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )
            Column(modifier = Modifier.padding(top = 14.dp)) {
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                localSelection = if (localSelection.contains(option.airlineId)) {
                                    localSelection - option.airlineId
                                } else {
                                    localSelection + option.airlineId
                                }
                            }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = option.name, color = BookingTextPrimary)
                        Checkbox(
                            checked = localSelection.contains(option.airlineId),
                            onCheckedChange = { checked ->
                                localSelection = if (checked) {
                                    localSelection + option.airlineId
                                } else {
                                    localSelection - option.airlineId
                                }
                            }
                        )
                    }
                }
            }
            SheetFooterButtons(
                onReset = onReset,
                onApply = { onApply(localSelection) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FlightTimesFilterSheet(
    selectedDepartureSlots: Set<FlightTimeSlot>,
    selectedArrivalSlots: Set<FlightTimeSlot>,
    onDismissRequest: () -> Unit,
    onReset: () -> Unit,
    onApply: (Set<FlightTimeSlot>, Set<FlightTimeSlot>) -> Unit
) {
    var localDepartureSlots by remember(selectedDepartureSlots) { mutableStateOf(selectedDepartureSlots) }
    var localArrivalSlots by remember(selectedArrivalSlots) { mutableStateOf(selectedArrivalSlots) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 24.dp)
        ) {
            BookingSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = "Flight times",
                style = MaterialTheme.typography.headlineSmall,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )
            Column(
                modifier = Modifier
                    .padding(top = 14.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Departs",
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                FlightTimeSlot.entries.forEach { slot ->
                    SlotRow(
                        label = slot.label,
                        checked = localDepartureSlots.contains(slot),
                        onCheckedChange = { checked ->
                            localDepartureSlots = if (checked) {
                                localDepartureSlots + slot
                            } else {
                                localDepartureSlots - slot
                            }
                        }
                    )
                }
                Text(
                    text = "Arrives",
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                FlightTimeSlot.entries.forEach { slot ->
                    SlotRow(
                        label = slot.label,
                        checked = localArrivalSlots.contains(slot),
                        onCheckedChange = { checked ->
                            localArrivalSlots = if (checked) {
                                localArrivalSlots + slot
                            } else {
                                localArrivalSlots - slot
                            }
                        }
                    )
                }
            }
            SheetFooterButtons(
                onReset = onReset,
                onApply = { onApply(localDepartureSlots, localArrivalSlots) }
            )
        }
    }
}

@Composable
private fun SlotRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = BookingTextPrimary)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun SheetFooterButtons(
    onReset: () -> Unit,
    onApply: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = BookingWhite,
            border = BorderStroke(1.dp, BookingGray),
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onReset)
        ) {
            Text(
                text = "Reset",
                color = BookingTextSecondary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 14.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = BookingBlueLight,
            modifier = Modifier
                .weight(1f)
                .clickable(onClick = onApply)
        ) {
            Text(
                text = "Apply",
                color = BookingWhite,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 14.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
