package com.example.booking.presentation.orders

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Luggage
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.booking.data.DataRepository
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingHomeTopBar
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.components.BookingTopBarAction
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingGreen
import com.example.booking.ui.theme.BookingRed
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun OrdersScreen() {
    val context = LocalContext.current.applicationContext
    val runtimeDataVersion by DataRepository.observeRuntimeDataVersion().collectAsState()
    var uiState by remember { mutableStateOf(OrdersUiState()) }
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    val view = remember {
        object : OrdersContract.View {
            override fun showState(state: OrdersUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { OrdersPresenter(view) }

    LaunchedEffect(presenter, context, runtimeDataVersion) {
        presenter.loadData(context)
    }

    val tabs = listOf("Active", "History", "Cancelled")
    val selectedOrders = when (selectedTabIndex) {
        0 -> uiState.activeOrders
        1 -> uiState.historyOrders
        else -> uiState.cancelledOrders
    }

    Scaffold(
        topBar = {
            BookingHomeTopBar(title = "Trips") {
                BookingTopBarAction(
                    icon = Icons.Outlined.HelpOutline,
                    contentDescription = "Help"
                )
                BookingTopBarAction(
                    icon = Icons.Filled.CloudUpload,
                    contentDescription = "Cloud"
                )
            }
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                tabs.forEachIndexed { index, title ->
                    OrdersTab(
                        title = title,
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (selectedOrders.isEmpty()) {
                val emptyTitle = when (selectedTabIndex) {
                    0 -> "Your next trip starts here"
                    1 -> "Revisit your travel history"
                    else -> "Plans can always change"
                }
                val emptyDescription = when (selectedTabIndex) {
                    0 -> "Upcoming bookings will appear here as soon as you make one."
                    1 -> "Completed stays and rides will stay here for future inspiration."
                    else -> "Cancelled trips remain here in case you want to book again."
                }

                BookingEmptyState(
                    icon = Icons.Filled.Luggage,
                    title = emptyTitle,
                    description = emptyDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 24.dp)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(selectedOrders, key = { it.orderId }) { order ->
                        OrderCard(order = order)
                    }
                }
            }
        }
    }
}

@Composable
private fun OrdersTab(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(40.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) BookingBlueLight.copy(alpha = 0.12f) else BookingWhite,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) BookingBlueLight else BookingGray
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = title,
                color = if (selected) BookingBlue else BookingTextPrimary,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun OrderCard(order: OrderCardUiModel) {
    val chipColors = when (order.status) {
        "ACTIVE" -> Pair(Color(0xFFDDF4DE), BookingGreen)
        "CANCELLED" -> Pair(Color(0xFFFFE2E0), BookingRed)
        else -> Pair(Color(0xFFE3F0FF), BookingBlueLight)
    }

    BookingRoundedCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BookingStatusChip(
                text = when (order.status) {
                    "ACTIVE" -> "Active"
                    "CANCELLED" -> "Cancelled"
                    else -> "History"
                },
                containerColor = chipColors.first,
                contentColor = chipColors.second
            )
            Text(
                text = order.totalPrice,
                style = MaterialTheme.typography.titleMedium,
                color = BookingTextPrimary,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = order.itemName,
            style = MaterialTheme.typography.titleMedium,
            color = BookingTextPrimary,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = order.typeLabel,
            color = BookingBlueLight,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = order.dateRange,
            color = BookingTextPrimary,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            text = order.guestLabel,
            color = BookingTextSecondary,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            text = order.bookedOn,
            color = BookingTextSecondary,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}

