package com.example.booking.presentation.attractions.booking

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
import androidx.compose.material.icons.filled.LocalActivity
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
fun AttractionPersonalInfoScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionPersonalInfoUiState()) }

    val view = remember {
        object : AttractionPersonalInfoContract.View {
            override fun showState(state: AttractionPersonalInfoUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionPersonalInfoPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    var name by remember(uiState.travelerName) { mutableStateOf(uiState.travelerName) }
    var email by remember(uiState.travelerEmail) { mutableStateOf(uiState.travelerEmail) }
    var phone by remember(uiState.travelerPhone) { mutableStateOf(uiState.travelerPhone) }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Personal information", onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (!uiState.hasTicket) {
            BookingEmptyState(
                icon = Icons.Filled.LocalActivity,
                title = "Select a ticket first",
                description = "Choose a ticket option before filling in traveler details.",
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
                BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = uiState.title, color = BookingTextPrimary, fontWeight = FontWeight.Bold)
                }
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
                    label = { Text("Full name") }
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    label = { Text("Email") }
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    label = { Text("Phone number") }
                )
                BookingPrimaryButton(
                    text = "Next step",
                    modifier = Modifier.padding(top = 20.dp),
                    onClick = {
                        presenter.saveTraveler(name, email, phone)
                        onNextClick()
                    }
                )
            }
        }
    }
}

@Composable
fun AttractionPaymentScreen(
    onBackClick: () -> Unit,
    onBookingComplete: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionPaymentUiState()) }

    val view = remember {
        object : AttractionPaymentContract.View {
            override fun showState(state: AttractionPaymentUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionPaymentPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Payment", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.hasTicket) {
                StayFooterBar(
                    priceLine = uiState.totalPriceText,
                    subLine = "Total price",
                    buttonText = "Pay now",
                    onClick = { presenter.completeBooking(context)?.let(onBookingComplete) }
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.hasTicket) {
            BookingEmptyState(
                icon = Icons.Filled.LocalActivity,
                title = "Payment not ready",
                description = "Complete the ticket and traveler steps first.",
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
                        Text(text = uiState.title, color = BookingTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                        Text(text = uiState.subtitle, color = BookingBlueLight, modifier = Modifier.padding(top = 8.dp))
                        Text(text = uiState.travelerName, color = BookingTextPrimary, modifier = Modifier.padding(top = 14.dp))
                        Text(text = uiState.travelerEmail, color = BookingTextSecondary, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Pay with", color = BookingTextPrimary, fontWeight = FontWeight.Bold)
                        Text(text = "Visa ending in 4242", color = BookingTextSecondary, modifier = Modifier.padding(top = 10.dp))
                        Text(text = uiState.helperText, color = BookingTextSecondary, modifier = Modifier.padding(top = 14.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionPaymentSuccessScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onViewTripsClick: () -> Unit,
    onSearchAgainClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionPaymentSuccessUiState()) }

    val view = remember {
        object : AttractionPaymentSuccessContract.View {
            override fun showState(state: AttractionPaymentSuccessUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionPaymentSuccessPresenter(view) }

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
                icon = Icons.Filled.LocalActivity,
                title = uiState.title.ifBlank { "Booking not found" },
                description = uiState.note.ifBlank { "This attraction order is missing from the local runtime file." },
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
                    Text(text = uiState.itemName, color = BookingTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text(text = uiState.dateLabel, color = BookingTextPrimary, modifier = Modifier.padding(top = 12.dp))
                    Text(text = uiState.totalPriceText, color = BookingTextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 18.dp))
                    Text(text = "Order ID: ${uiState.orderId}", color = BookingTextSecondary, modifier = Modifier.padding(top = 12.dp))
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
