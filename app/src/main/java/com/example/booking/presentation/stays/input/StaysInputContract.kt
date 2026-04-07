package com.example.booking.presentation.stays.input

import android.content.Context
import java.time.LocalDate

interface StayDestinationContract {
    interface View {
        fun showState(state: StayDestinationUiState)
    }

    interface Presenter {
        fun loadData(context: Context, query: String)
        fun applyDestination(query: String)
    }
}

data class StayDestinationUiState(
    val query: String = "",
    val recentSuggestions: List<StayDestinationSuggestionUiModel> = emptyList(),
    val propertySuggestions: List<StayDestinationSuggestionUiModel> = emptyList()
)

data class StayDestinationSuggestionUiModel(
    val title: String,
    val subtitle: String,
    val type: StayDestinationSuggestionType
)

enum class StayDestinationSuggestionType {
    Recent,
    City,
    Property
}

interface StayDateContract {
    interface View {
        fun showState(state: StayDateUiState)
    }

    interface Presenter {
        fun loadData()
        fun applyDates(checkInDate: LocalDate, checkOutDate: LocalDate)
    }
}

data class StayDateUiState(
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val calendarMonths: List<LocalDate>
)

interface StayGuestsContract {
    interface View {
        fun showState(state: StayGuestsUiState)
    }

    interface Presenter {
        fun loadData()
        fun applySelection(
            roomCount: Int,
            adultCount: Int,
            childCount: Int,
            travelingWithPets: Boolean
        )
    }
}

data class StayGuestsUiState(
    val roomCount: Int,
    val adultCount: Int,
    val childCount: Int,
    val travelingWithPets: Boolean
)
