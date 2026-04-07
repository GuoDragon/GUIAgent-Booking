package com.example.booking.presentation.taxi.results

import android.content.Context

interface TaxiResultsContract {
    interface View {
        fun showState(state: TaxiResultsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun selectRoute(context: Context, routeId: String)
    }
}

data class TaxiResultsUiState(
    val title: String = "",
    val subtitle: String = "",
    val cards: List<TaxiResultCardUiModel> = emptyList(),
    val selectedRouteLabel: String = "",
    val selectedPriceText: String = "",
    val canContinue: Boolean = false
)

data class TaxiResultCardUiModel(
    val cardId: String,
    val routeId: String,
    val title: String,
    val seatText: String,
    val bagText: String,
    val driverText: String,
    val cancelText: String,
    val locationText: String,
    val durationText: String,
    val priceText: String,
    val selected: Boolean
)
