package com.example.booking.presentation.taxi.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.stays.common.StayFooterBar
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
fun TaxiContactDetailsScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(TaxiContactUiState()) }

    val view = remember {
        object : TaxiContactContract.View {
            override fun showState(state: TaxiContactUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiContactPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    var name by remember(uiState.contactName) { mutableStateOf(uiState.contactName) }
    var email by remember(uiState.contactEmail) { mutableStateOf(uiState.contactEmail) }
    var phone by remember(uiState.contactPhone) { mutableStateOf(uiState.contactPhone) }
    var flightNumber by remember(uiState.flightNumber) { mutableStateOf(uiState.flightNumber) }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Contact details", onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (!uiState.hasSelection) {
            BookingEmptyState(
                icon = Icons.Filled.DirectionsCar,
                title = "Select a taxi first",
                description = "Pick a taxi result before filling in the contact details.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                BookingRoundedCard {
                    Text(text = uiState.pickupLine, color = BookingTextPrimary, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = uiState.destinationLine,
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    label = { Text("Full name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text("Phone number") }
                )
                OutlinedTextField(
                    value = flightNumber,
                    onValueChange = { flightNumber = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    label = { Text("Flight number") }
                )
                BookingPrimaryButton(
                    text = "Next step",
                    modifier = Modifier.padding(top = 20.dp),
                    onClick = {
                        presenter.saveContact(name, email, phone, flightNumber)
                        onNextClick()
                    }
                )
            }
        }
    }
}

@Composable
fun TaxiOverviewScreen(
    onBackClick: () -> Unit,
    onBookingComplete: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(TaxiOverviewUiState()) }

    val view = remember {
        object : TaxiOverviewContract.View {
            override fun showState(state: TaxiOverviewUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiOverviewPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Summary and payment", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.hasSelection) {
                StayFooterBar(
                    priceLine = uiState.totalPriceText,
                    subLine = "Total price",
                    buttonText = "Book Taxi",
                    onClick = { presenter.completeBooking(context)?.let(onBookingComplete) }
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.hasSelection) {
            BookingEmptyState(
                icon = Icons.Filled.DirectionsCar,
                title = "Taxi booking not ready",
                description = "Choose a route and complete the contact details first.",
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
                    BookingRoundedCard {
                        Text(
                            text = "Trip summary",
                            color = BookingTextPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = uiState.pickupLine, color = BookingTextPrimary, modifier = Modifier.padding(top = 14.dp))
                        Text(text = uiState.destinationLine, color = BookingTextPrimary, modifier = Modifier.padding(top = 14.dp))
                        Text(
                            text = uiState.routeTitle,
                            color = BookingBlueLight,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            text = "Flight number: ${uiState.flightNumber}",
                            color = BookingTextPrimary,
                            modifier = Modifier.padding(top = 14.dp)
                        )
                        Text(
                            text = uiState.passengerLine,
                            color = BookingTextSecondary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                item {
                    BookingRoundedCard {
                        Text(
                            text = "Payment",
                            color = BookingTextPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.helperText,
                            color = BookingTextSecondary,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaxiBookingSuccessScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onViewTripsClick: () -> Unit,
    onSearchAgainClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(TaxiBookingSuccessUiState()) }

    val view = remember {
        object : TaxiBookingSuccessContract.View {
            override fun showState(state: TaxiBookingSuccessUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiBookingSuccessPresenter(view) }

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
                description = uiState.note.ifBlank { "This taxi order is missing from the local runtime file." },
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
                    Text(text = uiState.dateLabel, color = BookingTextPrimary, modifier = Modifier.padding(top = 12.dp))
                    Text(text = uiState.guestLabel, color = BookingTextSecondary, modifier = Modifier.padding(top = 8.dp))
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
