package com.example.booking.presentation.carrentals.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Checkbox
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.carrentals.common.CarRentalDraftStore
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun CarRentalBookingSummaryScreen(
    onBackClick: () -> Unit,
    onBookingComplete: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(CarRentalSummaryUiState()) }
    var childSeatRequired by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : CarRentalSummaryContract.View {
            override fun showState(state: CarRentalSummaryUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { CarRentalSummaryPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    LaunchedEffect(uiState.childSeatRequired) {
        childSeatRequired = uiState.childSeatRequired
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Booking summary", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canContinue) {
                com.example.booking.presentation.stays.common.StayFooterBar(
                    priceLine = uiState.totalPriceText,
                    subLine = uiState.totalLabel,
                    buttonText = "Confirm booking",
                    onClick = {
                        presenter.completeBooking(context, childSeatRequired)?.let(onBookingComplete)
                    }
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.canContinue) {
            BookingEmptyState(
                icon = Icons.Filled.DirectionsCar,
                title = "Select a car first",
                description = "Open a car result and continue to see the booking summary.",
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
                            color = BookingTextPrimary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = uiState.pickupLine,
                            color = BookingTextPrimary,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                        Text(
                            text = uiState.dropOffLine,
                            color = BookingTextPrimary,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                        Text(
                            text = uiState.companyName,
                            color = BookingBlueLight,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = Color(0xFFEFFAF3),
                        border = BorderStroke(1.dp, Color(0xFF91D0A6))
                    ) {
                        Text(
                            text = uiState.includedLine,
                            color = Color(0xFF1C7C35),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                        )
                    }
                }
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Child safety seat",
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Add a certified child seat for airport pick-up",
                                    color = BookingTextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Checkbox(
                                checked = childSeatRequired,
                                onCheckedChange = { checked ->
                                    childSeatRequired = checked
                                    CarRentalDraftStore.update { draft ->
                                        draft.copy(childSeatRequired = checked)
                                    }
                                    presenter.loadData(context)
                                }
                            )
                        }
                    }
                }
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Payable today",
                            style = MaterialTheme.typography.titleLarge,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        uiState.priceLineItems.forEach { (label, value) ->
                            androidx.compose.foundation.layout.Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = label, color = BookingTextPrimary)
                                Text(text = value, color = BookingTextPrimary)
                            }
                        }
                        Text(
                            text = "Tap Confirm booking to create this order in Trips.",
                            color = BookingTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CarRentalBookingSuccessScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onViewTripsClick: () -> Unit,
    onSearchAgainClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(CarRentalBookingSuccessUiState()) }

    val view = remember {
        object : CarRentalBookingSuccessContract.View {
            override fun showState(state: CarRentalBookingSuccessUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { CarRentalBookingSuccessPresenter(view) }

    LaunchedEffect(presenter, context, orderId) {
        presenter.loadData(context, orderId)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Booking complete", onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (!uiState.hasOrder) {
            BookingEmptyState(
                icon = Icons.Filled.DirectionsCar,
                title = uiState.title.ifBlank { "Booking not found" },
                description = uiState.note.ifBlank { "This order is missing from the local runtime file." },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(88.dp),
                    shape = CircleShape,
                    color = Color(0xFFDDF4DE)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF1C7C35),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = uiState.note,
                    color = BookingTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
                BookingRoundedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = uiState.itemName,
                        style = MaterialTheme.typography.titleLarge,
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.dateLabel,
                        color = BookingTextPrimary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = uiState.guestLabel,
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = uiState.totalPriceText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        text = "Order ID: ${uiState.orderId}",
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                BookingPrimaryButton(
                    text = "View trips",
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = onViewTripsClick
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clickable(onClick = onSearchAgainClick),
                    shape = RoundedCornerShape(10.dp),
                    color = BookingWhite,
                    border = BorderStroke(1.dp, BookingBlueLight)
                ) {
                    Text(
                        text = "Search again",
                        color = BookingBlue,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}
