package com.example.booking.presentation.stays.booking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.presentation.stays.common.StaySummaryInfoCard
import com.example.booking.presentation.stays.common.StayTripPurpose
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.components.BookingStatusChip
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun StayPersonalInfoScreen(
    onBackClick: () -> Unit,
    onNextClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    val selectedRoomId = StayDraftStore.snapshot().selectedRoomId
    var uiState by remember { mutableStateOf(StayPersonalInfoUiState()) }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneCountryCode by rememberSaveable { mutableStateOf("+1") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var countryOrRegion by rememberSaveable { mutableStateOf("") }
    var saveToAccount by rememberSaveable { mutableStateOf(false) }
    var tripPurpose by rememberSaveable { mutableStateOf<StayTripPurpose?>(null) }

    val view = remember {
        object : StayPersonalInfoContract.View {
            override fun showState(state: StayPersonalInfoUiState) {
                uiState = state
                firstName = state.firstName
                lastName = state.lastName
                email = state.email
                phoneCountryCode = state.phoneCountryCode
                phoneNumber = state.phoneNumber
                countryOrRegion = state.countryOrRegion
                saveToAccount = state.saveToAccount
                tripPurpose = state.tripPurpose
            }
        }
    }
    val presenter = remember(view) { StayPersonalInfoPresenter(view) }

    LaunchedEffect(presenter, context, selectedRoomId) {
        presenter.loadData(context)
    }

    val isFormValid = firstName.isNotBlank() &&
        lastName.isNotBlank() &&
        email.contains("@") &&
        phoneNumber.isNotBlank() &&
        countryOrRegion.isNotBlank() &&
        uiState.roomType.isNotBlank()

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Your personal info",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.roomType.isNotBlank()) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = "Your details help the property prepare your stay.",
                    buttonText = "Next step",
                    enabled = isFormValid,
                    onClick = {
                        presenter.saveDraft(
                            firstName = firstName,
                            lastName = lastName,
                            email = email,
                            phoneCountryCode = phoneCountryCode,
                            phoneNumber = phoneNumber,
                            countryOrRegion = countryOrRegion,
                            saveToAccount = saveToAccount,
                            tripPurpose = tripPurpose
                        )
                        onNextClick()
                    }
                )
            }
        }
    ) { innerPadding ->
        if (uiState.roomType.isBlank()) {
            BookingEmptyState(
                icon = Icons.Filled.Hotel,
                title = "Select a room first",
                description = "Choose a stay room before filling in personal information.",
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
                    StaySummaryInfoCard(
                        title = uiState.hotelName,
                        description = "${uiState.roomType}\n${uiState.priceText}"
                    )
                }
                item {
                    FormField(
                        label = "First name",
                        value = firstName,
                        onValueChange = { firstName = it },
                        keyboardType = KeyboardType.Text
                    )
                }
                item {
                    FormField(
                        label = "Last name",
                        value = lastName,
                        onValueChange = { lastName = it },
                        keyboardType = KeyboardType.Text
                    )
                }
                item {
                    FormField(
                        label = "Email address",
                        value = email,
                        onValueChange = { email = it },
                        keyboardType = KeyboardType.Email
                    )
                }
                item {
                    Text(
                        text = "Mobile number",
                        style = MaterialTheme.typography.titleMedium,
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = phoneCountryCode,
                            onValueChange = { phoneCountryCode = it },
                            modifier = Modifier.weight(0.35f),
                            singleLine = true,
                            label = { Text("Code") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            modifier = Modifier.weight(0.65f),
                            singleLine = true,
                            label = { Text("Phone number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )
                    }
                    if (phoneNumber.isBlank()) {
                        Text(
                            text = "Add your mobile phone number",
                            color = Color(0xFFD11C32),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
                item {
                    FormField(
                        label = "Country / region",
                        value = countryOrRegion,
                        onValueChange = { countryOrRegion = it },
                        keyboardType = KeyboardType.Text
                    )
                }
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { saveToAccount = !saveToAccount },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Save this info to your account",
                            modifier = Modifier.weight(1f),
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                        Checkbox(
                            checked = saveToAccount,
                            onCheckedChange = { saveToAccount = it }
                        )
                    }
                }
                item {
                    BookingSectionHeader(title = "What is the primary purpose of your trip?")
                }
                items(StayTripPurpose.entries) { purpose ->
                    TripPurposeRow(
                        purpose = purpose,
                        selected = tripPurpose == purpose,
                        onClick = { tripPurpose = purpose }
                    )
                }
            }
        }
    }
}

@Composable
fun StayBookingOverviewScreen(
    onBackClick: () -> Unit,
    onBookingComplete: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    val selectedRoomId = StayDraftStore.snapshot().selectedRoomId
    var uiState by remember { mutableStateOf(StayBookingOverviewUiState()) }
    var interestedInCarRental by rememberSaveable { mutableStateOf(false) }
    var specialRequest by rememberSaveable { mutableStateOf("") }

    val view = remember {
        object : StayBookingOverviewContract.View {
            override fun showState(state: StayBookingOverviewUiState) {
                uiState = state
                interestedInCarRental = state.interestedInCarRental
                specialRequest = state.specialRequest
            }
        }
    }
    val presenter = remember(view) { StayBookingOverviewPresenter(view) }

    LaunchedEffect(presenter, context, selectedRoomId) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Booking overview",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canComplete) {
                StayFooterBar(
                    priceLine = uiState.priceText,
                    subLine = uiState.taxesText,
                    buttonText = "Final step",
                    enabled = uiState.canComplete,
                    onClick = {
                        presenter.completeBooking(
                            context = context,
                            interestedInCarRental = interestedInCarRental,
                            specialRequest = specialRequest
                        )?.let(onBookingComplete)
                    }
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.canComplete) {
            BookingEmptyState(
                icon = Icons.Filled.Hotel,
                title = "Complete the stay steps first",
                description = "A hotel, room, and guest details are required before the booking can be confirmed.",
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
                    BookingRoundedCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                                    repeat(4) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = Color(0xFFFEBB02),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    BookingStatusChip(
                                        text = uiState.ratingText,
                                        containerColor = Color(0xFFE3F0FF),
                                        contentColor = BookingBlueLight,
                                        modifier = Modifier.padding(start = 10.dp)
                                    )
                                }
                                Text(
                                    text = uiState.address,
                                    color = BookingTextSecondary,
                                    modifier = Modifier.padding(top = 12.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
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
                        Text(
                            text = uiState.guestSummary,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                item {
                    BookingRoundedCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                text = "Price",
                                style = MaterialTheme.typography.titleLarge,
                                color = BookingTextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = uiState.priceText,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.taxesText,
                                    color = BookingTextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = uiState.priceInfoText,
                            color = BookingTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                item {
                    BookingRoundedCard {
                        BookingSectionHeader(title = "Booking conditions")
                        uiState.conditions.forEach { condition ->
                            Text(
                                text = condition,
                                color = BookingTextPrimary,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
                item {
                    BookingRoundedCard {
                        BookingSectionHeader(title = "Benefits included")
                        uiState.benefits.forEach { benefit ->
                            Text(
                                text = benefit,
                                color = BookingTextPrimary,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
                item {
                    BookingRoundedCard {
                        BookingSectionHeader(title = "Your selection")
                        Text(
                            text = uiState.roomType,
                            style = MaterialTheme.typography.titleMedium,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                        Text(
                            text = uiState.guestSummary,
                            color = BookingTextPrimary,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                        Text(
                            text = "Booking for ${uiState.bookingForLabel}",
                            color = BookingBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = BookingWhite
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { interestedInCarRental = !interestedInCarRental }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "I am interested in renting a car",
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Make the most of your trip and check rental options in your confirmation.",
                                    color = BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                            }
                            Checkbox(
                                checked = interestedInCarRental,
                                onCheckedChange = { interestedInCarRental = it }
                            )
                        }
                    }
                }
                item {
                    BookingRoundedCard {
                        BookingSectionHeader(title = "Special requests")
                        OutlinedTextField(
                            value = specialRequest,
                            onValueChange = { specialRequest = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            minLines = 3,
                            label = { Text("Add a note for the property") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StayBookingSuccessScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onViewTripsClick: () -> Unit,
    onSearchAgainClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(StayBookingSuccessUiState()) }

    val view = remember {
        object : StayBookingSuccessContract.View {
            override fun showState(state: StayBookingSuccessUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { StayBookingSuccessPresenter(view) }

    LaunchedEffect(presenter, context, orderId) {
        presenter.loadData(context, orderId)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Booking complete",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (!uiState.hasOrder) {
            BookingEmptyState(
                icon = Icons.Filled.Hotel,
                title = uiState.title.ifBlank { "Booking not found" },
                description = uiState.note.ifBlank { "The local order file does not contain this booking yet." },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(Color(0xFFDDF4DE), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF1C7C35),
                        modifier = Modifier.size(48.dp)
                    )
                }
                Text(
                    text = uiState.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = uiState.note,
                    color = BookingTextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 10.dp)
                )
                BookingRoundedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    Text(
                        text = uiState.itemName,
                        style = MaterialTheme.typography.titleLarge,
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.dateLabel,
                        color = BookingTextPrimary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                    Text(
                        text = uiState.guestLabel,
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = uiState.totalPrice,
                        style = MaterialTheme.typography.headlineSmall,
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        text = uiState.bookedOn,
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "Order ID: ${uiState.orderId}",
                        color = BookingTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                BookingPrimaryButton(
                    text = "View trips",
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = onViewTripsClick
                )
                SecondaryActionButton(
                    text = "Search again",
                    modifier = Modifier.padding(top = 12.dp),
                    onClick = onSearchAgainClick
                )
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = BookingTextPrimary,
            fontWeight = FontWeight.SemiBold
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
        )
    }
}

@Composable
private fun TripPurposeRow(
    purpose: StayTripPurpose,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = BookingWhite,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, BookingGray)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = purpose.label,
                modifier = Modifier.weight(1f),
                color = BookingTextPrimary,
                fontWeight = FontWeight.Medium
            )
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun SecondaryActionButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = BookingWhite,
        border = BorderStroke(1.dp, BookingBlueLight)
    ) {
        Text(
            text = text,
            color = BookingBlue,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp)
        )
    }
}

