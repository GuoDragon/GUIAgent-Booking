package com.example.booking.presentation.stays.results

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.stays.common.StayPhotoPlaceholder
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.components.BookingMapNoticeDialog
import com.example.booking.ui.components.BookingReferenceImage
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingGreen
import com.example.booking.ui.theme.BookingRed
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun StayResultsScreen(
    onBackClick: () -> Unit,
    onFilterClick: () -> Unit,
    onHotelClick: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(StayResultsUiState("", "", "", 0)) }
    var showSortSheet by rememberSaveable { mutableStateOf(false) }
    var showMapDialog by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : StayResultsContract.View {
            override fun showState(state: StayResultsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { StayResultsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        containerColor = BookingWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BookingWhite)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = BookingWhite,
                    shadowElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = onBackClick) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back"
                                )
                            }
                            Text(
                                text = uiState.destinationLabel,
                                modifier = Modifier.weight(1f),
                                color = BookingTextPrimary,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = uiState.dateLabel,
                                color = BookingTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = uiState.guestLabel,
                            color = BookingTextSecondary,
                            modifier = Modifier.padding(start = 52.dp)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResultsActionChip("Sort", Icons.Filled.SwapVert, { showSortSheet = true }, modifier = Modifier.weight(1f))
                ResultsActionChip(
                    text = if (uiState.hasActiveFilters) "Filter on" else "Filter",
                    icon = Icons.Filled.FilterList,
                    onClick = onFilterClick,
                    modifier = Modifier.weight(1f)
                )
                ResultsActionChip(
                    text = "Map",
                    icon = Icons.Filled.Map,
                    onClick = {
                        presenter.recordMapOpened(context)
                        showMapDialog = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                text = "${uiState.propertyCount} properties",
                color = BookingTextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(uiState.hotelCards, key = { it.cardId }) { hotel ->
                    StayHotelCard(
                        hotel = hotel,
                        onClick = { onHotelClick(hotel.hotelId) }
                    )
                }
            }
        }
    }

    if (showSortSheet) {
        StaySortSheet(
            onDismissRequest = { showSortSheet = false }
        )
    }

    if (showMapDialog) {
        BookingMapNoticeDialog(
            onDismissRequest = { showMapDialog = false }
        )
    }
}

@Composable
private fun ResultsActionChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(999.dp),
        color = BookingWhite,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, BookingGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BookingTextPrimary)
            Text(
                text = text,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}

@Composable
private fun StayHotelCard(
    hotel: StayHotelCardUiModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = BookingWhite,
        shadowElevation = 2.dp
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            BookingReferenceImage(
                assetPath = hotel.imageAssetPath,
                modifier = Modifier.size(width = 112.dp, height = 150.dp),
                compact = true,
                contentDescription = hotel.name
            )
            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = hotel.name,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    repeat(hotel.starRating) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFFEBB02),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookingStatusChip(
                        text = hotel.ratingText,
                        containerColor = Color(0xFFE3F0FF),
                        contentColor = BookingBlueLight
                    )
                    Text(
                        text = hotel.reviewCountText,
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                Text(
                    text = hotel.locationText,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = hotel.amenityText,
                    color = BookingGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = hotel.policyText,
                    color = BookingTextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = hotel.availabilityText,
                    color = BookingRed,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = hotel.highlightText,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = hotel.priceText,
                    color = BookingTextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = hotel.taxesText,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
