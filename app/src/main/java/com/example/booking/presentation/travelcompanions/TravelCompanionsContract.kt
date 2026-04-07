package com.example.booking.presentation.travelcompanions

import android.content.Context

interface TravelCompanionsContract {
    interface View {
        fun showState(state: TravelCompanionsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class TravelCompanionsUiState(
    val companions: List<TravelCompanionUiModel> = emptyList()
)

data class TravelCompanionUiModel(
    val companionId: String,
    val fullName: String,
    val dateOfBirth: String,
    val gender: String
)
