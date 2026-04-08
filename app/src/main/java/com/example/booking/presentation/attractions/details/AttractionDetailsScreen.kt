package com.example.booking.presentation.attractions.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.presentation.stays.common.StayPhotoPlaceholder
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGreen
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun AttractionPreviewScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionPreviewUiState()) }

    val view = remember {
        object : AttractionPreviewContract.View {
            override fun showState(state: AttractionPreviewUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionPreviewPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    AttractionPreviewScaffold(
        uiState = uiState,
        title = "Attraction preview",
        onBackClick = onBackClick,
        buttonText = "Continue",
        onButtonClick = onContinueClick
    )
}

@Composable
fun AttractionDetailsScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionDetailsUiState()) }

    val view = remember {
        object : AttractionDetailsContract.View {
            override fun showState(state: AttractionDetailsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionDetailsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Attraction details", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.hasAttraction) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = uiState.locationText,
                    buttonText = "See tickets",
                    onClick = onContinueClick
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.hasAttraction) {
            BookingEmptyState(
                icon = Icons.Filled.LocalActivity,
                title = "Select an attraction first",
                description = "Open a result card to continue this attraction booking flow.",
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
                            color = BookingTextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = uiState.locationText, color = BookingTextSecondary, modifier = Modifier.padding(top = 8.dp))
                        Text(text = uiState.categoryText, color = BookingBlueLight, modifier = Modifier.padding(top = 10.dp))
                        Text(text = uiState.description, color = BookingTextPrimary, modifier = Modifier.padding(top = 14.dp))
                    }
                }
                item {
                    WrappedItemRows(
                        items = uiState.highlights,
                        itemsPerRow = 2,
                        horizontalSpacing = 10.dp,
                        verticalSpacing = 10.dp
                    ) { item ->
                            BookingStatusChip(
                                text = item,
                                containerColor = Color(0xFFEFFAF3),
                                contentColor = BookingGreen
                            )
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionTicketsScreen(
    onBackClick: () -> Unit,
    onTicketClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionTicketsUiState()) }

    val view = remember {
        object : AttractionTicketsContract.View {
            override fun showState(state: AttractionTicketsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionTicketsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Available tickets", onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (!uiState.hasAttraction) {
            BookingEmptyState(
                icon = Icons.Filled.LocalActivity,
                title = "No attraction selected",
                description = "Open an attraction first to see the local ticket inventory.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 30.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(text = uiState.title, color = BookingTextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                    Text(text = uiState.subtitle, color = BookingTextSecondary, modifier = Modifier.padding(top = 6.dp))
                }
                items(uiState.tickets, key = { it.ticketId }) { ticket ->
                    BookingRoundedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                presenter.selectTicket(ticket.ticketId)
                                onTicketClick()
                            }
                    ) {
                        Text(text = ticket.title, color = BookingTextPrimary, fontWeight = FontWeight.Bold)
                        Text(text = ticket.description, color = BookingTextSecondary, modifier = Modifier.padding(top = 8.dp))
                        Text(text = ticket.validityText, color = BookingBlueLight, modifier = Modifier.padding(top = 10.dp))
                        Text(text = ticket.cancelText, color = BookingGreen, modifier = Modifier.padding(top = 6.dp))
                        Text(text = ticket.priceText, color = BookingTextPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AttractionTicketDetailsScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionTicketDetailUiState()) }

    val view = remember {
        object : AttractionTicketDetailContract.View {
            override fun showState(state: AttractionTicketDetailUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionTicketDetailPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Ticket details", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.hasTicket) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = uiState.validityText,
                    buttonText = "Continue",
                    onClick = onContinueClick
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.hasTicket) {
            BookingEmptyState(
                icon = Icons.Filled.LocalActivity,
                title = "No ticket selected",
                description = "Choose a ticket option before continuing to traveler details.",
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
                        Text(text = uiState.attractionTitle, color = BookingBlueLight, modifier = Modifier.padding(top = 8.dp))
                        Text(text = uiState.description, color = BookingTextPrimary, modifier = Modifier.padding(top = 14.dp))
                        Text(text = uiState.validityText, color = BookingTextSecondary, modifier = Modifier.padding(top = 12.dp))
                        Text(text = uiState.cancelText, color = BookingGreen, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AttractionPreviewScaffold(
    uiState: AttractionPreviewUiState,
    title: String,
    onBackClick: () -> Unit,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Scaffold(
        topBar = {
            BookingBackTopBar(title = title, onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.hasAttraction) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = uiState.cancelText,
                    buttonText = buttonText,
                    onClick = onButtonClick
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.hasAttraction) {
            BookingEmptyState(
                icon = Icons.Filled.LocalActivity,
                title = "Select an attraction first",
                description = "Open a result card to continue this attraction booking flow.",
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
                            color = BookingTextPrimary,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.padding(top = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color(0xFFFEBB02),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${uiState.ratingText} (${uiState.reviewText})",
                                color = BookingTextPrimary,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StayPhotoPlaceholder(title = uiState.title, modifier = Modifier.weight(1f).height(150.dp))
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                StayPhotoPlaceholder(title = uiState.title.take(1), modifier = Modifier.fillMaxWidth().height(71.dp), compact = true)
                                StayPhotoPlaceholder(title = "+6", modifier = Modifier.fillMaxWidth().height(71.dp), compact = true)
                            }
                        }
                        Text(text = uiState.durationText, color = BookingTextPrimary, modifier = Modifier.padding(top = 14.dp))
                        Text(text = uiState.cancelText, color = BookingGreen, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                item {
                    WrappedItemRows(
                        items = uiState.dateLabels,
                        itemsPerRow = 2,
                        horizontalSpacing = 8.dp,
                        verticalSpacing = 8.dp
                    ) { label ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BookingWhite,
                                border = androidx.compose.foundation.BorderStroke(1.dp, BookingBlueLight)
                            ) {
                                Text(
                                    text = label,
                                    color = BookingBlueLight,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
                                )
                            }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> WrappedItemRows(
    items: List<T>,
    itemsPerRow: Int,
    horizontalSpacing: Dp,
    verticalSpacing: Dp,
    itemContent: @Composable (T) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(verticalSpacing)) {
        items.chunked(itemsPerRow).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalSpacing)
            ) {
                rowItems.forEach { item ->
                    itemContent(item)
                }
            }
        }
    }
}
