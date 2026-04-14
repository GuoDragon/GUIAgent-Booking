package com.example.booking.presentation.personalinfo

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
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
import com.example.booking.common.format.BookingFormatters
import com.example.booking.ui.components.BookingBackTopBar
import com.example.booking.ui.components.BookingCardDivider
import com.example.booking.ui.components.BookingInitialsAvatar
import com.example.booking.ui.components.BookingPrimaryButton
import com.example.booking.ui.components.BookingRoundedCard
import com.example.booking.ui.components.BookingSectionHeader
import com.example.booking.ui.components.BookingSettingRow
import com.example.booking.ui.components.BookingSheetHandle
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite

private data class NameEditorState(
    val firstName: String = "",
    val lastName: String = ""
)

private data class PhoneEditorState(
    val countryCode: String = "+1",
    val number: String = ""
)

@Composable
fun PersonalInfoScreen(
    onBackClick: () -> Unit
) {
    val localContext = LocalContext.current
    val appContext = localContext.applicationContext
    var uiState by remember { mutableStateOf(PersonalInfoUiState()) }
    var activeEditor by remember { mutableStateOf<PersonalInfoFieldKey?>(null) }
    var nameEditor by remember { mutableStateOf(NameEditorState()) }
    var phoneEditor by remember { mutableStateOf(PhoneEditorState()) }

    val view = remember {
        object : PersonalInfoContract.View {
            override fun showState(state: PersonalInfoUiState) {
                uiState = state
            }
        }
    }
    val presenter = remember(view) { PersonalInfoPresenter(view) }

    LaunchedEffect(presenter, appContext) {
        presenter.loadData(appContext)
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
                            showChevron = true,
                            onClick = if (field.editable) {
                                {
                                    when (field.key) {
                                        PersonalInfoFieldKey.Name -> {
                                            val firstName = field.value
                                                .split(" ")
                                                .firstOrNull()
                                                .orEmpty()
                                            val lastName = field.value
                                                .split(" ")
                                                .drop(1)
                                                .joinToString(" ")
                                            nameEditor = NameEditorState(
                                                firstName = firstName,
                                                lastName = lastName
                                            )
                                            activeEditor = PersonalInfoFieldKey.Name
                                        }

                                        else -> Unit
                                    }
                                }
                            } else {
                                null
                            }
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
                                showChevron = true,
                                onClick = if (field.editable) {
                                    {
                                        when (field.key) {
                                            PersonalInfoFieldKey.PhoneNumber -> {
                                                val (phoneCountryCode, phoneNumber) = BookingFormatters.parsePhoneParts(
                                                    phone = field.value.takeUnless {
                                                        it.equals("Add phone number", ignoreCase = true) ||
                                                            it.equals("Not provided", ignoreCase = true)
                                                    }
                                                )
                                                phoneEditor = PhoneEditorState(
                                                    countryCode = phoneCountryCode,
                                                    number = phoneNumber
                                                )
                                                activeEditor = PersonalInfoFieldKey.PhoneNumber
                                            }

                                            else -> Unit
                                        }
                                    }
                                } else {
                                    null
                                }
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
                        uiState.displayFields.forEach { field ->
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

    when (activeEditor) {
        PersonalInfoFieldKey.Name -> {
            EditNameSheet(
                state = nameEditor,
                onStateChange = { nameEditor = it },
                onDismissRequest = { activeEditor = null },
                onSaveClick = {
                    val updated = presenter.updateName(
                        context = appContext,
                        firstName = nameEditor.firstName,
                        lastName = nameEditor.lastName
                    )
                    if (updated) {
                        Toast.makeText(localContext, "Name updated", Toast.LENGTH_SHORT).show()
                        activeEditor = null
                    } else {
                        Toast.makeText(localContext, "Please enter first and last name", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        PersonalInfoFieldKey.PhoneNumber -> {
            EditPhoneSheet(
                state = phoneEditor,
                onStateChange = { phoneEditor = it },
                onDismissRequest = { activeEditor = null },
                onSaveClick = {
                    val updated = presenter.updatePhone(
                        context = appContext,
                        phoneCountryCode = phoneEditor.countryCode,
                        phoneNumber = phoneEditor.number
                    )
                    if (updated) {
                        Toast.makeText(localContext, "Phone number updated", Toast.LENGTH_SHORT).show()
                        activeEditor = null
                    } else {
                        Toast.makeText(localContext, "Please enter a valid phone number", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

        else -> Unit
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditNameSheet(
    state: NameEditorState,
    onStateChange: (NameEditorState) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BookingSheetHandle(modifier = Modifier.padding(bottom = 2.dp))
            Text(
                text = "Edit name",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = state.firstName,
                onValueChange = { onStateChange(state.copy(firstName = it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("First name") },
                singleLine = true
            )
            OutlinedTextField(
                value = state.lastName,
                onValueChange = { onStateChange(state.copy(lastName = it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Last name") },
                singleLine = true
            )
            BookingPrimaryButton(
                text = "Save name",
                onClick = onSaveClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPhoneSheet(
    state: PhoneEditorState,
    onStateChange: (PhoneEditorState) -> Unit,
    onDismissRequest: () -> Unit,
    onSaveClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = BookingWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 10.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BookingSheetHandle(modifier = Modifier.padding(bottom = 2.dp))
            Text(
                text = "Edit phone number",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = state.countryCode,
                onValueChange = { onStateChange(state.copy(countryCode = it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Country code") },
                singleLine = true
            )
            OutlinedTextField(
                value = state.number,
                onValueChange = { onStateChange(state.copy(number = it)) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone number") },
                singleLine = true
            )
            BookingPrimaryButton(
                text = "Save phone",
                onClick = onSaveClick
            )
        }
    }
}
