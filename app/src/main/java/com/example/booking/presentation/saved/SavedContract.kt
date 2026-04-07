package com.example.booking.presentation.saved

import android.content.Context

interface SavedContract {
    interface View {
        fun showState(state: SavedUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class SavedUiState(
    val groups: List<SavedGroupUiModel> = emptyList()
)

data class SavedGroupUiModel(
    val title: String,
    val savedItemCount: Int
)
