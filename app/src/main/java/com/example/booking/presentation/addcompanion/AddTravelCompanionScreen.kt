package com.example.booking.presentation.addcompanion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.theme.BookingWhite

@Composable
fun AddTravelCompanionScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    var uiState by remember { mutableStateOf(AddTravelCompanionUiState()) }

    val view = remember {
        object : AddTravelCompanionContract.View {
            override fun showState(state: AddTravelCompanionUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { AddTravelCompanionPresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var dateOfBirth by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var consentChecked by rememberSaveable { mutableStateOf(false) }
    var genderMenuExpanded by rememberSaveable { mutableStateOf(false) }

    val canSave = firstName.isNotBlank() &&
        lastName.isNotBlank() &&
        dateOfBirth.isNotBlank() &&
        gender.isNotBlank() &&
        consentChecked

    Scaffold(
        topBar = {
            BookingBackTopBar(
                title = "Add traveler",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Box(modifier = Modifier.padding(16.dp)) {
                BookingPrimaryButton(
                    text = "Save",
                    enabled = canSave,
                    onClick = {
                        if (canSave) {
                            onSaveClick()
                        }
                    }
                )
            }
        },
        containerColor = BookingWhite
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding()),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 18.dp,
                bottom = innerPadding.calculateBottomPadding() + 18.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Text(
                    text = "Enter traveler details carefully and make sure you have consent before saving them locally.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            item {
                FormField(
                    label = "First name *",
                    value = firstName,
                    onValueChange = { firstName = it }
                )
            }
            item {
                FormHint(text = "Use the first name shown on the traveler’s passport or official travel document.")
            }
            item {
                FormField(
                    label = "Last name *",
                    value = lastName,
                    onValueChange = { lastName = it }
                )
            }
            item {
                FormField(
                    label = "Date of birth *",
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    placeholder = "DD/MM/YYYY",
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.CalendarToday,
                            contentDescription = null
                        )
                    }
                )
            }
            item {
                FormHint(text = "Correct birth dates are important and may be used for tickets and reservations.")
            }
            item {
                Box {
                    FormField(
                        label = "Gender *",
                        value = gender,
                        onValueChange = {},
                        placeholder = "Select gender",
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { genderMenuExpanded = true }
                    )
                    DropdownMenu(
                        expanded = genderMenuExpanded,
                        onDismissRequest = { genderMenuExpanded = false }
                    ) {
                        uiState.genderOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    gender = option
                                    genderMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            item {
                FormHint(text = "Choose the gender listed on the traveler’s passport or travel document.")
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Checkbox(
                        checked = consentChecked,
                        onCheckedChange = { consentChecked = it }
                    )
                    Text(
                        text = "I confirm that I have permission to provide this traveler’s personal data to Booking.com for trip services.",
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = {
            Text(
                text = label,
                fontWeight = FontWeight.Medium
            )
        },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(text = placeholder)
            }
        },
        readOnly = readOnly,
        singleLine = true,
        trailingIcon = trailingIcon
    )
}

@Composable
private fun FormHint(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            modifier = Modifier.padding(top = 2.dp, end = 8.dp)
        )
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}
