package com.example.booking.presentation.attractions.results

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapVert
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
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingReferenceImage
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingGreen
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun AttractionResultsScreen(
    onBackClick: () -> Unit,
    onAttractionClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AttractionResultsUiState()) }

    val view = remember {
        object : AttractionResultsContract.View {
            override fun showState(state: AttractionResultsUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AttractionResultsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Attractions", onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (uiState.cards.isEmpty()) {
            BookingEmptyState(
                icon = Icons.Filled.LocalActivity,
                title = "No attractions found",
                description = "Try another city from the local attractions data.",
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
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = BookingWhite,
                        shadowElevation = 2.dp
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = uiState.headerTitle,
                                color = BookingTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.headerSubtitle,
                                color = BookingTextSecondary,
                                modifier = Modifier.padding(top = 6.dp)
                            )
                        }
                    }
                    Row(
                        modifier = Modifier.padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AttractionChip(text = uiState.keywordLabel, icon = Icons.Filled.Search)
                        AttractionChip(text = "City")
                        AttractionChip(text = "Category")
                        AttractionChip(text = "Sort", icon = Icons.Filled.SwapVert)
                    }
                }
                items(uiState.cards, key = { it.attractionId }) { card ->
                    AttractionResultCard(
                        card = card,
                        onClick = {
                            presenter.selectAttraction(context, card.attractionId)
                            onAttractionClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun AttractionChip(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = BookingWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, BookingGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            icon?.let {
                Icon(imageVector = it, contentDescription = null, tint = BookingTextSecondary, modifier = Modifier.size(16.dp))
            }
            Text(text = text, color = BookingTextPrimary)
        }
    }
}

@Composable
private fun AttractionResultCard(
    card: AttractionResultCardUiModel,
    onClick: () -> Unit
) {
    BookingRoundedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            card.badges.forEach { badge ->
                BookingStatusChip(
                    text = badge,
                    containerColor = if ("10%" in badge) Color(0xFFDDF4DE) else Color(0xFFE3F0FF),
                    contentColor = if ("10%" in badge) BookingGreen else BookingBlueLight
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BookingReferenceImage(
                assetPath = card.imageAssetPath,
                modifier = Modifier.size(width = 104.dp, height = 132.dp),
                compact = true
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.title,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = card.cityLabel,
                    color = BookingTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = Color(0xFFFEBB02),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${card.ratingText} (${card.reviewText})",
                        color = BookingTextPrimary,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
                Text(
                    text = card.durationText,
                    color = BookingTextSecondary,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = card.priceText,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp)
                )
                Text(
                    text = card.availabilityText,
                    color = BookingGreen,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}
