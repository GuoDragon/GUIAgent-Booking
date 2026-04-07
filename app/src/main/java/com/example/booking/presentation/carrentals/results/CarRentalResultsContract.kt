package com.example.booking.presentation.carrentals.results

import android.content.Context
import com.example.booking.presentation.carrentals.common.CarRentalFilterState
import com.example.booking.presentation.carrentals.common.CarRentalSortOption

interface CarRentalResultsContract {
    interface View {
        fun showState(state: CarRentalResultsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun recordMapOpened(context: Context)
    }
}

data class CarRentalResultsUiState(
    val headerTitle: String = "",
    val headerSubtitle: String = "",
    val resultsCount: Int = 0,
    val cards: List<CarRentalCardUiModel> = emptyList(),
    val hasActiveFilters: Boolean = false
)

data class CarRentalCardUiModel(
    val cardId: String,
    val carId: String,
    val title: String,
    val detailLine: String,
    val transmissionLine: String,
    val locationLine: String,
    val companyName: String,
    val ratingText: String,
    val reviewText: String,
    val priceText: String,
    val originalPriceText: String,
    val tagLabels: List<String>
)

interface CarRentalSortContract {
    interface View {
        fun showState(state: CarRentalSortUiState)
    }

    interface Presenter {
        fun loadData()
        fun applySort(option: CarRentalSortOption)
    }
}

data class CarRentalSortUiState(
    val selectedOption: CarRentalSortOption,
    val options: List<CarRentalSortOption>
)

interface CarRentalFilterContract {
    interface View {
        fun showState(state: CarRentalFilterUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applyFilter(filterState: CarRentalFilterState)
    }
}

data class CarRentalFilterUiState(
    val locationOptions: List<String> = emptyList(),
    val categoryOptions: List<String> = emptyList(),
    val maxPricePerDay: Double = 0.0,
    val currentFilter: CarRentalFilterState = CarRentalFilterState()
)

interface CarRentalDetailsContract {
    interface View {
        fun showState(state: CarRentalDetailsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class CarRentalDetailsUiState(
    val title: String = "",
    val subtitle: String = "",
    val location: String = "",
    val companyName: String = "",
    val ratingText: String = "",
    val reviewText: String = "",
    val featureLines: List<String> = emptyList(),
    val includedLines: List<String> = emptyList(),
    val priceText: String = "",
    val totalLabel: String = "",
    val canContinue: Boolean = false
)
