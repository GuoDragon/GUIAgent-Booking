package com.example.booking.presentation.attractions.booking

import android.content.Context

interface AttractionPersonalInfoContract {
    interface View {
        fun showState(state: AttractionPersonalInfoUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun saveTraveler(name: String, email: String, phone: String)
    }
}

data class AttractionPersonalInfoUiState(
    val hasTicket: Boolean = false,
    val title: String = "",
    val travelerName: String = "",
    val travelerEmail: String = "",
    val travelerPhone: String = ""
)

interface AttractionPaymentContract {
    interface View {
        fun showState(state: AttractionPaymentUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun completeBooking(context: Context): String?
    }
}

data class AttractionPaymentUiState(
    val hasTicket: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val travelerName: String = "",
    val travelerEmail: String = "",
    val totalPriceText: String = "",
    val helperText: String = ""
)

interface AttractionPaymentSuccessContract {
    interface View {
        fun showState(state: AttractionPaymentSuccessUiState)
    }

    interface Presenter {
        fun loadData(context: Context, orderId: String)
    }
}

data class AttractionPaymentSuccessUiState(
    val hasOrder: Boolean = false,
    val title: String = "",
    val note: String = "",
    val orderId: String = "",
    val itemName: String = "",
    val dateLabel: String = "",
    val totalPriceText: String = ""
)
