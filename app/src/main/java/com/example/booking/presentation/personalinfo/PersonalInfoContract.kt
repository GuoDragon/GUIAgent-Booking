package com.example.booking.presentation.personalinfo

import android.content.Context

interface PersonalInfoContract {
    interface View {
        fun showState(state: PersonalInfoUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class PersonalInfoUiState(
    val initials: String = "BK",
    val profileFields: List<InfoFieldUiModel> = emptyList(),
    val contactFields: List<InfoFieldUiModel> = emptyList(),
    val displayFields: List<InfoFieldUiModel> = emptyList()
)

data class InfoFieldUiModel(
    val title: String,
    val value: String,
    val badge: String? = null
)
