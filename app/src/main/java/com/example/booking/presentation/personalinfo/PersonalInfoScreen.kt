package com.example.booking.presentation.personalinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.example.booking.ui.components.BookingInitialsAvatar
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.components.BookingSettingRow
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun PersonalInfoScreen(
    onBackClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(PersonalInfoUiState()) }

    val view = remember {
        object : PersonalInfoContract.View {
            override fun showState(state: PersonalInfoUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { PersonalInfoPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Personal information",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Text(
                    text = "We save key details here to help you book faster.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                BookingRoundedCard {
                    uiState.profileFields.forEachIndexed { index, field ->
                        BookingSettingRow(
                            title = field.title,
                            subtitle = field.value,
                            showChevron = true
                        )
                        if (index != uiState.profileFields.lastIndex) {
                            BookingCardDivider()
                        }
                    }
                }
            }
            item {
                Column {
                    BookingSectionHeader(
                        title = "Contact details",
                        subtitle = "Property partners may use these details if they need to reach you."
                    )
                    BookingRoundedCard(modifier = Modifier.padding(top = 10.dp)) {
                        uiState.contactFields.forEachIndexed { index, field ->
                            BookingSettingRow(
                                title = field.title,
                                subtitle = field.value,
                                badgeText = field.badge,
                                showChevron = true
                            )
                            if (index != uiState.contactFields.lastIndex) {
                                BookingCardDivider()
                            }
                        }
                    }
                    Text(
                        text = "This is the email used to sign in. Booking confirmations can also be sent here.",
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 10.dp, start = 4.dp, end = 4.dp)
                    )
                }
            }
            item {
                Column {
                    BookingSectionHeader(
                        title = "How your details appear",
                        subtitle = "This information is used across saved items and future bookings."
                    )
                    BookingRoundedCard(modifier = Modifier.padding(top = 10.dp)) {
                        BookingSettingRow(
                            title = "Avatar",
                            subtitle = "Profile badge",
                            leadingIcon = Icons.Filled.AccountCircle,
                            showChevron = false
                        )
                        BookingInitialsAvatar(
                            initials = uiState.initials,
                            modifier = Modifier.padding(vertical = 14.dp)
                        )
                        uiState.displayFields.forEachIndexed { index, field ->
                            BookingCardDivider()
                            BookingSettingRow(
                                title = field.title,
                                subtitle = field.value,
                                showChevron = true
                            )
                        }
                    }
                }
            }
        }
    }
}
