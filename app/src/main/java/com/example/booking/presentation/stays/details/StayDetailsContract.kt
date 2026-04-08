package com.example.booking.presentation.stays.details

import android.content.Context

interface StayDetailsContract {
    interface View {
        fun showState(state: StayDetailsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class StayDetailsUiState(
    val hotelId: String? = null,
    val hotelName: String = "",
    val starRating: Int = 0,
    val reviewScoreText: String = "",
    val ratingText: String = "",
    val reviewText: String = "",
    val address: String = "",
    val locationText: String = "",
    val description: String = "",
    val highlightAmenities: List<String> = emptyList(),
    val photoAssetPaths: List<String?> = emptyList(),
    val checkInLabel: String = "",
    val checkOutLabel: String = "",
    val guestSummary: String = "",
    val nightsLabel: String = "",
    val roomPreviewText: String = "",
    val guestReviews: List<StayGuestReviewUiModel> = emptyList(),
    val priceText: String = ""
)

interface StayRoomTypeContract {
    interface View {
        fun showState(state: StayRoomTypeUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun selectRoom(roomId: String)
    }
}

data class StayRoomTypeUiState(
    val hotelName: String = "",
    val dateLabel: String = "",
    val guestSummary: String = "",
    val roomCountLabel: String = "",
    val roomCards: List<StayRoomCardUiModel> = emptyList()
)

data class StayRoomCardUiModel(
    val roomId: String,
    val title: String,
    val capacityText: String,
    val bedText: String,
    val description: String,
    val amenityText: String,
    val priceText: String,
    val taxesText: String,
    val availabilityText: String,
    val imageAssetPath: String? = null,
    val enabled: Boolean
)

data class StayGuestReviewUiModel(
    val reviewer: String,
    val scoreText: String,
    val title: String,
    val detail: String,
    val meta: String
)
