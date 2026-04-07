package com.example.booking.presentation.orders

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.Order

class OrdersPresenter(
    private val view: OrdersContract.View
) : OrdersContract.Presenter {

    override fun loadData(context: Context) {
        val orders = DataRepository.loadOrders(context).sortedByDescending { it.startDate }

        view.showState(
            OrdersUiState(
                activeOrders = orders.filterByStatus("ACTIVE"),
                historyOrders = orders.filterByStatus("COMPLETED"),
                cancelledOrders = orders.filterByStatus("CANCELLED")
            )
        )
    }

    private fun List<Order>.filterByStatus(status: String): List<OrderCardUiModel> {
        return filter { it.status == status }.map { order ->
            OrderCardUiModel(
                orderId = order.orderId,
                itemName = order.itemName,
                typeLabel = BookingFormatters.humanizeEnum(order.orderType),
                dateRange = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                guestLabel = "${order.guestCount} guest" + if (order.guestCount == 1) "" else "s",
                totalPrice = BookingFormatters.formatCurrency(order.totalPrice, order.currency),
                bookedOn = "Booked ${BookingFormatters.formatFullDate(order.bookingDate)}",
                status = status
            )
        }
    }
}
