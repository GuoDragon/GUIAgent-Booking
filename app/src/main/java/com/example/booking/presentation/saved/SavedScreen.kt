package com.example.booking.presentation.saved

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booking.ui.components.BookingCardDivider
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingHomeTopBar
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingTopBarAction
import com.example.booking.ui.theme.BookingRed
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun SavedScreen() {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(SavedUiState()) }

    val view = remember {
        object : SavedContract.View {
            override fun showState(state: SavedUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { SavedPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingHomeTopBar(title = "Saved") {
                BookingTopBarAction(
                    icon = Icons.Filled.Add,
                    contentDescription = "Create saved list"
                )
            }
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (uiState.groups.isEmpty()) {
            BookingEmptyState(
                icon = Icons.Outlined.FavoriteBorder,
                title = "Nothing saved yet",
                description = "Saved places and travel ideas will show up here.",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = innerPadding.calculateTopPadding(),
                        bottom = 24.dp
                    )
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding()),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    BookingRoundedCard {
                        uiState.groups.forEachIndexed { index, group ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = group.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = BookingTextPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        modifier = Modifier.padding(top = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Favorite,
                                            contentDescription = null,
                                            tint = BookingRed,
                                            modifier = Modifier.padding(end = 6.dp)
                                        )
                                        Text(
                                            text = "${group.savedItemCount} saved item" +
                                                if (group.savedItemCount == 1) "" else "s",
                                            color = BookingTextSecondary
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = null,
                                    tint = BookingTextSecondary
                                )
                            }
                            if (index != uiState.groups.lastIndex) {
                                BookingCardDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}
