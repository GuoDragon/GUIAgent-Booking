package com.example.booking.presentation.taxi.booking

import android.content.Context

interface TaxiAddFlightTrackingContract {
    interface View {
        fun showState(state: TaxiAddFlightTrackingUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun saveDepartureAirportQuery(query: String)
    }
}

data class TaxiAddFlightTrackingUiState(
    val pickupAirportLabel: String = "",
    val departureAirportQuery: String = "",
    val selectedFlightTitle: String = "",
    val selectedFlightSubtitle: String = ""
)
