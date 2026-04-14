package com.example.booking.presentation.personalinfo

import android.content.Context

interface PersonalInfoContract {
    interface View {
        fun showState(state: PersonalInfoUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun updateName(context: Context, firstName: String, lastName: String): Boolean
        fun updatePhone(context: Context, phoneCountryCode: String, phoneNumber: String): Boolean
    }
}

enum class PersonalInfoFieldKey {
    Name,
    Gender,
    DateOfBirth,
    PassportDetails,
    EmailAddress,
    PhoneNumber,
    Address,
    DisplayName,
    Nationality
}

data class PersonalInfoUiState(
    val initials: String = "BK",
    val profileFields: List<InfoFieldUiModel> = emptyList(),
    val contactFields: List<InfoFieldUiModel> = emptyList(),
    val displayFields: List<InfoFieldUiModel> = emptyList()
)

data class InfoFieldUiModel(
    val key: PersonalInfoFieldKey,
    val title: String,
    val value: String,
    val badge: String? = null,
    val editable: Boolean = false
)
