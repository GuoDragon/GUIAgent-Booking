package com.example.booking.presentation.addcompanion

interface AddTravelCompanionContract {
    interface View {
        fun showState(state: AddTravelCompanionUiState)
    }

    interface Presenter {
        fun loadData()
    }
}

data class AddTravelCompanionUiState(
    val genderOptions: List<String> = emptyList()
)
