package com.example.booking.presentation.attractions.details

import android.content.Context

interface AttractionPreviewContract {
    interface View {
        fun showState(state: AttractionPreviewUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class AttractionPreviewUiState(
    val hasAttraction: Boolean = false,
    val title: String = "",
    val ratingText: String = "",
    val reviewText: String = "",
    val durationText: String = "",
    val cancelText: String = "",
    val dateLabels: List<String> = emptyList(),
    val priceText: String = ""
)

interface AttractionDetailsContract {
    interface View {
        fun showState(state: AttractionDetailsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class AttractionDetailsUiState(
    val hasAttraction: Boolean = false,
    val title: String = "",
    val locationText: String = "",
    val categoryText: String = "",
    val description: String = "",
    val highlights: List<String> = emptyList(),
    val priceText: String = ""
)

interface AttractionTicketsContract {
    interface View {
        fun showState(state: AttractionTicketsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class AttractionTicketsUiState(
    val hasAttraction: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val tickets: List<AttractionTicketCardUiModel> = emptyList()
)

data class AttractionTicketCardUiModel(
    val ticketId: String,
    val title: String,
    val description: String,
    val validityText: String,
    val cancelText: String,
    val priceText: String
)

interface AttractionTicketDetailContract {
    interface View {
        fun showState(state: AttractionTicketDetailUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class AttractionTicketDetailUiState(
    val hasTicket: Boolean = false,
    val title: String = "",
    val attractionTitle: String = "",
    val description: String = "",
    val validityText: String = "",
    val cancelText: String = "",
    val priceText: String = ""
)
