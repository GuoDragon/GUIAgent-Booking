package com.example.booking.presentation.flightplushotel

import android.content.Context

interface FlightPlusHotelContract {
    interface View {
        fun showState(state: FlightPlusHotelUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class FlightPlusHotelUiState(
    val flightTitle: String = "",
    val flightSubtitle: String = "",
    val flightPrice: String = "",
    val flightCountLabel: String = "",
    val stayTitle: String = "",
    val staySubtitle: String = "",
    val stayPrice: String = "",
    val stayCountLabel: String = ""
)
