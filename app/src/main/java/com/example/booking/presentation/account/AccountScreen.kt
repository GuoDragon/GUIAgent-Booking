package com.example.booking.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsApplications
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.booking.ui.components.BookingCardDivider
import com.example.booking.ui.components.BookingInitialsAvatar
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.components.BookingSettingRow
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

private data class AccountSectionUiModel(
    val title: String,
    val rows: List<AccountRowUiModel>
)

private data class AccountRowUiModel(
    val title: String,
    val icon: ImageVector,
    val action: AccountAction = AccountAction.NoAction
)

private enum class AccountAction {
    NoAction,
    PersonalInformation,
    TravelCompanions
}

@Composable
fun AccountScreen(
    onPersonalInfoClick: () -> Unit,
    onTravelCompanionsClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(AccountUiState()) }

    val view = remember {
        object : AccountContract.View {
            override fun showState(state: AccountUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AccountPresenter(view) }
    val runtimeDataVersion by presenter.observeRuntimeVersion().collectAsState()

    LaunchedEffect(presenter, context, runtimeDataVersion) {
        presenter.loadData(context)
    }

    val sections = remember {
        listOf(
            AccountSectionUiModel(
                title = "Payment details",
                rows = listOf(
                    AccountRowUiModel("Rewards and wallet", Icons.Filled.AccountBalanceWallet),
                    AccountRowUiModel("Payment methods", Icons.Filled.Payment),
                    AccountRowUiModel("Transactions", Icons.Outlined.WorkOutline)
                )
            ),
            AccountSectionUiModel(
                title = "Manage account",
                rows = listOf(
                    AccountRowUiModel("Personal information", Icons.Outlined.PersonOutline, AccountAction.PersonalInformation),
                    AccountRowUiModel("Security settings", Icons.Filled.Lock),
                    AccountRowUiModel("Travel companions", Icons.Filled.TravelExplore, AccountAction.TravelCompanions)
                )
            ),
            AccountSectionUiModel(
                title = "Preferences",
                rows = listOf(
                    AccountRowUiModel("Device preferences", Icons.Filled.Devices),
                    AccountRowUiModel("Travel preferences", Icons.Filled.SettingsApplications),
                    AccountRowUiModel("Communication preferences", Icons.Outlined.ChatBubbleOutline)
                )
            ),
            AccountSectionUiModel(
                title = "Travel activity",
                rows = listOf(
                    AccountRowUiModel("Saved lists", Icons.Outlined.FavoriteBorder),
                    AccountRowUiModel("My reviews", Icons.Filled.VerifiedUser),
                    AccountRowUiModel("Questions about my stays", Icons.Filled.Quiz)
                )
            ),
            AccountSectionUiModel(
                title = "Help and support",
                rows = listOf(
                    AccountRowUiModel("Contact customer service", Icons.Filled.ContactSupport),
                    AccountRowUiModel("Safe travel information center", Icons.Filled.Security)
                )
            ),
            AccountSectionUiModel(
                title = "Legal and privacy",
                rows = listOf(
                    AccountRowUiModel("Privacy and data management", Icons.Filled.PrivacyTip)
                )
            ),
            AccountSectionUiModel(
                title = "Discover more",
                rows = listOf(
                    AccountRowUiModel("Deals", Icons.Filled.LocalOffer)
                )
            ),
            AccountSectionUiModel(
                title = "Accommodation management",
                rows = listOf(
                    AccountRowUiModel("List your property", Icons.Outlined.WorkOutline)
                )
            )
        )
    }

    Scaffold(containerColor = BookingWhite) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AccountHeader(state = uiState)
            }
            items(sections) { section ->
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    BookingSectionHeader(title = section.title)
                    Spacer(modifier = Modifier.padding(top = 10.dp))
                    BookingRoundedCard {
                        section.rows.forEachIndexed { rowIndex, row ->
                            BookingSettingRow(
                                title = row.title,
                                leadingIcon = row.icon,
                                onClick = when (row.action) {
                                    AccountAction.PersonalInformation -> onPersonalInfoClick
                                    AccountAction.TravelCompanions -> onTravelCompanionsClick
                                    AccountAction.NoAction -> null
                                }
                            )
                            if (rowIndex != section.rows.lastIndex) {
                                BookingCardDivider()
                            }
                        }
                    }
                }
            }
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = BookingWhite
                    ) {
                        Text(
                            text = "Sign out",
                            color = Color(0xFFD13B30),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AccountHeader(state: AccountUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BookingWhite)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Messages",
                    tint = BookingTextPrimary
                )
            }
            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Outlined.NotificationsNone,
                    contentDescription = "Notifications",
                    tint = BookingTextPrimary
                )
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            BookingInitialsAvatar(initials = state.initials, size = 60)
            Column {
                Text(
                    text = "Hi, ${state.firstName}",
                    style = MaterialTheme.typography.headlineSmall,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.geniusLevelLabel,
                    color = BookingTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.padding(top = 14.dp))
        BookingRoundedCard {
            Text(
                text = "You have 3 Genius perks waiting",
                color = BookingTextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Unlock extra savings and easier trips with your account.",
                color = BookingTextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Spacer(modifier = Modifier.padding(top = 12.dp))
        BookingRoundedCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Travel credits and offers",
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.email.ifBlank { "No contact email saved" },
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
                Text(
                    text = "HK$ 0",
                    color = BookingBlueLight,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
