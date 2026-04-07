package com.example.booking.presentation.flights.results

import android.content.Context
import com.example.booking.presentation.flights.common.FlightFilterState
import com.example.booking.presentation.flights.common.FlightSortOption

interface FlightResultsContract {
    interface View {
        fun showState(state: FlightResultsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun recordMapOpened(context: Context)
    }
}

data class FlightResultsUiState(
    val routeLabel: String = "",
    val tripLabel: String = "",
    val resultsCount: Int = 0,
    val cards: List<FlightResultCardUiModel> = emptyList(),
    val hasActiveFilters: Boolean = false
)

data class FlightResultCardUiModel(
    val cardId: String,
    val outboundFlightId: String?,
    val returnFlightId: String?,
    val airlineLabel: String,
    val supportingText: String,
    val outboundTimeLabel: String,
    val outboundMetaLabel: String,
    val returnTimeLabel: String,
    val returnMetaLabel: String,
    val priceText: String,
    val badgeText: String,
    val stopsLabel: String,
    val durationLabel: String
)

interface FlightSortContract {
    interface View {
        fun showState(state: FlightSortUiState)
    }

    interface Presenter {
        fun loadData()
        fun applySort(option: FlightSortOption)
    }
}

data class FlightSortUiState(
    val selectedOption: FlightSortOption,
    val options: List<FlightSortOption>
)

interface FlightFilterContract {
    interface View {
        fun showState(state: FlightFilterUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applyFilter(filterState: FlightFilterState)
    }
}

data class FlightFilterUiState(
    val airlines: List<FlightAirlineOptionUiModel> = emptyList(),
    val stopOptions: List<Int> = listOf(0, 1),
    val currentFilter: FlightFilterState = FlightFilterState()
)

data class FlightAirlineOptionUiModel(
    val airlineId: String,
    val name: String
)

interface FlightDetailsContract {
    interface View {
        fun showState(state: FlightDetailsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class FlightDetailsUiState(
    val title: String = "",
    val subtitle: String = "",
    val priceText: String = "",
    val totalLabel: String = "",
    val segments: List<FlightDetailSegmentUiModel> = emptyList(),
    val canContinue: Boolean = false
)

data class FlightDetailSegmentUiModel(
    val header: String,
    val points: List<FlightDetailPointUiModel>
)

data class FlightDetailPointUiModel(
    val timeLabel: String,
    val airportLabel: String,
    val airportMeta: String,
    val supportingText: String
)
