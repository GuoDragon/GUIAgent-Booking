package com.example.booking.presentation.taxi.input

import android.content.Context
import java.time.LocalDateTime

interface TaxiLocationContract {
    interface View {
        fun showState(state: TaxiLocationUiState)
    }

    interface Presenter {
        fun loadPickupLocations(context: Context)
        fun loadDestinations(context: Context)
        fun selectPickupLocation(value: String)
        fun selectDestination(value: String)
    }
}

data class TaxiLocationUiState(
    val title: String = "",
    val subtitle: String = "",
    val selectedValue: String = "",
    val options: List<String> = emptyList()
)

interface TaxiTimeContract {
    interface View {
        fun showState(state: TaxiTimeUiState)
    }

    interface Presenter {
        fun loadTimeOptions()
        fun applySelection(pickupDateTime: LocalDateTime, returnDateTime: LocalDateTime)
    }
}

data class TaxiTimeUiState(
    val tripLabel: String = "",
    val currentPickupLabel: String = "",
    val currentReturnLabel: String = "",
    val pickupOptions: List<LocalDateTime> = emptyList(),
    val returnOptions: List<LocalDateTime> = emptyList(),
    val roundTrip: Boolean = false
)

interface TaxiPassengerContract {
    interface View {
        fun showState(state: TaxiPassengerUiState)
    }

    interface Presenter {
        fun loadData()
        fun updatePassengerCount(value: Int)
    }
}

data class TaxiPassengerUiState(
    val tripLabel: String = "",
    val passengerCount: Int = 1,
    val helperText: String = ""
)
