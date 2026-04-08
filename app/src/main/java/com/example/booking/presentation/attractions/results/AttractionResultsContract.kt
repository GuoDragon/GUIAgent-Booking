package com.example.booking.presentation.attractions.results

import android.content.Context

interface AttractionResultsContract {
    interface View {
        fun showState(state: AttractionResultsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun selectAttraction(context: Context, attractionId: String)
    }
}

data class AttractionResultsUiState(
    val headerTitle: String = "",
    val headerSubtitle: String = "",
    val keywordLabel: String = "",
    val cards: List<AttractionResultCardUiModel> = emptyList()
)

data class AttractionResultCardUiModel(
    val attractionId: String,
    val imageAssetPath: String?,
    val title: String,
    val cityLabel: String,
    val ratingText: String,
    val reviewText: String,
    val durationText: String,
    val priceText: String,
    val availabilityText: String,
    val badges: List<String>
)
