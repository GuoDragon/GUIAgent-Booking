package com.example.booking.presentation.attractions.booking

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.BookingSignal
import com.example.booking.model.Order
import com.example.booking.presentation.attractions.common.AttractionDraftStore
import java.util.UUID

class AttractionPersonalInfoPresenter(
    private val view: AttractionPersonalInfoContract.View
) : AttractionPersonalInfoContract.Presenter {

    override fun loadData(context: Context) {
        val draft = AttractionDraftStore.snapshot()
        val ticket = DataRepository.loadAttractionTickets(context).firstOrNull { it.ticketId == draft.selectedTicketId }
        view.showState(
            AttractionPersonalInfoUiState(
                hasTicket = ticket != null,
                title = ticket?.ticketType ?: "",
                travelerName = draft.travelerName,
                travelerEmail = draft.travelerEmail,
                travelerPhone = draft.travelerPhone
            )
        )
    }

    override fun saveTraveler(name: String, email: String, phone: String) {
        AttractionDraftStore.update { draft ->
            draft.copy(travelerName = name, travelerEmail = email, travelerPhone = phone)
        }
    }
}

class AttractionPaymentPresenter(
    private val view: AttractionPaymentContract.View
) : AttractionPaymentContract.Presenter {

    override fun loadData(context: Context) {
        val draft = AttractionDraftStore.snapshot()
        val attraction = DataRepository.loadAttractions(context).firstOrNull { it.attractionId == draft.selectedAttractionId }
        val ticket = DataRepository.loadAttractionTickets(context).firstOrNull { it.ticketId == draft.selectedTicketId }
        if (attraction == null || ticket == null) {
            view.showState(AttractionPaymentUiState())
            return
        }
        view.showState(
            AttractionPaymentUiState(
                hasTicket = true,
                title = attraction.name,
                subtitle = ticket.ticketType,
                travelerName = draft.travelerName,
                travelerEmail = draft.travelerEmail,
                totalPriceText = BookingFormatters.formatCurrency(ticket.price, ticket.currency),
                helperText = "The attraction order and booking signal will be saved locally after payment."
            )
        )
    }

    override fun completeBooking(context: Context): String? {
        val draft = AttractionDraftStore.snapshot()
        val attraction = DataRepository.loadAttractions(context).firstOrNull { it.attractionId == draft.selectedAttractionId } ?: return null
        val ticket = DataRepository.loadAttractionTickets(context).firstOrNull { it.ticketId == draft.selectedTicketId } ?: return null
        val userId = DataRepository.loadUsers(context).firstOrNull()?.userId ?: "user001"
        val orderId = "attraction_${UUID.randomUUID()}"
        val now = System.currentTimeMillis()
        val startDate = BookingFormatters.localDateToEpochMillis(draft.selectedDate)
        val itemName = "${attraction.name} - ${ticket.ticketType}"

        DataRepository.appendOrder(
            context = context,
            order = Order(
                orderId = orderId,
                userId = userId,
                orderType = "ATTRACTION",
                status = "ACTIVE",
                itemId = ticket.ticketId,
                itemName = itemName,
                bookingDate = now,
                startDate = startDate,
                endDate = null,
                totalPrice = ticket.price,
                currency = ticket.currency,
                guestCount = 1
            )
        )
        DataRepository.appendBookingSignal(
            context = context,
            signal = BookingSignal(
                signalId = "attraction_booking_${UUID.randomUUID()}",
                userId = userId,
                orderType = "ATTRACTION",
                itemId = ticket.ticketId,
                itemName = itemName,
                totalPrice = ticket.price,
                currency = ticket.currency,
                guestCount = 1,
                startDate = startDate,
                endDate = null,
                createdAt = now
            )
        )
        AttractionDraftStore.markBookingComplete(orderId)
        return orderId
    }
}

class AttractionPaymentSuccessPresenter(
    private val view: AttractionPaymentSuccessContract.View
) : AttractionPaymentSuccessContract.Presenter {

    override fun loadData(context: Context, orderId: String) {
        val order = DataRepository.loadOrderById(context, orderId)
        if (order == null) {
            view.showState(
                AttractionPaymentSuccessUiState(
                    title = "Booking not found",
                    note = "The attraction booking is missing from the local runtime order file."
                )
            )
            return
        }
        view.showState(
            AttractionPaymentSuccessUiState(
                hasOrder = true,
                title = "Your attraction is booked",
                note = "The attraction order and booking signal were saved locally and now appear in Trips.",
                orderId = order.orderId,
                itemName = order.itemName,
                dateLabel = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                totalPriceText = BookingFormatters.formatCurrency(order.totalPrice, order.currency)
            )
        )
    }
}
