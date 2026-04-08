package com.example.booking.presentation.taxi.booking

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun TaxiAddFlightTrackingScreen(
    onBackClick: () -> Unit,
    onChooseFlightClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(TaxiAddFlightTrackingUiState()) }

    val view = remember {
        object : TaxiAddFlightTrackingContract.View {
            override fun showState(state: TaxiAddFlightTrackingUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiAddFlightTrackingPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    var departureAirport by remember(uiState.departureAirportQuery) {
        mutableStateOf(uiState.departureAirportQuery)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Add flight tracking", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            BookingPrimaryButton(
                text = "Continue",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                onClick = {
                    presenter.saveDepartureAirportQuery(departureAirport)
                    onContinueClick()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .background(BookingBlueLight.copy(alpha = 0.14f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.FlightTakeoff,
                    contentDescription = null,
                    tint = BookingBlueLight,
                    modifier = Modifier.size(44.dp)
                )
            }
            Text(
                text = "Tailor your pick-up time at ${uiState.pickupAirportLabel}",
                color = BookingTextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 18.dp)
            )
            Text(
                text = "• Find your flight and add complimentary flight tracking for a smoother pick-up",
                color = BookingTextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 14.dp)
            )
            Text(
                text = "• The driver can track your arrival and adjust the pick-up time based on when you land",
                color = BookingTextPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "Tell us where you're flying from",
                color = BookingTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 22.dp, bottom = 10.dp)
            )
            OutlinedTextField(
                value = departureAirport,
                onValueChange = { departureAirport = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.FlightTakeoff,
                        contentDescription = null,
                        tint = BookingTextSecondary
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Choose flight",
                        tint = BookingBlueLight,
                        modifier = Modifier.clickable {
                            presenter.saveDepartureAirportQuery(departureAirport)
                            onChooseFlightClick()
                        }
                    )
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        presenter.saveDepartureAirportQuery(departureAirport)
                        onChooseFlightClick()
                    }
                ),
                placeholder = { Text("Enter departure airport (e.g. Heathrow)") }
            )
            if (uiState.selectedFlightTitle.isNotBlank()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = BookingWhite,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BookingBlueLight.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                        Text(
                            text = uiState.selectedFlightTitle,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = uiState.selectedFlightSubtitle,
                            color = BookingTextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = BookingTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "If there's more than one flight to your destination, enter the departure airport for the final leg of your trip.",
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
