package com.example.booking.presentation.flightplushotel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun FlightPlusHotelHubScreen(
    onBackClick: () -> Unit,
    onViewFlightsClick: () -> Unit,
    onViewStaysClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightPlusHotelUiState()) }

    val view = remember {
        object : FlightPlusHotelContract.View {
            override fun showState(state: FlightPlusHotelUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightPlusHotelPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Flight + Hotel",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "Your search shows flight and hotel information together. Open either branch to continue in the reused flow.",
                style = MaterialTheme.typography.bodyLarge,
                color = BookingTextPrimary
            )
            CombinedHubCard(
                title = "Flights",
                subtitle = uiState.flightSubtitle,
                itemTitle = uiState.flightTitle,
                priceText = uiState.flightPrice,
                actionText = "View flights",
                icon = Icons.Filled.FlightTakeoff,
                onClick = onViewFlightsClick
            )
            CombinedHubCard(
                title = "Hotels",
                subtitle = uiState.staySubtitle,
                itemTitle = uiState.stayTitle,
                priceText = uiState.stayPrice,
                actionText = "View stays",
                icon = Icons.Filled.Hotel,
                onClick = onViewStaysClick
            )
        }
    }
}

@Composable
private fun CombinedHubCard(
    title: String,
    subtitle: String,
    itemTitle: String,
    priceText: String,
    actionText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = BookingBlueLight)
            Column(modifier = Modifier.weight(1f)) {
                BookingSectionHeader(title = title)
                Text(
                    text = itemTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = subtitle,
                    color = BookingTextSecondary,
                    modifier = Modifier.padding(top = 6.dp)
                )
                if (priceText.isNotBlank()) {
                    Text(
                        text = priceText,
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
        BookingPrimaryButton(
            text = actionText,
            modifier = Modifier.padding(top = 16.dp),
            onClick = onClick
        )
    }
}
