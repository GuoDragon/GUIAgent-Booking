package com.example.booking.presentation.orders

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.HotelReviewSignal
import com.example.booking.model.Order
import java.util.UUID

class OrdersPresenter(
    private val view: OrdersContract.View
) : OrdersContract.Presenter {

    override fun observeRuntimeVersion() = DataRepository.observeRuntimeDataVersion()

    override fun loadData(context: Context) {
        val orders = DataRepository.loadOrders(context).sortedByDescending { it.startDate }
        val hotelReviewMap = DataRepository.loadHotelReviewSignals(context).associateBy { it.orderId }

        view.showState(
            OrdersUiState(
                activeOrders = orders.filterByStatus(status = "ACTIVE", hotelReviewMap = hotelReviewMap),
                historyOrders = orders.filterByStatus(status = "COMPLETED", hotelReviewMap = hotelReviewMap),
                cancelledOrders = orders.filterByStatus(status = "CANCELLED", hotelReviewMap = hotelReviewMap)
            )
        )
    }

    override fun saveHotelReview(
        context: Context,
        orderId: String,
        rating: Int,
        comment: String
    ) {
        val sanitizedRating = rating.coerceIn(1, 5)
        val sanitizedComment = comment.trim()
        val order = DataRepository.loadOrders(context).firstOrNull { it.orderId == orderId } ?: return
        if (order.status != "COMPLETED" || order.orderType != "STAY") return

        val existingSignal = DataRepository.loadHotelReviewSignals(context).firstOrNull { it.orderId == orderId }
        DataRepository.upsertHotelReviewSignal(
            context = context,
            signal = HotelReviewSignal(
                signalId = existingSignal?.signalId ?: "hotel_review_${UUID.randomUUID()}",
                userId = order.userId,
                orderId = order.orderId,
                hotelId = order.itemId,
                hotelName = order.itemName,
                rating = sanitizedRating,
                comment = sanitizedComment,
                updatedAt = System.currentTimeMillis()
            )
        )
        loadData(context)
    }

    private fun List<Order>.filterByStatus(
        status: String,
        hotelReviewMap: Map<String, HotelReviewSignal>
    ): List<OrderCardUiModel> {
        return filter { it.status == status }.map { order ->
            val review = hotelReviewMap[order.orderId]
            val isHistoryStayOrder = status == "COMPLETED" && order.orderType == "STAY"
            OrderCardUiModel(
                orderId = order.orderId,
                itemName = order.itemName,
                typeLabel = BookingFormatters.humanizeEnum(order.orderType),
                dateRange = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                guestLabel = "${order.guestCount} guest" + if (order.guestCount == 1) "" else "s",
                totalPrice = BookingFormatters.formatCurrency(order.totalPrice, order.currency),
                bookedOn = "Booked ${BookingFormatters.formatFullDate(order.bookingDate)}",
                status = status,
                showReviewAction = isHistoryStayOrder,
                reviewActionLabel = if (review == null) "Write review" else "Edit review",
                reviewRating = review?.rating,
                reviewComment = review?.comment.orEmpty(),
                reviewUpdatedOn = review?.let {
                    "Updated ${BookingFormatters.formatFullDate(it.updatedAt)}"
                }.orEmpty()
            )
        }
    }
}
