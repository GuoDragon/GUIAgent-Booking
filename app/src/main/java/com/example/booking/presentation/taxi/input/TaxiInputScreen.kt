package com.example.booking.presentation.taxi.input

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.booking.common.format.BookingFormatters
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite
import java.time.LocalDateTime

@Composable
fun TaxiPickupLocationScreen(
    onBackClick: () -> Unit
) {
    TaxiLocationSelectionScreen(
        onBackClick = onBackClick,
        load = { presenter, context -> presenter.loadPickupLocations(context) },
        select = { presenter, value -> presenter.selectPickupLocation(value) }
    )
}

@Composable
fun TaxiDestinationScreen(
    onBackClick: () -> Unit
) {
    TaxiLocationSelectionScreen(
        onBackClick = onBackClick,
        load = { presenter, context -> presenter.loadDestinations(context) },
        select = { presenter, value -> presenter.selectDestination(value) }
    )
}

@Composable
private fun TaxiLocationSelectionScreen(
    onBackClick: () -> Unit,
    load: (TaxiLocationPresenter, android.content.Context) -> Unit,
    select: (TaxiLocationPresenter, String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(TaxiLocationUiState()) }

    val view = remember {
        object : TaxiLocationContract.View {
            override fun showState(state: TaxiLocationUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiLocationPresenter(view) }

    LaunchedEffect(presenter, context) {
        load(presenter, context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = uiState.title.ifBlank { "Taxi" }, onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = uiState.subtitle,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            items(uiState.options) { option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            select(presenter, option)
                            onBackClick()
                        },
                    shape = RoundedCornerShape(18.dp),
                    color = BookingWhite,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = BookingBlueLight
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = option,
                                color = BookingTextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (option == uiState.selectedValue) "Currently selected" else "Tap to use this value",
                                color = BookingTextSecondary,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        if (option == uiState.selectedValue) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                tint = BookingBlue
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TaxiTimeScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(TaxiTimeUiState()) }

    val view = remember {
        object : TaxiTimeContract.View {
            override fun showState(state: TaxiTimeUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiTimePresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadTimeOptions()
    }

    var selectedPickup by remember(uiState.currentPickupLabel) {
        mutableStateOf(uiState.pickupOptions.firstOrNull() ?: LocalDateTime.now().plusDays(1))
    }
    var selectedReturn by remember(uiState.currentReturnLabel) {
        mutableStateOf(uiState.returnOptions.firstOrNull() ?: LocalDateTime.now().plusDays(2))
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Pick-up time", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            StayFooterBar(
                priceLine = uiState.tripLabel.ifBlank { "Taxi timing" },
                subLine = BookingFormatters.formatLocalDateTime(selectedPickup),
                buttonText = "Apply",
                onClick = {
                    presenter.applySelection(selectedPickup, selectedReturn)
                    onApplyClick()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            BookingRoundedCard {
                TaxiTimeSection(
                    title = "Pick-up",
                    options = uiState.pickupOptions,
                    selected = selectedPickup,
                    onSelect = { selectedPickup = it }
                )
            }
            if (uiState.roundTrip) {
                BookingRoundedCard(modifier = Modifier.padding(top = 16.dp)) {
                    TaxiTimeSection(
                        title = "Return",
                        options = uiState.returnOptions,
                        selected = selectedReturn,
                        onSelect = { selectedReturn = it }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaxiTimeSection(
    title: String,
    options: List<LocalDateTime>,
    selected: LocalDateTime,
    onSelect: (LocalDateTime) -> Unit
) {
    Text(
        text = title,
        color = BookingTextPrimary,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium
    )
    options.chunked(2).forEach { rowItems ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            rowItems.forEach { option ->
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(option) },
                    shape = RoundedCornerShape(14.dp),
                    color = if (option == selected) BookingBlueLight.copy(alpha = 0.12f) else BookingWhite,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (option == selected) BookingBlueLight else BookingGray
                    )
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)) {
                        Text(
                            text = BookingFormatters.formatLongLocalDate(option.toLocalDate()),
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            modifier = Modifier.padding(top = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Schedule,
                                contentDescription = null,
                                tint = BookingBlueLight
                            )
                            Text(
                                text = BookingFormatters.formatTime(option),
                                color = BookingTextSecondary
                            )
                        }
                    }
                }
            }
            if (rowItems.size == 1) {
                Surface(modifier = Modifier.weight(1f), color = BookingWhite) {}
            }
        }
    }
}

@Composable
fun TaxiPassengersScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(TaxiPassengerUiState()) }

    val view = remember {
        object : TaxiPassengerContract.View {
            override fun showState(state: TaxiPassengerUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiPassengerPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    var passengerCount by remember(uiState.passengerCount) { mutableStateOf(uiState.passengerCount) }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Passengers", onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            BookingRoundedCard {
                Text(
                    text = uiState.tripLabel,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = uiState.helperText,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = BookingBlueLight
                        )
                        Text(
                            text = "Passengers",
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PassengerCounterButton(label = "-", onClick = {
                            passengerCount = (passengerCount - 1).coerceAtLeast(1)
                        })
                        Text(
                            text = passengerCount.toString(),
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 18.dp)
                        )
                        PassengerCounterButton(label = "+", onClick = {
                            passengerCount = (passengerCount + 1).coerceAtMost(8)
                        })
                    }
                }
            }
            BookingPrimaryButton(
                text = "Apply",
                modifier = Modifier.padding(top = 20.dp),
                onClick = {
                    presenter.updatePassengerCount(passengerCount)
                    onApplyClick()
                }
            )
        }
    }
}

@Composable
private fun PassengerCounterButton(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = BookingGray
    ) {
        Text(
            text = label,
            color = BookingTextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}
