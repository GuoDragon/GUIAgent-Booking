package com.example.booking.presentation.attractions.results

import android.content.Context
import com.example.booking.common.demo.DemoVisuals
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.Attraction
import com.example.booking.presentation.attractions.common.AttractionDraftStore
import com.example.booking.presentation.attractions.common.AttractionFlowMapper

class AttractionResultsPresenter(
    private val view: AttractionResultsContract.View
) : AttractionResultsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = AttractionDraftStore.snapshot()
        val attractions = AttractionFlowMapper.sortAttractions(
            AttractionFlowMapper.filterAttractions(DataRepository.loadAttractions(context), draft)
        )

        view.showState(
            AttractionResultsUiState(
                headerTitle = draft.destinationQuery,
                headerSubtitle = BookingFormatters.formatLongLocalDate(draft.selectedDate),
                keywordLabel = "Filter by keyword",
                cards = attractions.mapIndexed { index, attraction ->
                    attractionToCard(attraction, index)
                }
            )
        )
    }

    private fun attractionToCard(
        attraction: Attraction,
        index: Int
    ): AttractionResultCardUiModel {
        val variantKey = "${attraction.attractionId}_$index"
        val durationOptions = listOf("45 min", "1 hour", "2 hours", "Half day")
        return AttractionResultCardUiModel(
            attractionId = attraction.attractionId,
            title = attraction.name,
            cityLabel = "${attraction.city}, ${attraction.country}",
            ratingText = String.format("%.1f", attraction.rating),
            reviewText = "${attraction.reviewCount} reviews",
            durationText = durationOptions[DemoVisuals.stableIndex("$variantKey:duration", durationOptions.size)],
            priceText = "From ${BookingFormatters.formatCurrency(attraction.fromPrice, attraction.currency)}",
            availabilityText = "Available starting today",
            badges = buildList {
                add(if (index % 2 == 0) "Best seller" else "Genius")
                if (index % 3 == 0) add("10% off")
            }
        )
    }
}
