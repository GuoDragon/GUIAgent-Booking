package com.example.booking.presentation.stays.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
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
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.presentation.stays.common.StaySummaryInfoCard
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingReferenceImage
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingGreen
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun StayDetailsScreen(
    onBackClick: () -> Unit,
    onSelectRoomsClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(StayDetailsUiState()) }

    val view = remember {
        object : StayDetailsContract.View {
            override fun showState(state: StayDetailsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { StayDetailsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Property details",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.hotelId != null) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = "${uiState.nightsLabel} | ${uiState.roomPreviewText}",
                    buttonText = "Select rooms",
                    enabled = uiState.priceText.isNotBlank() && !uiState.priceText.contains("unavailable", ignoreCase = true),
                    onClick = onSelectRoomsClick
                )
            }
        }
    ) { innerPadding ->
        if (uiState.hotelId == null) {
            BookingEmptyState(
                icon = Icons.Filled.Hotel,
                title = "No property selected",
                description = "Choose a hotel from the stay results page to continue this booking flow.",
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
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                item {
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = uiState.hotelName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Row(
                                    modifier = Modifier.padding(top = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    repeat(uiState.starRating) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFEBB02),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = uiState.address,
                                    color = BookingTextPrimary,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                                Text(
                                    text = uiState.locationText,
                                    color = BookingGreen,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = uiState.reviewText,
                                    color = BookingTextSecondary,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                            if (uiState.reviewScoreText.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BookingBlueLight
                                ) {
                                    Text(
                                        text = uiState.reviewScoreText,
                                        color = BookingWhite,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    StayPhotoGallery(assetPaths = uiState.photoAssetPaths)
                }
                item {
                    BookingRoundedCard {
                        BookingSectionHeader(title = "About this property")
                        Text(
                            text = uiState.description,
                            color = BookingTextPrimary,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
                item {
                    Column {
                        BookingSectionHeader(title = "Property highlights")
                        PropertyHighlightsRow(
                            amenities = uiState.highlightAmenities,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StaySummaryInfoCard(
                            title = "Check-in",
                            description = uiState.checkInLabel,
                            modifier = Modifier.weight(1f)
                        )
                        StaySummaryInfoCard(
                            title = "Check-out",
                            description = uiState.checkOutLabel,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                item {
                    StaySummaryInfoCard(
                        title = "You searched for",
                        description = "${uiState.guestSummary}\n${uiState.nightsLabel}\n${uiState.roomPreviewText}"
                    )
                }
                item {
                    BookingSectionHeader(
                        title = "Guest reviews",
                        subtitle = "Local demo comments for this property"
                    )
                }
                items(uiState.guestReviews) { review ->
                    BookingRoundedCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = review.reviewer,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = review.meta,
                                    color = BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BookingBlueLight
                            ) {
                                Text(
                                    text = review.scoreText,
                                    color = BookingWhite,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                        Text(
                            text = review.title,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = review.detail,
                            color = BookingTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StayRoomTypeScreen(
    onBackClick: () -> Unit,
    onRoomSelected: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(StayRoomTypeUiState()) }

    val view = remember {
        object : StayRoomTypeContract.View {
            override fun showState(state: StayRoomTypeUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { StayRoomTypePresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Select a room",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (uiState.roomCards.isEmpty()) {
            BookingEmptyState(
                icon = Icons.Filled.Hotel,
                title = "No rooms available",
                description = "The selected property does not have matching room data for this demo flow.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StaySummaryInfoCard(
                        title = uiState.hotelName,
                        description = "${uiState.dateLabel}\n${uiState.guestSummary}\n${uiState.roomCountLabel}"
                    )
                }
                items(uiState.roomCards, key = { it.roomId }) { room ->
                    BookingRoundedCard {
                        Row(verticalAlignment = Alignment.Top) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = room.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = BookingBlueLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = room.capacityText,
                                    color = BookingTextPrimary,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                Text(
                                    text = room.bedText,
                                    color = BookingTextPrimary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            BookingReferenceImage(
                                assetPath = room.imageAssetPath,
                                modifier = Modifier.size(width = 88.dp, height = 72.dp),
                                compact = true,
                                contentDescription = room.title
                            )
                        }
                        Text(
                            text = room.description,
                            color = BookingTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = room.amenityText,
                            color = BookingTextPrimary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp),
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFF8FAFF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BookingGray)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = room.priceText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = room.taxesText,
                                    color = BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = room.availabilityText,
                                    color = if (room.enabled) Color(0xFFD11C32) else BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                                BookingPrimaryButton(
                                    text = "Select",
                                    enabled = room.enabled,
                                    modifier = Modifier.padding(top = 12.dp),
                                    onClick = {
                                        presenter.selectRoom(room.roomId)
                                        onRoomSelected()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StayPhotoGallery(assetPaths: List<String?>) {
    val galleryAssetPaths = if (assetPaths.isEmpty()) {
        List(6) { null }
    } else {
        List(6) { index -> assetPaths[index % assetPaths.size] }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BookingReferenceImage(
                assetPath = galleryAssetPaths[0],
                modifier = Modifier
                    .weight(1f)
                    .height(168.dp),
                contentDescription = "Stay gallery photo"
            )
            BookingReferenceImage(
                assetPath = galleryAssetPaths[1],
                modifier = Modifier
                    .weight(1f)
                    .height(168.dp),
                contentDescription = "Stay gallery photo"
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            galleryAssetPaths.drop(2).forEach { assetPath ->
                BookingReferenceImage(
                    assetPath = assetPath,
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    compact = true,
                    contentDescription = "Stay gallery photo"
                )
            }
        }
    }
}

@Composable
private fun PropertyHighlightsRow(
    amenities: List<String>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(amenities) { amenity ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = BookingWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, BookingGray)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(Color(0xFFF3F6FB), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = amenity.take(1).uppercase(),
                            color = BookingBlueLight,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = amenity,
                        color = BookingTextPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                }
            }
        }
    }
}

