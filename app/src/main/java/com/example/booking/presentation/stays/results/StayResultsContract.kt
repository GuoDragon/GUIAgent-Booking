package com.example.booking.presentation.stays.results

import android.content.Context
import com.example.booking.presentation.stays.common.StayFilterState
import com.example.booking.presentation.stays.common.StaySortOption

interface StayResultsContract {
    interface View {
        fun showState(state: StayResultsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun recordMapOpened(context: Context)
        fun selectHotel(context: Context, hotelId: String)
    }
}

data class StayResultsUiState(
    val destinationLabel: String,
    val dateLabel: String,
    val guestLabel: String,
    val propertyCount: Int,
    val hotelCards: List<StayHotelCardUiModel> = emptyList(),
    val hasActiveFilters: Boolean = false
)

data class StayHotelCardUiModel(
    val cardId: String,
    val hotelId: String,
    val name: String,
    val city: String,
    val country: String,
    val starRating: Int,
    val reviewScoreText: String,
    val ratingText: String,
    val reviewCountText: String,
    val locationText: String,
    val highlightText: String,
    val amenityText: String,
    val policyText: String,
    val availabilityText: String,
    val priceText: String,
    val taxesText: String,
    val imageAssetPath: String? = null
)

interface StaySortContract {
    interface View {
        fun showState(state: StaySortUiState)
    }

    interface Presenter {
        fun loadData()
        fun applySort(option: StaySortOption)
    }
}

data class StaySortUiState(
    val selectedOption: StaySortOption,
    val options: List<StaySortOption>
)

interface StayFilterContract {
    interface View {
        fun showState(state: StayFilterUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applyFilter(filterState: StayFilterState)
    }
}

data class StayFilterUiState(
    val hotels: List<StayFilterHotelSeed> = emptyList(),
    val minimumBudget: Float = 0f,
    val maximumBudget: Float = 0f,
    val currentFilter: StayFilterState = StayFilterState(),
    val amenityOptions: List<String> = emptyList(),
    val brandOptions: List<String> = emptyList()
)

data class StayFilterHotelSeed(
    val pricePerNight: Double,
    val starRating: Int,
    val rating: Double,
    val amenities: List<String>,
    val brand: String
)
