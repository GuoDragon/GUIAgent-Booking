package com.example.booking.presentation.stays.details

import android.content.Context
import com.example.booking.common.demo.DemoVisuals
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.Hotel
import com.example.booking.model.HotelRoom
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.stays.common.StayFlowMapper
import java.time.temporal.ChronoUnit
import kotlin.math.ceil

class StayDetailsPresenter(
    private val view: StayDetailsContract.View
) : StayDetailsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = StayDraftStore.snapshot()
        val hotels = DataRepository.loadHotels(context)
        val rooms = DataRepository.loadHotelRooms(context)
        val hotel = StayFlowMapper.findHotel(hotels, draft.selectedHotelId)

        if (hotel == null) {
            view.showState(StayDetailsUiState())
            return
        }

        val hotelRooms = StayFlowMapper.roomsForHotel(rooms, hotel.hotelId)
        val cheapestRoom = hotelRooms.minByOrNull { it.pricePerNight }
        val photoAssetPaths = DemoVisuals.stayImageAssignments(
            context = context,
            seed = hotel.hotelId,
            cardCount = 6
        )

        view.showState(
            StayDetailsUiState(
                hotelId = hotel.hotelId,
                hotelName = hotel.name,
                starRating = hotel.starRating,
                reviewScoreText = String.format("%.1f", hotel.rating),
                ratingText = ratingLabel(hotel.rating),
                reviewText = "${hotel.reviewCount} reviews",
                address = hotel.address,
                locationText = ratingLabel(hotel.rating),
                description = hotel.description,
                highlightAmenities = hotel.amenities.take(6),
                photoAssetPaths = photoAssetPaths,
                checkInLabel = BookingFormatters.formatLongLocalDate(draft.checkInDate),
                checkOutLabel = BookingFormatters.formatLongLocalDate(draft.checkOutDate),
                guestSummary = BookingFormatters.formatGuestSummary(
                    rooms = draft.roomCount,
                    adults = draft.adultCount,
                    children = draft.childCount
                ),
                nightsLabel = BookingFormatters.formatNightCount(draft.checkInDate, draft.checkOutDate),
                roomPreviewText = roomPreviewText(hotelRooms.size),
                guestReviews = buildGuestReviews(hotel),
                priceText = cheapestRoom?.let {
                    "From ${BookingFormatters.formatCurrency(it.pricePerNight, it.currency)}"
                } ?: "Rooms unavailable"
            )
        )
    }

    private fun roomPreviewText(roomCount: Int): String {
        return when {
            roomCount <= 0 -> "No rooms available in local data"
            roomCount == 1 -> "1 room available in local data"
            else -> "$roomCount room types available"
        }
    }

    private fun buildGuestReviews(hotel: Hotel): List<StayGuestReviewUiModel> {
        val reviewerNames = listOf(
            "Olivia", "Noah", "Emma", "Liam", "Ava", "Mason", "Sophia", "Ethan", "Isabella", "Lucas",
            "Mia", "James", "Charlotte", "Henry", "Amelia", "Benjamin", "Harper", "Logan"
        )
        val titles = listOf(
            "Great location and smooth check-in",
            "Comfortable room for a short city break",
            "Solid choice for the price",
            "Would stay again for this area",
            "Clean, practical and close to transport",
            "Good value with friendly staff"
        )
        val details = listOf(
            "The room was tidy and quiet at night. Walking to nearby spots was easy and the front desk was efficient.",
            "Good mattress, clean bathroom and clear instructions for late arrival. It covered everything needed for this trip.",
            "The property feels straightforward and reliable. Public transport and food options are close by.",
            "Check-in was quick, Wi-Fi stable, and common areas were well maintained during the stay.",
            "Everything matched the listing and the stay felt predictable. Good pick for a packed schedule."
        )
        val metaLabels = listOf(
            "Solo traveler",
            "Couple",
            "Family stay",
            "Business trip",
            "Weekend break"
        )

        return List(4) { index ->
            val key = "${hotel.hotelId}:review:$index"
            val reviewer = reviewerNames[DemoVisuals.stableIndex("$key:name", reviewerNames.size)]
            val title = titles[DemoVisuals.stableIndex("$key:title", titles.size)]
            val detail = details[DemoVisuals.stableIndex("$key:detail", details.size)]
            val meta = metaLabels[DemoVisuals.stableIndex("$key:meta", metaLabels.size)]
            val score = DemoVisuals.stableIndex("$key:score", 19) + 80
            StayGuestReviewUiModel(
                reviewer = reviewer,
                scoreText = String.format("%.1f", score / 10.0),
                title = title,
                detail = detail,
                meta = "$meta \u00b7 ${hotel.city}"
            )
        }
    }

    private fun ratingLabel(rating: Double): String {
        return when {
            rating >= 9.0 -> "Excellent location!"
            rating >= 8.0 -> "Great location"
            else -> "Good location"
        }
    }
}

class StayRoomTypePresenter(
    private val view: StayRoomTypeContract.View
) : StayRoomTypeContract.Presenter {

    override fun loadData(context: Context) {
        val draft = StayDraftStore.snapshot()
        val hotels = DataRepository.loadHotels(context)
        val rooms = DataRepository.loadHotelRooms(context)
        val hotel = StayFlowMapper.findHotel(hotels, draft.selectedHotelId)

        if (hotel == null) {
            view.showState(StayRoomTypeUiState())
            return
        }

        val requiredGuestsPerRoom = ceil(draft.totalGuests.toDouble() / draft.roomCount.toDouble()).toInt()
            .coerceAtLeast(1)
        val nightCount = ChronoUnit.DAYS.between(draft.checkInDate, draft.checkOutDate).coerceAtLeast(1)
        val roomImageAssignments = DemoVisuals.stayImageAssignments(
            context = context,
            seed = hotel.hotelId,
            cardCount = 6
        )

        val roomCards = StayFlowMapper.roomsForHotel(rooms, hotel.hotelId)
            .sortedBy { it.pricePerNight }
            .mapIndexed { index, room ->
                roomToCard(
                    room = room,
                    roomCount = draft.roomCount,
                    nights = nightCount.toInt(),
                    requiredGuestsPerRoom = requiredGuestsPerRoom,
                    index = index,
                    imageAssetPath = roomImageAssignments.getOrNull(
                        if (roomImageAssignments.isEmpty()) 0 else index % roomImageAssignments.size
                    )
                )
            }

        view.showState(
            StayRoomTypeUiState(
                hotelName = hotel.name,
                dateLabel = BookingFormatters.formatStayDateRange(draft.checkInDate, draft.checkOutDate),
                guestSummary = BookingFormatters.formatGuestSummary(
                    rooms = draft.roomCount,
                    adults = draft.adultCount,
                    children = draft.childCount
                ),
                roomCountLabel = BookingFormatters.formatNightCount(draft.checkInDate, draft.checkOutDate),
                roomCards = roomCards
            )
        )
    }

    override fun selectRoom(roomId: String) {
        StayDraftStore.selectRoom(roomId)
    }

    private fun roomToCard(
        room: HotelRoom,
        roomCount: Int,
        nights: Int,
        requiredGuestsPerRoom: Int,
        index: Int,
        imageAssetPath: String?
    ): StayRoomCardUiModel {
        val enabled = room.available && room.maxGuests >= requiredGuestsPerRoom
        val totalRoomPrice = room.pricePerNight * roomCount
        return StayRoomCardUiModel(
            roomId = room.roomId,
            title = room.roomType,
            capacityText = "Sleeps up to ${room.maxGuests} guests",
            bedText = "${room.bedType} bed",
            description = room.description,
            amenityText = room.amenities.take(4).joinToString(" | "),
            priceText = BookingFormatters.formatCurrency(totalRoomPrice, room.currency),
            taxesText = "For $roomCount room(s) per night | $nights night trip",
            availabilityText = if (enabled) {
                "Only ${index + 2} left at this price"
            } else {
                "Not enough space for your current party"
            },
            imageAssetPath = imageAssetPath,
            enabled = enabled
        )
    }
}

