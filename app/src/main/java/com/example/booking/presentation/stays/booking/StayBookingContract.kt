package com.example.booking.presentation.stays.booking

import android.content.Context
import com.example.booking.presentation.stays.common.StayTripPurpose

interface StayPersonalInfoContract {
    interface View {
        fun showState(state: StayPersonalInfoUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun saveDraft(
            firstName: String,
            lastName: String,
            email: String,
            phoneCountryCode: String,
            phoneNumber: String,
            countryOrRegion: String,
            saveToAccount: Boolean,
            tripPurpose: StayTripPurpose?
        )
    }
}

data class StayPersonalInfoUiState(
    val hotelName: String = "",
    val roomType: String = "",
    val priceText: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phoneCountryCode: String = "+1",
    val phoneNumber: String = "",
    val countryOrRegion: String = "",
    val saveToAccount: Boolean = false,
    val tripPurpose: StayTripPurpose? = null
)

interface StayBookingOverviewContract {
    interface View {
        fun showState(state: StayBookingOverviewUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun completeBooking(
            context: Context,
            interestedInCarRental: Boolean,
            specialRequest: String
        ): String?
    }
}

data class StayBookingOverviewUiState(
    val hotelName: String = "",
    val roomType: String = "",
    val ratingText: String = "",
    val address: String = "",
    val checkInLabel: String = "",
    val checkOutLabel: String = "",
    val guestSummary: String = "",
    val bookingForLabel: String = "",
    val priceText: String = "",
    val taxesText: String = "",
    val priceInfoText: String = "",
    val conditions: List<String> = emptyList(),
    val benefits: List<String> = emptyList(),
    val interestedInCarRental: Boolean = false,
    val specialRequest: String = "",
    val canComplete: Boolean = false
)

interface StayBookingSuccessContract {
    interface View {
        fun showState(state: StayBookingSuccessUiState)
    }

    interface Presenter {
        fun loadData(context: Context, orderId: String)
    }
}

data class StayBookingSuccessUiState(
    val hasOrder: Boolean = false,
    val title: String = "",
    val orderId: String = "",
    val itemName: String = "",
    val dateLabel: String = "",
    val guestLabel: String = "",
    val totalPrice: String = "",
    val bookedOn: String = "",
    val note: String = ""
)
