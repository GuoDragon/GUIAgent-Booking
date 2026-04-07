package com.example.booking.presentation.taxi.booking

import android.content.Context

interface TaxiContactContract {
    interface View {
        fun showState(state: TaxiContactUiState)
    }

    interface Presenter {
        fun loadData()
        fun saveContact(name: String, email: String, phone: String, flightNumber: String)
    }
}

data class TaxiContactUiState(
    val hasSelection: Boolean = false,
    val pickupLine: String = "",
    val destinationLine: String = "",
    val contactName: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val flightNumber: String = ""
)

interface TaxiOverviewContract {
    interface View {
        fun showState(state: TaxiOverviewUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun completeBooking(context: Context): String?
    }
}

data class TaxiOverviewUiState(
    val hasSelection: Boolean = false,
    val routeTitle: String = "",
    val pickupLine: String = "",
    val destinationLine: String = "",
    val flightNumber: String = "",
    val passengerLine: String = "",
    val totalPriceText: String = "",
    val helperText: String = ""
)

interface TaxiBookingSuccessContract {
    interface View {
        fun showState(state: TaxiBookingSuccessUiState)
    }

    interface Presenter {
        fun loadData(context: Context, orderId: String)
    }
}

data class TaxiBookingSuccessUiState(
    val hasOrder: Boolean = false,
    val title: String = "",
    val note: String = "",
    val orderId: String = "",
    val itemName: String = "",
    val dateLabel: String = "",
    val guestLabel: String = "",
    val totalPriceText: String = ""
)
