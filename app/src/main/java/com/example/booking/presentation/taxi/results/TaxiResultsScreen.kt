package com.example.booking.presentation.taxi.results

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
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun TaxiResultsScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(TaxiResultsUiState()) }

    val view = remember {
        object : TaxiResultsContract.View {
            override fun showState(state: TaxiResultsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TaxiResultsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Taxi results", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canContinue) {
                StayFooterBar(
                    priceLine = uiState.selectedPriceText,
                    subLine = "Choose ${uiState.selectedRouteLabel}",
                    buttonText = "Continue",
                    onClick = onContinueClick
                )
            }
        }
    ) { innerPadding ->
        if (uiState.cards.isEmpty()) {
            BookingEmptyState(
                icon = Icons.Filled.DirectionsCar,
                title = "No taxi routes available",
                description = "Adjust the local taxi search inputs and try again.",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = uiState.title,
                        color = BookingTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.subtitle,
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                items(uiState.cards, key = { it.cardId }) { card ->
                    TaxiResultCard(
                        card = card,
                        onClick = { presenter.selectRoute(context, card.routeId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TaxiResultCard(
    card: TaxiResultCardUiModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = BookingWhite,
        border = BorderStroke(2.dp, if (card.selected) BookingBlueLight else BookingGray),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.title,
                        color = BookingTextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TaxiMetaChip(Icons.Filled.Person, card.seatText)
                        TaxiMetaChip(Icons.Filled.Luggage, card.bagText)
                    }
                }
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .padding(start = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsCar,
                        contentDescription = null,
                        tint = if (card.selected) BookingBlue else BookingBlueLight,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }
            Text(
                text = card.driverText,
                color = BookingTextPrimary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = card.cancelText,
                color = BookingTextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 6.dp)
            )
            Text(
                text = card.locationText,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(text = card.durationText, color = BookingTextSecondary)
                Text(
                    text = card.priceText,
                    color = BookingTextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TaxiMetaChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(shape = CircleShape, color = Color(0xFFF1F6FF)) {
            Box(modifier = Modifier.size(26.dp), contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = BookingBlueLight,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
        Text(
            text = text,
            color = BookingTextSecondary,
            modifier = Modifier.padding(start = 6.dp)
        )
    }
}
