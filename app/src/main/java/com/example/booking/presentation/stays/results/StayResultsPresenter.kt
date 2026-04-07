package com.example.booking.presentation.stays.results

import android.content.Context
import com.example.booking.common.demo.DemoVisuals
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
        val hotelCards = expandHotelCards(
            context = context,
            hotels = filteredHotels,
            seed = draft.destinationQuery.ifBlank { "all_destinations" }
        )

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

    private fun expandHotelCards(
        context: Context,
        hotels: List<Hotel>,
        seed: String
    ): List<StayHotelCardUiModel> {
        if (hotels.isEmpty()) return emptyList()
        val targetCount = maxOf(8, hotels.size * 2)
        val imageAssignments = DemoVisuals.stayImageAssignments(context, seed, targetCount)
        return List(targetCount) { index ->
            hotelToCard(
                hotel = hotels[index % hotels.size],
                index = index,
                imageAssetPath = imageAssignments.getOrNull(index)
            )
        }
    }

    private fun hotelToCard(
        hotel: Hotel,
        index: Int,
        imageAssetPath: String?
    ): StayHotelCardUiModel {
        val variantKey = "${hotel.hotelId}_$index"
        return StayHotelCardUiModel(
            cardId = variantKey,
            hotelId = hotel.hotelId,
            name = hotel.name,
            city = hotel.city,
            country = hotel.country,
            starRating = hotel.starRating,
            ratingText = ratingLabel(hotel.rating),
            reviewCountText = "${hotel.reviewCount} reviews",
            locationText = buildLocationText(hotel, variantKey),
            highlightText = buildHighlightText(hotel, variantKey),
            amenityText = buildAmenityText(hotel, variantKey),
            policyText = buildPolicyText(variantKey),
            availabilityText = buildAvailabilityText(hotel, variantKey),
            priceText = BookingFormatters.formatCurrency(hotel.pricePerNight, hotel.currency),
            taxesText = buildTaxesText(variantKey),
            imageAssetPath = imageAssetPath
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

    private fun buildLocationText(
        hotel: Hotel,
        variantKey: String
    ): String {
        val options = listOf(
            "In downtown ${hotel.city}",
            "Near ${hotel.city} city center",
            "Close to top sights in ${hotel.city}",
            "Well placed for local transit"
        )
        return options[DemoVisuals.stableIndex("$variantKey:location", options.size)]
    }

    private fun buildHighlightText(
        hotel: Hotel,
        variantKey: String
    ): String {
        val options = listOf(
            hotel.description.take(80).trimEnd('.', ' ') + ".",
            "A popular pick for short stays with easy access around ${hotel.city}.",
            "Guests like the ${hotel.starRating}-star comfort and flexible city-break feel.",
            "A handy base for ${hotel.city} trips with a polished local stay setup."
        )
        return options[DemoVisuals.stableIndex("$variantKey:highlight", options.size)]
    }

    private fun buildAmenityText(
        hotel: Hotel,
        variantKey: String
    ): String {
        if (hotel.amenities.isEmpty()) {
            return "Flexible booking | Guest favorite"
        }
        val startIndex = DemoVisuals.stableIndex("$variantKey:amenity", hotel.amenities.size)
        val selectedAmenities = List(minOf(3, hotel.amenities.size)) { offset ->
            hotel.amenities[(startIndex + offset) % hotel.amenities.size]
        }
        return selectedAmenities.joinToString(" | ")
    }

    private fun buildPolicyText(variantKey: String): String {
        val options = listOf(
            "No prepayment needed",
            "Free cancellation available",
            "Breakfast included",
            "Reserve now, pay later"
        )
        return options[DemoVisuals.stableIndex("$variantKey:policy", options.size)]
    }

    private fun buildAvailabilityText(
        hotel: Hotel,
        variantKey: String
    ): String {
        val options = listOf(
            "Only ${hotel.reviewCount % 4 + 2} left at this price",
            "Booked ${hotel.reviewCount % 7 + 3} times in the last 24 hours",
            "Limited-time deal for this stay",
            "High demand for ${hotel.city} this week"
        )
        return options[DemoVisuals.stableIndex("$variantKey:availability", options.size)]
    }

    private fun buildTaxesText(variantKey: String): String {
        val options = listOf(
            "1 night | taxes and fees included",
            "1 night | taxes may apply",
            "1 night | includes local fees",
            "1 night | flexible booking rate"
        )
        return options[DemoVisuals.stableIndex("$variantKey:taxes", options.size)]
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
