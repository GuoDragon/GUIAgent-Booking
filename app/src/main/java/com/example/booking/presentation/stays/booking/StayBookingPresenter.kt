package com.example.booking.presentation.stays.booking

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.BookingSignal
import com.example.booking.model.Order
import com.example.booking.presentation.stays.common.StayDraftStore
import com.example.booking.presentation.stays.common.StayFlowMapper
import com.example.booking.presentation.stays.common.StayTripPurpose
import java.time.temporal.ChronoUnit
import java.util.UUID

class StayPersonalInfoPresenter(
    private val view: StayPersonalInfoContract.View
) : StayPersonalInfoContract.Presenter {

    override fun loadData(context: Context) {
        val draft = StayDraftStore.snapshot()
        val user = DataRepository.loadUsers(context).firstOrNull()
        val hotels = DataRepository.loadHotels(context)
        val rooms = DataRepository.loadHotelRooms(context)
        val hotel = StayFlowMapper.findHotel(hotels, draft.selectedHotelId)
        val room = StayFlowMapper.findRoom(rooms, draft.selectedRoomId)
        val phoneParts = BookingFormatters.parsePhoneParts(user?.phone)

        view.showState(
            StayPersonalInfoUiState(
                hotelName = hotel?.name.orEmpty(),
                roomType = room?.roomType.orEmpty(),
                priceText = room?.let {
                    BookingFormatters.formatCurrency(it.pricePerNight, it.currency)
                }.orEmpty(),
                firstName = draft.firstName.ifBlank { user?.firstName.orEmpty() },
                lastName = draft.lastName.ifBlank { user?.lastName.orEmpty() },
                email = draft.email.ifBlank { user?.email.orEmpty() },
                phoneCountryCode = if (draft.phoneNumber.isNotBlank()) draft.phoneCountryCode else phoneParts.first,
                phoneNumber = draft.phoneNumber.ifBlank { phoneParts.second },
                countryOrRegion = draft.countryOrRegion.ifBlank {
                    BookingFormatters.formatCountry(user?.nationality)
                },
                saveToAccount = draft.saveToAccount,
                tripPurpose = draft.tripPurpose
            )
        )
    }

    override fun saveDraft(
        firstName: String,
        lastName: String,
        email: String,
        phoneCountryCode: String,
        phoneNumber: String,
        countryOrRegion: String,
        saveToAccount: Boolean,
        tripPurpose: StayTripPurpose?
    ) {
        StayDraftStore.update { draft ->
            draft.copy(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                email = email.trim(),
                phoneCountryCode = phoneCountryCode.trim(),
                phoneNumber = phoneNumber.trim(),
                countryOrRegion = countryOrRegion.trim(),
                saveToAccount = saveToAccount,
                tripPurpose = tripPurpose
            )
        }
    }
}

class StayBookingOverviewPresenter(
    private val view: StayBookingOverviewContract.View
) : StayBookingOverviewContract.Presenter {

    override fun loadData(context: Context) {
        val draft = StayDraftStore.snapshot()
        val hotels = DataRepository.loadHotels(context)
        val rooms = DataRepository.loadHotelRooms(context)
        val hotel = StayFlowMapper.findHotel(hotels, draft.selectedHotelId)
        val room = StayFlowMapper.findRoom(rooms, draft.selectedRoomId)

        if (hotel == null || room == null) {
            view.showState(StayBookingOverviewUiState())
            return
        }

        view.showState(buildOverviewState(hotel = hotel, room = room, draft = draft))
    }

    override fun completeBooking(
        context: Context,
        interestedInCarRental: Boolean,
        specialRequest: String
    ): String? {
        val draft = StayDraftStore.snapshot()
        val user = DataRepository.loadUsers(context).firstOrNull()
        val hotels = DataRepository.loadHotels(context)
        val rooms = DataRepository.loadHotelRooms(context)
        val hotel = StayFlowMapper.findHotel(hotels, draft.selectedHotelId) ?: return null
        val room = StayFlowMapper.findRoom(rooms, draft.selectedRoomId) ?: return null

        StayDraftStore.update { currentDraft ->
            currentDraft.copy(
                interestedInCarRental = interestedInCarRental,
                specialRequest = specialRequest.trim()
            )
        }

        val now = System.currentTimeMillis()
        val orderId = "stay_${UUID.randomUUID()}"
        val nightCount = ChronoUnit.DAYS.between(draft.checkInDate, draft.checkOutDate).coerceAtLeast(1)
        val totalPrice = room.pricePerNight * draft.roomCount * nightCount
        val userId = user?.userId ?: "user001"
        val itemName = "${hotel.name} - ${room.roomType}"
        val startDate = BookingFormatters.localDateToEpochMillis(draft.checkInDate)
        val endDate = BookingFormatters.localDateToEpochMillis(draft.checkOutDate)

        DataRepository.appendOrder(
            context = context,
            order = Order(
                orderId = orderId,
                userId = userId,
                orderType = "STAY",
                status = "ACTIVE",
                itemId = hotel.hotelId,
                itemName = itemName,
                bookingDate = now,
                startDate = startDate,
                endDate = endDate,
                totalPrice = totalPrice,
                currency = room.currency,
                guestCount = draft.totalGuests
            )
        )
        DataRepository.appendBookingSignal(
            context = context,
            signal = BookingSignal(
                signalId = "stay_booking_${UUID.randomUUID()}",
                userId = userId,
                orderType = "STAY",
                itemId = hotel.hotelId,
                itemName = itemName,
                totalPrice = totalPrice,
                currency = room.currency,
                guestCount = draft.totalGuests,
                startDate = startDate,
                endDate = endDate,
                createdAt = now
            )
        )
        StayDraftStore.markBookingComplete(orderId)
        return orderId
    }

    private fun buildOverviewState(
        hotel: com.example.booking.model.Hotel,
        room: com.example.booking.model.HotelRoom,
        draft: com.example.booking.presentation.stays.common.StayDraft
    ): StayBookingOverviewUiState {
        val nightCount = ChronoUnit.DAYS.between(draft.checkInDate, draft.checkOutDate).coerceAtLeast(1)
        val subtotal = room.pricePerNight * draft.roomCount * nightCount
        val taxes = subtotal * 0.1
        return StayBookingOverviewUiState(
            hotelName = hotel.name,
            roomType = room.roomType,
            ratingText = "${hotel.rating} / 10",
            address = hotel.address,
            checkInLabel = BookingFormatters.formatLongLocalDate(draft.checkInDate),
            checkOutLabel = BookingFormatters.formatLongLocalDate(draft.checkOutDate),
            guestSummary = BookingFormatters.formatGuestSummary(
                rooms = draft.roomCount,
                adults = draft.adultCount,
                children = draft.childCount
            ),
            bookingForLabel = BookingFormatters.formatFullName(draft.firstName, draft.lastName),
            priceText = BookingFormatters.formatCurrency(subtotal, room.currency),
            taxesText = "+${BookingFormatters.formatCurrency(taxes, room.currency)} taxes and fees",
            priceInfoText = "Charged in ${room.currency} at the property. Price is based on ${nightCount} night(s).",
            conditions = listOf(
                "No prepayment is required in this demo flow.",
                "Cancellation details depend on the selected room type."
            ),
            benefits = listOf(
                "This booking counts toward Genius progress.",
                "Your local Trips page updates right after confirmation."
            ),
            interestedInCarRental = draft.interestedInCarRental,
            specialRequest = draft.specialRequest,
            canComplete = hotel.hotelId.isNotBlank() && room.roomId.isNotBlank()
        )
    }
}

class StayBookingSuccessPresenter(
    private val view: StayBookingSuccessContract.View
) : StayBookingSuccessContract.Presenter {

    override fun loadData(context: Context, orderId: String) {
        val order = DataRepository.loadOrderById(context, orderId)
        if (order == null) {
            view.showState(
                StayBookingSuccessUiState(
                    hasOrder = false,
                    title = "Booking not found",
                    note = "The booking was not found in the local order file."
                )
            )
            return
        }

        view.showState(
            StayBookingSuccessUiState(
                hasOrder = true,
                title = "Your stay is booked",
                orderId = order.orderId,
                itemName = order.itemName,
                dateLabel = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                guestLabel = "${order.guestCount} guest" + if (order.guestCount == 1) "" else "s",
                totalPrice = BookingFormatters.formatCurrency(order.totalPrice, order.currency),
                bookedOn = "Booked ${BookingFormatters.formatFullDate(order.bookingDate)}",
                note = "The order and booking signal were saved locally, and this stay now appears in Trips."
            )
        )
    }
}
