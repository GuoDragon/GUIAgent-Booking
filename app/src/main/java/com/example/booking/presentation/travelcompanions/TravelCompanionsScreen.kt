package com.example.booking.presentation.travelcompanions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
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
import com.example.booking.ui.components.BookingCardDivider
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSettingRow
import com.example.booking.ui.theme.BookingWhite

@Composable
fun TravelCompanionsScreen(
    onBackClick: () -> Unit,
    onAddTravelCompanionClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(TravelCompanionsUiState()) }

    val view = remember {
        object : TravelCompanionsContract.View {
            override fun showState(state: TravelCompanionsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { TravelCompanionsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Travel companions",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                BookingPrimaryButton(
                    text = "Add new traveler",
                    onClick = onAddTravelCompanionClick
                )
            }
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (uiState.companions.isEmpty()) {
            BookingEmptyState(
                icon = Icons.Filled.Group,
                title = "Add companion details for easier booking",
                description = "Store your traveler details locally so future trips are faster to set up.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = innerPadding.calculateBottomPadding()
                    )
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 18.dp,
                    bottom = innerPadding.calculateBottomPadding() + 18.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "Add or review traveler details before booking.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                items(uiState.companions, key = { it.companionId }) { companion ->
                    BookingRoundedCard {
                        Text(
                            text = companion.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        BookingSettingRow(
                            title = "Name",
                            subtitle = companion.fullName,
                            showChevron = false
                        )
                        BookingCardDivider()
                        BookingSettingRow(
                            title = "Date of birth",
                            subtitle = companion.dateOfBirth,
                            showChevron = false
                        )
                        BookingCardDivider()
                        BookingSettingRow(
                            title = "Gender",
                            subtitle = companion.gender,
                            showChevron = false
                        )
                    }
                }
            }
        }
    }
}
