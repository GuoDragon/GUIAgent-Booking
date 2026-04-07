package com.example.booking.presentation.attractions.input

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
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
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun AttractionDestinationScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionDestinationUiState()) }

    val view = remember {
        object : AttractionDestinationContract.View {
            override fun showState(state: AttractionDestinationUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionDestinationPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Destination", onBackClick = onBackClick)
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
            items(uiState.options) { option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            presenter.selectDestination(option)
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
                            Text(text = option, color = BookingTextPrimary, fontWeight = FontWeight.SemiBold)
                            Text(
                                text = if (option == uiState.selectedValue) "Currently selected" else "Tap to browse this city",
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
fun AttractionDateScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(AttractionDateUiState()) }

    val view = remember {
        object : AttractionDateContract.View {
            override fun showState(state: AttractionDateUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionDatePresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    var selectedDate by remember(uiState.selectedDate) { mutableStateOf(uiState.selectedDate) }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Choose date", onBackClick = onBackClick)
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
                    text = "Next available dates",
                    color = BookingTextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                uiState.options.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowItems.forEach { date ->
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedDate = date },
                                shape = RoundedCornerShape(14.dp),
                                color = if (date == selectedDate) BookingBlueLight.copy(alpha = 0.12f) else BookingWhite,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (date == selectedDate) BookingBlueLight else BookingGray
                                )
                            ) {
                                Text(
                                    text = BookingFormatters.formatLongLocalDate(date),
                                    color = BookingTextPrimary,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp)
                                )
                            }
                        }
                    }
                }
            }
            BookingPrimaryButton(
                text = "Apply",
                modifier = Modifier.padding(top = 20.dp),
                onClick = {
                    presenter.selectDate(selectedDate)
                    onApplyClick()
                }
            )
        }
    }
}
