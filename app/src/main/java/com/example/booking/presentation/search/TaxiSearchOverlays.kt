package com.example.booking.presentation.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.unit.dp
import com.example.booking.common.format.BookingFormatters
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingSheetHandle
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingBlueLight
import com.example.booking.ui.theme.BookingGray
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite
import java.time.LocalDateTime

enum class TaxiRoutePlannerFocus {
    Pickup,
    Destination
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxiRoutePlannerDialog(
    initialPickupLocation: String,
    initialDestination: String,
    focus: TaxiRoutePlannerFocus,
    onDismissRequest: () -> Unit,
    onConfirm: (pickupLocation: String, destination: String) -> Unit
) {
    var pickupLocation by remember(initialPickupLocation) { mutableStateOf(initialPickupLocation) }
    var destination by remember(initialDestination) { mutableStateOf(initialDestination) }

    val pickupFocusRequester = remember { FocusRequester() }
    val destinationFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    fun commitAndDismiss() {
        onConfirm(pickupLocation.trim(), destination.trim())
        onDismissRequest()
    }

    LaunchedEffect(focus) {
        when (focus) {
            TaxiRoutePlannerFocus.Pickup -> pickupFocusRequester.requestFocus()
            TaxiRoutePlannerFocus.Destination -> destinationFocusRequester.requestFocus()
        }
    }

    Dialog(
        onDismissRequest = { commitAndDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = BookingWhite
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "Route planner",
                                color = BookingWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { commitAndDismiss() }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    tint = BookingWhite
                                )
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = BookingBlue,
                            titleContentColor = BookingWhite
                        )
                    )
                },
                containerColor = BookingWhite
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding())
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp),
                        color = BookingWhite,
                        border = BorderStroke(1.dp, BookingGray)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = BookingTextSecondary
                                )
                                OutlinedTextField(
                                    value = pickupLocation,
                                    onValueChange = { pickupLocation = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                        .focusRequester(pickupFocusRequester),
                                    placeholder = { Text("Enter pick-up location") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Next
                                    )
                                )
                                IconButton(onClick = {
                                    val cached = pickupLocation
                                    pickupLocation = destination
                                    destination = cached
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.SwapVert,
                                        contentDescription = "Swap locations",
                                        tint = BookingBlueLight
                                    )
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(BookingGray)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = null,
                                    tint = BookingTextSecondary
                                )
                                OutlinedTextField(
                                    value = destination,
                                    onValueChange = { destination = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                        .focusRequester(destinationFocusRequester),
                                    placeholder = { Text("Enter destination") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(
                                        keyboardType = KeyboardType.Text,
                                        imeAction = ImeAction.Done
                                    ),
                                    keyboardActions = KeyboardActions(
                                        onDone = {
                                            keyboardController?.hide()
                                            commitAndDismiss()
                                        }
                                    )
                                )
                            }
                        }
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 20.dp)
                            .clickable {
                                pickupLocation = "Current location"
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = null,
                            tint = BookingBlueLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Use current location",
                            color = BookingBlueLight,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxiScheduleTimeSheet(
    title: String,
    helperText: String,
    initialDateTime: LocalDateTime,
    onDismissRequest: () -> Unit,
    onConfirm: (LocalDateTime) -> Unit
) {
    val initialDate = initialDateTime.toLocalDate()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = BookingFormatters.localDateToEpochMillis(initialDate)
    )
    var timeText by remember(initialDateTime) { mutableStateOf(BookingFormatters.formatTime(initialDateTime)) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 24.dp)
        ) {
            BookingSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = title,
                color = BookingTextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )
            Text(
                text = helperText,
                color = BookingTextSecondary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 6.dp)
            )
            DatePicker(
                state = datePickerState,
                title = null,
                headline = null,
                showModeToggle = false,
                modifier = Modifier.padding(top = 8.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = BookingTextPrimary
                    )
                    Text(
                        text = "Time",
                        color = BookingTextPrimary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                OutlinedTextField(
                    value = timeText,
                    onValueChange = { value ->
                        timeText = value
                            .filter { it.isDigit() || it == ':' }
                            .take(5)
                    },
                    singleLine = true,
                    modifier = Modifier.width(120.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    placeholder = { Text("12:00") }
                )
            }
            BookingPrimaryButton(
                text = "Confirm",
                modifier = Modifier.padding(top = 18.dp),
                onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                        ?.let(BookingFormatters::epochMillisToLocalDate)
                        ?: initialDate
                    val selectedTime = parseTime(timeText)
                        ?: (initialDateTime.hour to initialDateTime.minute)
                    onConfirm(
                        selectedDate.atTime(
                            selectedTime.first.coerceIn(0, 23),
                            selectedTime.second.coerceIn(0, 59)
                        )
                    )
                    onDismissRequest()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxiPassengersSheet(
    initialPassengers: Int,
    onDismissRequest: () -> Unit,
    onDoneClick: (Int) -> Unit
) {
    var passengerCount by remember(initialPassengers) { mutableStateOf(initialPassengers.coerceIn(1, 8)) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 24.dp)
        ) {
            BookingSheetHandle(modifier = Modifier.align(Alignment.CenterHorizontally))
            Text(
                text = "Select passengers",
                color = BookingTextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 14.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Passengers",
                    color = BookingTextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = BookingWhite,
                    border = BorderStroke(1.dp, BookingGray)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        Text(
                            text = "−",
                            color = BookingBlueLight,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.clickable {
                                passengerCount = (passengerCount - 1).coerceAtLeast(1)
                            }
                        )
                        Text(
                            text = passengerCount.toString(),
                            color = BookingTextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "+",
                            color = BookingBlueLight,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.clickable {
                                passengerCount = (passengerCount + 1).coerceAtMost(8)
                            }
                        )
                    }
                }
            }
            BookingPrimaryButton(
                text = "Done",
                modifier = Modifier.padding(top = 18.dp),
                onClick = {
                    onDoneClick(passengerCount)
                    onDismissRequest()
                }
            )
        }
    }
}

private fun parseTime(value: String): Pair<Int, Int>? {
    val parts = value.trim().split(":")
    if (parts.size != 2) return null
    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null
    return hour to minute
}
