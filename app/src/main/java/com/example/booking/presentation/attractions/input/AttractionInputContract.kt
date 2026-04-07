package com.example.booking.presentation.attractions.input

import android.content.Context
import java.time.LocalDate

interface AttractionDestinationContract {
    interface View {
        fun showState(state: AttractionDestinationUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun selectDestination(value: String)
    }
}

data class AttractionDestinationUiState(
    val selectedValue: String = "",
    val options: List<String> = emptyList()
)

interface AttractionDateContract {
    interface View {
        fun showState(state: AttractionDateUiState)
    }

    interface Presenter {
        fun loadData()
        fun selectDate(date: LocalDate)
    }
}

data class AttractionDateUiState(
    val selectedDate: LocalDate = LocalDate.now().plusDays(1),
    val options: List<LocalDate> = emptyList()
)
