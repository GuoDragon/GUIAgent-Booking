package com.example.booking.presentation.stays.results

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.Hotel
import com.example.booking.model.SearchSignal
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.stays.common.StayFilterState
import com.example.booking.presentation.stays.common.StayFlowMapper
import com.example.booking.presentation.stays.common.StaySortOption
import java.util.UUID

class StayResultsPresenter(
    private val view: StayResultsContract.View
) : StayResultsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = StayDraftStore.snapshot()
        val hotels = DataRepository.loadHotels(context)
        val filteredHotels = StayFlowMapper.sortHotels(
            hotels = StayFlowMapper.filterHotels(hotels, draft),
            sortOption = draft.sortOption
        )
        val hotelCards = expandHotelCards(filteredHotels)

        view.showState(
            StayResultsUiState(
                destinationLabel = draft.destinationQuery.ifBlank { "All destinations" },
                dateLabel = BookingFormatters.formatStayDateRange(draft.checkInDate, draft.checkOutDate),
                guestLabel = BookingFormatters.formatGuestSummary(
                    rooms = draft.roomCount,
                    adults = draft.adultCount,
                    children = draft.childCount
                ),
                propertyCount = hotelCards.size,
                hotelCards = hotelCards,
                hasActiveFilters = draft.filterState != StayFilterState()
            )
        )
    }

    override fun recordMapOpened(context: Context) {
        val draft = StayDraftStore.snapshot()
        DataRepository.appendSearchSignal(
            context = context,
            signal = SearchSignal(
                signalId = "stay_map_${UUID.randomUUID()}",
                searchType = "STAY_MAP_OPENED",
                destination = draft.destinationQuery.ifBlank { "All destinations" },
                checkInDate = BookingFormatters.localDateToEpochMillis(draft.checkInDate),
                checkOutDate = BookingFormatters.localDateToEpochMillis(draft.checkOutDate),
                guestCount = draft.totalGuests,
                occurredAt = System.currentTimeMillis()
            )
        )
    }

    private fun expandHotelCards(hotels: List<Hotel>): List<StayHotelCardUiModel> {
        if (hotels.isEmpty()) return emptyList()
        val targetCount = maxOf(8, hotels.size * 2)
        return List(targetCount) { index ->
            hotelToCard(hotels[index % hotels.size], index)
        }
    }

    private fun hotelToCard(hotel: Hotel, index: Int): StayHotelCardUiModel {
        return StayHotelCardUiModel(
            cardId = "${hotel.hotelId}_$index",
            hotelId = hotel.hotelId,
            name = hotel.name,
            city = hotel.city,
            country = hotel.country,
            starRating = hotel.starRating,
            ratingText = ratingLabel(hotel.rating),
            reviewCountText = "${hotel.reviewCount} reviews",
            locationText = hotel.address,
            highlightText = hotel.description.take(88).trimEnd('.') + ".",
            amenityText = hotel.amenities.take(3).joinToString(" | "),
            priceText = BookingFormatters.formatCurrency(hotel.pricePerNight, hotel.currency),
            taxesText = "1 night | taxes may apply"
        )
    }

    private fun ratingLabel(rating: Double): String {
        return when {
            rating >= 9.0 -> "Excellent"
            rating >= 8.0 -> "Very Good"
            rating >= 7.0 -> "Good"
            else -> "Review score"
        }
    }
}

class StaySortPresenter(
    private val view: StaySortContract.View
) : StaySortContract.Presenter {

    override fun loadData() {
        val draft = StayDraftStore.snapshot()
        view.showState(
            StaySortUiState(
                selectedOption = draft.sortOption,
                options = StaySortOption.entries
            )
        )
    }

    override fun applySort(option: StaySortOption) {
        StayDraftStore.update { draft ->
            draft.copy(sortOption = option)
        }
    }
}

class StayFilterPresenter(
    private val view: StayFilterContract.View
) : StayFilterContract.Presenter {

    override fun loadData(context: Context) {
        val hotels = DataRepository.loadHotels(context)
        val draft = StayDraftStore.snapshot()

        view.showState(
            StayFilterUiState(
                hotels = hotels.map { hotel ->
                    StayFilterHotelSeed(
                        pricePerNight = hotel.pricePerNight,
                        starRating = hotel.starRating,
                        rating = hotel.rating,
                        amenities = hotel.amenities,
                        brand = hotel.name
                    )
                },
                minimumBudget = hotels.minOfOrNull { it.pricePerNight }?.toFloat() ?: 0f,
                maximumBudget = hotels.maxOfOrNull { it.pricePerNight }?.toFloat() ?: 0f,
                currentFilter = draft.filterState,
                amenityOptions = StayFlowMapper.hotelAmenities(hotels).take(8),
                brandOptions = StayFlowMapper.hotelBrands(hotels).take(6)
            )
        )
    }

    override fun applyFilter(filterState: StayFilterState) {
        StayDraftStore.update { draft ->
            draft.copy(filterState = filterState)
        }
    }
}
