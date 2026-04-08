package com.example.booking.presentation.flights.booking

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlightTakeoff
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
import com.example.booking.presentation.flights.common.FlightFareOption
import com.example.booking.presentation.flights.common.FlightFlexibleTicketOption
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingEmptyState
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

@Composable
fun FlightFareScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightFareUiState()) }

    val view = remember {
        object : FlightFareContract.View {
            override fun showState(state: FlightFareUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightFarePresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Choose your fare",
                onBackClick = onBackClick
            )
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canContinue) {
                com.example.booking.presentation.stays.common.StayFooterBar(
                    priceLine = uiState.totalPriceText,
                    subLine = "Total",
                    buttonText = "Select",
                    onClick = onContinueClick
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.canContinue) {
            BookingEmptyState(
                icon = Icons.Filled.FlightTakeoff,
                title = "Select a flight first",
                description = "Choose a flight result before selecting a fare.",
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
                    BookingSectionHeader(title = uiState.title)
                }
                items(uiState.options) { option ->
                    FareOptionCard(
                        option = option,
                        onClick = {
                            presenter.applyFare(option.option)
                            presenter.loadData(context)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FlightLuggageScreen(
    onBackClick: () -> Unit,
    onMealChoiceClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightLuggageUiState()) }
    var noAdditionalBaggage by rememberSaveable { mutableStateOf(false) }
    var selectedFlexibleOption by rememberSaveable { mutableStateOf(FlightFlexibleTicketOption.Standard) }

    val view = remember {
        object : FlightLuggageContract.View {
            override fun showState(state: FlightLuggageUiState) {
                uiState = state
                selectedFlexibleOption = state.flexibleOptions.firstOrNull { it.selected }?.option ?: FlightFlexibleTicketOption.Standard
            }
        }
    }
    val presenter = remember(view) { FlightLuggagePresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Luggage", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canContinue) {
                com.example.booking.presentation.stays.common.StayFooterBar(
                    priceLine = uiState.totalPriceText,
                    subLine = "Total",
                    buttonText = "Next",
                    onClick = {
                        presenter.applySelection(noAdditionalBaggage, selectedFlexibleOption)
                        onContinueClick()
                    }
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.canContinue) {
            BookingEmptyState(
                icon = Icons.Filled.FlightTakeoff,
                title = "Select a fare first",
                description = "Choose a fare before adjusting luggage and flexibility.",
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
                        Text(
                            text = uiState.includedBagText,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Included",
                            color = Color(0xFF1C7C35),
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                item {
                    BookingRoundedCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { noAdditionalBaggage = !noAdditionalBaggage },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "No more baggage can be added right now",
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "You can still continue with the included allowance.",
                                    color = BookingTextSecondary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            androidx.compose.material3.Checkbox(
                                checked = noAdditionalBaggage,
                                onCheckedChange = { noAdditionalBaggage = it }
                            )
                        }
                    }
                }
                item {
                    BookingRoundedCard(modifier = Modifier.clickable(onClick = onMealChoiceClick)) {
                        BookingSectionHeader(title = "Meal choice", subtitle = "Request dietary preferences")
                        Text(
                            text = uiState.mealChoice,
                            color = BookingTextPrimary,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
                item {
                    BookingSectionHeader(title = "Flexible ticket", subtitle = "Plans change. Your flight can, too.")
                }
                items(uiState.flexibleOptions) { option ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedFlexibleOption = option.option },
                        shape = RoundedCornerShape(16.dp),
                        color = BookingWhite,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (option.option == selectedFlexibleOption) BookingBlueLight else BookingGray
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = option.title,
                                    color = BookingTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = option.priceText,
                                    color = BookingTextSecondary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            RadioButton(
                                selected = option.option == selectedFlexibleOption,
                                onClick = { selectedFlexibleOption = option.option }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlightMealChoiceScreen(
    onBackClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(FlightMealChoiceUiState()) }

    val view = remember {
        object : FlightMealChoiceContract.View {
            override fun showState(state: FlightMealChoiceUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightMealChoicePresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Meal choice",
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(uiState.options) { option ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            presenter.selectMeal(option)
                            onBackClick()
                        },
                    color = if (option == uiState.selectedMeal) Color(0xFFE9F3FF) else BookingWhite,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.weight(1f),
                            color = BookingTextPrimary,
                            fontWeight = if (option == uiState.selectedMeal) FontWeight.SemiBold else FontWeight.Normal
                        )
                        if (option == uiState.selectedMeal) {
                            Text(text = "Check", color = BookingBlueLight)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlightSeatSelectionScreen(
    onBackClick: () -> Unit,
    onContinueClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightSeatUiState()) }

    val view = remember {
        object : FlightSeatContract.View {
            override fun showState(state: FlightSeatUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightSeatPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Seat selection", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            if (uiState.canContinue) {
                com.example.booking.presentation.stays.common.StayFooterBar(
                    priceLine = uiState.totalPriceText,
                    subLine = "Total",
                    buttonText = "Next",
                    onClick = onContinueClick
                )
            }
        }
    ) { innerPadding ->
        if (!uiState.canContinue) {
            BookingEmptyState(
                icon = Icons.Filled.FlightTakeoff,
                title = "Select a flight first",
                description = "The seat selection page needs a chosen itinerary.",
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
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    BookingSectionHeader(title = uiState.routeTitle)
                }
                items(uiState.seatRows) { row ->
                    BookingRoundedCard {
                        Text(
                            text = row,
                            color = BookingTextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "No seats selected",
                            color = BookingTextSecondary,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                        Text(
                            text = "Select seats from a later airline step",
                            color = BookingBlueLight,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FlightTravelerDetailsScreen(
    onBackClick: () -> Unit,
    onDoneClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightTravelerDetailsUiState()) }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }

    val view = remember {
        object : FlightTravelerDetailsContract.View {
            override fun showState(state: FlightTravelerDetailsUiState) {
                uiState = state
                firstName = state.firstName
                lastName = state.lastName
                gender = state.gender
            }
        }
    }
    val presenter = remember(view) { FlightTravelerDetailsPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    val formValid = firstName.isNotBlank() && lastName.isNotBlank() && gender.isNotBlank()

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Traveler details", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            com.example.booking.presentation.stays.common.StayFooterBar(
                priceLine = uiState.totalPriceText,
                subLine = "Total",
                buttonText = "Done",
                enabled = formValid,
                onClick = {
                    presenter.saveDetails(firstName, lastName, gender)
                    onDoneClick()
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BookingRoundedCard {
                    Text(
                        text = "Double-check your details",
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Make sure your details match your passport or ID. Some airlines do not allow changes after booking.",
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            item {
                BookingFormField(
                    label = "First names",
                    value = firstName,
                    onValueChange = { firstName = it },
                    keyboardType = KeyboardType.Text
                )
            }
            item {
                BookingFormField(
                    label = "Last names",
                    value = lastName,
                    onValueChange = { lastName = it },
                    keyboardType = KeyboardType.Text
                )
            }
            item {
                BookingFormField(
                    label = "Gender specified on your passport / ID",
                    value = gender,
                    onValueChange = { gender = it },
                    keyboardType = KeyboardType.Text
                )
            }
        }
    }
}

@Composable
fun FlightTravelerContactScreen(
    onBackClick: () -> Unit,
    onBookingComplete: (String) -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightTravelerContactUiState()) }
    var email by rememberSaveable { mutableStateOf("") }
    var phoneCountryCode by rememberSaveable { mutableStateOf("+1") }
    var phoneNumber by rememberSaveable { mutableStateOf("") }

    val view = remember {
        object : FlightTravelerContactContract.View {
            override fun showState(state: FlightTravelerContactUiState) {
                uiState = state
                email = state.email
                phoneCountryCode = state.phoneCountryCode
                phoneNumber = state.phoneNumber
            }
        }
    }
    val presenter = remember(view) { FlightTravelerContactPresenter(view) }

    LaunchedEffect(presenter, context) {
        presenter.loadData(context)
    }

    val formValid = email.contains("@") && phoneNumber.isNotBlank() && uiState.canComplete

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Traveler details", onBackClick = onBackClick)
        },
        containerColor = BookingWhite,
        bottomBar = {
            com.example.booking.presentation.stays.common.StayFooterBar(
                priceLine = uiState.totalPriceText,
                subLine = "Total",
                buttonText = "Done",
                enabled = formValid,
                onClick = {
                    presenter.completeBooking(context, email, phoneCountryCode, phoneNumber)?.let(onBookingComplete)
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                BookingSectionHeader(title = "Contact details")
            }
            item {
                BookingFormField(
                    label = "Contact email",
                    value = email,
                    onValueChange = { email = it },
                    keyboardType = KeyboardType.Email
                )
            }
            item {
                Text(
                    text = "Contact number",
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        modifier = Modifier.weight(0.65f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            }
        }
    }
}

@Composable
fun FlightBookingSuccessScreen(
    orderId: String,
    onBackClick: () -> Unit,
    onViewTripsClick: () -> Unit,
    onSearchAgainClick: () -> Unit
) {
    val context = LocalContext.current.applicationContext
    var uiState by remember { mutableStateOf(FlightBookingSuccessUiState()) }

    val view = remember {
        object : FlightBookingSuccessContract.View {
            override fun showState(state: FlightBookingSuccessUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { FlightBookingSuccessPresenter(view) }

    LaunchedEffect(presenter, context, orderId) {
        presenter.loadData(context, orderId)
    }

    Scaffold(
        topBar = {
            BookingBackTopBar(title = "Booking complete", onBackClick = onBackClick)
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        if (!uiState.hasOrder) {
            BookingEmptyState(
                icon = Icons.Filled.FlightTakeoff,
                title = uiState.title.ifBlank { "Booking not found" },
                description = uiState.note.ifBlank { "This order is missing from the local runtime file." },
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
                        .clickable(enabled = false) {}
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = Color(0xFFDDF4DE)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF1C7C35),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
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
                        text = uiState.totalPriceText,
                        style = MaterialTheme.typography.headlineSmall,
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 18.dp)
                    )
                    Text(
                        text = "Order ID: ${uiState.orderId}",
                        color = BookingTextSecondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
                BookingPrimaryButton(
                    text = "View trips",
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = onViewTripsClick
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .clickable(onClick = onSearchAgainClick),
                    shape = RoundedCornerShape(10.dp),
                    color = BookingWhite,
                    border = BorderStroke(1.dp, BookingBlueLight)
                ) {
                    Text(
                        text = "Search again",
                        color = BookingBlue,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FareOptionCard(
    option: FlightFareOptionUiModel,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = BookingWhite,
        border = BorderStroke(
            width = 1.dp,
            color = if (option.selected) BookingBlueLight else BookingGray
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = option.subtitle,
                    color = BookingTextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            RadioButton(selected = option.selected, onClick = onClick)
        }
    }
}

@Composable
private fun BookingFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    Column {
        Text(
            text = label,
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
