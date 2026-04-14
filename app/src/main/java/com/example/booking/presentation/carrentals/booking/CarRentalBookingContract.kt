package com.example.booking.presentation.carrentals.booking

import android.content.Context

interface CarRentalSummaryContract {
    interface View {
        fun showState(state: CarRentalSummaryUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun completeBooking(context: Context, childSeatRequired: Boolean): String?
    }
}

data class CarRentalSummaryUiState(
    val title: String = "",
    val subtitle: String = "",
    val pickupLine: String = "",
    val dropOffLine: String = "",
    val companyName: String = "",
    val includedLine: String = "",
    val priceLineItems: List<Pair<String, String>> = emptyList(),
    val totalPriceText: String = "",
    val totalLabel: String = "",
    val canContinue: Boolean = false,
    val childSeatRequired: Boolean = false
)

interface CarRentalBookingSuccessContract {
    interface View {
        fun showState(state: CarRentalBookingSuccessUiState)
    }

    interface Presenter {
        fun loadData(context: Context, orderId: String)
    }
}

data class CarRentalBookingSuccessUiState(
    val hasOrder: Boolean = false,
    val title: String = "",
    val orderId: String = "",
    val itemName: String = "",
    val dateLabel: String = "",
    val guestLabel: String = "",
    val totalPriceText: String = "",
    val note: String = ""
)
