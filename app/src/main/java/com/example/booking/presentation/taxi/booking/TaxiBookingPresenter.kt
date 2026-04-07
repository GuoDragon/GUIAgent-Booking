package com.example.booking.presentation.taxi.booking

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.BookingSignal
import com.example.booking.model.Order
import com.example.booking.presentation.taxi.common.TaxiDraftStore
import com.example.booking.presentation.taxi.common.TaxiTripType
import java.util.UUID

class TaxiContactPresenter(
    private val view: TaxiContactContract.View
) : TaxiContactContract.Presenter {

    override fun loadData() {
        val draft = TaxiDraftStore.snapshot()
        view.showState(
            TaxiContactUiState(
                hasSelection = draft.selectedRouteId != null,
                pickupLine = draft.pickupLocation,
                destinationLine = draft.destination,
                contactName = draft.contactName,
                contactEmail = draft.contactEmail,
                contactPhone = draft.contactPhone,
                flightNumber = draft.flightNumber
            )
        )
    }

    override fun saveContact(name: String, email: String, phone: String, flightNumber: String) {
        TaxiDraftStore.update { draft ->
            draft.copy(
                contactName = name,
                contactEmail = email,
                contactPhone = phone,
                flightNumber = flightNumber
            )
        }
    }
}

class TaxiOverviewPresenter(
    private val view: TaxiOverviewContract.View
) : TaxiOverviewContract.Presenter {

    override fun loadData(context: Context) {
        val draft = TaxiDraftStore.snapshot()
        val route = DataRepository.loadTaxiRoutes(context).firstOrNull { it.routeId == draft.selectedRouteId }
        if (route == null) {
            view.showState(TaxiOverviewUiState())
            return
        }
        view.showState(
            TaxiOverviewUiState(
                hasSelection = true,
                routeTitle = route.vehicleType,
                pickupLine = "${BookingFormatters.formatLocalDateTime(draft.pickupDateTime)}\n${draft.pickupLocation}",
                destinationLine = buildString {
                    append(draft.destination)
                    if (draft.tripType == TaxiTripType.RoundTrip) {
                        append("\nReturn: ${BookingFormatters.formatLocalDateTime(draft.returnDateTime)}")
                    }
                },
                flightNumber = draft.flightNumber,
                passengerLine = "${draft.passengerCount} passenger" + if (draft.passengerCount == 1) "" else "s",
                totalPriceText = BookingFormatters.formatCurrency(route.price, route.currency),
                helperText = "The taxi order and signal will be saved locally once you confirm."
            )
        )
    }

    override fun completeBooking(context: Context): String? {
        val draft = TaxiDraftStore.snapshot()
        val route = DataRepository.loadTaxiRoutes(context).firstOrNull { it.routeId == draft.selectedRouteId } ?: return null
        val userId = DataRepository.loadUsers(context).firstOrNull()?.userId ?: "user001"
        val orderId = "taxi_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()
        val itemName = "${route.vehicleType} taxi to ${route.destination}"
        val startDate = BookingFormatters.localDateTimeToEpochMillis(draft.pickupDateTime)
        val endDate = if (draft.tripType == TaxiTripType.RoundTrip) {
            BookingFormatters.localDateTimeToEpochMillis(draft.returnDateTime)
        } else {
            null
        }

        DataRepository.appendOrder(
            context = context,
            order = Order(
                orderId = orderId,
                userId = userId,
                orderType = "TAXI",
                status = "ACTIVE",
                itemId = route.routeId,
                itemName = itemName,
                bookingDate = now,
                startDate = startDate,
                endDate = endDate,
                totalPrice = route.price,
                currency = route.currency,
                guestCount = draft.passengerCount
            )
        )
        DataRepository.appendBookingSignal(
            context = context,
            signal = BookingSignal(
                signalId = "taxi_booking_${UUID.randomUUID()}",
                userId = userId,
                orderType = "TAXI",
                itemId = route.routeId,
                itemName = itemName,
                totalPrice = route.price,
                currency = route.currency,
                guestCount = draft.passengerCount,
                startDate = startDate,
                endDate = endDate,
                createdAt = now
            )
        )
        TaxiDraftStore.markBookingComplete(orderId)
        return orderId
    }
}

class TaxiBookingSuccessPresenter(
    private val view: TaxiBookingSuccessContract.View
) : TaxiBookingSuccessContract.Presenter {

    override fun loadData(context: Context, orderId: String) {
        val order = DataRepository.loadOrderById(context, orderId)
        if (order == null) {
            view.showState(
                TaxiBookingSuccessUiState(
                    title = "Booking not found",
                    note = "The taxi booking is missing from the local runtime order file."
                )
            )
            return
        }
        view.showState(
            TaxiBookingSuccessUiState(
                hasOrder = true,
                title = "Your airport taxi is booked",
                note = "The taxi order and booking signal were saved locally and now appear in Trips.",
                orderId = order.orderId,
                itemName = order.itemName,
                dateLabel = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                guestLabel = "${order.guestCount} passenger" + if (order.guestCount == 1) "" else "s",
                totalPriceText = BookingFormatters.formatCurrency(order.totalPrice, order.currency)
            )
        )
    }
}
