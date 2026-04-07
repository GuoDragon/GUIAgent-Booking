package com.example.booking.presentation.stays.details

import android.content.Context
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

        view.showState(
            StayDetailsUiState(
                hotelId = hotel.hotelId,
                hotelName = hotel.name,
                starRating = hotel.starRating,
                ratingText = ratingLabel(hotel.rating),
                reviewText = "${hotel.rating} | ${hotel.reviewCount} reviews",
                address = hotel.address,
                locationText = "${hotel.city}, ${hotel.country}",
                description = hotel.description,
                highlightAmenities = hotel.amenities.take(6),
                photoLabels = buildPhotoLabels(hotel),
                checkInLabel = BookingFormatters.formatLongLocalDate(draft.checkInDate),
                checkOutLabel = BookingFormatters.formatLongLocalDate(draft.checkOutDate),
                guestSummary = BookingFormatters.formatGuestSummary(
                    rooms = draft.roomCount,
                    adults = draft.adultCount,
                    children = draft.childCount
                ),
                nightsLabel = BookingFormatters.formatNightCount(draft.checkInDate, draft.checkOutDate),
                roomPreviewText = roomPreviewText(hotelRooms.size),
                priceText = cheapestRoom?.let {
                    "From ${BookingFormatters.formatCurrency(it.pricePerNight, it.currency)}"
                } ?: "Rooms unavailable"
            )
        )
    }

    private fun buildPhotoLabels(hotel: Hotel): List<String> {
        return listOf(
            hotel.city,
            hotel.name,
            hotel.country,
            hotel.name.take(1),
            hotel.city.take(1),
            "+${hotel.reviewCount % 60 + 12}"
        )
    }

    private fun roomPreviewText(roomCount: Int): String {
        return when {
            roomCount <= 0 -> "No rooms available in local data"
            roomCount == 1 -> "1 room available in local data"
            else -> "$roomCount room types available"
        }
    }

    private fun ratingLabel(rating: Double): String {
        return when {
            rating >= 9.0 -> "Excellent location"
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

        val roomCards = StayFlowMapper.roomsForHotel(rooms, hotel.hotelId)
            .sortedBy { it.pricePerNight }
            .mapIndexed { index, room ->
                roomToCard(
                    room = room,
                    roomCount = draft.roomCount,
                    nights = nightCount.toInt(),
                    requiredGuestsPerRoom = requiredGuestsPerRoom,
                    index = index
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

    private fun roomToCard(
        room: HotelRoom,
        roomCount: Int,
        nights: Int,
        requiredGuestsPerRoom: Int,
        index: Int
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
            enabled = enabled
        )
    }
}

