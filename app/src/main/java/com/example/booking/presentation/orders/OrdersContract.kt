package com.example.booking.presentation.orders

import android.content.Context

interface OrdersContract {
    interface View {
        fun showState(state: OrdersUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class OrdersUiState(
    val activeOrders: List<OrderCardUiModel> = emptyList(),
    val historyOrders: List<OrderCardUiModel> = emptyList(),
    val cancelledOrders: List<OrderCardUiModel> = emptyList()
)

data class OrderCardUiModel(
    val orderId: String,
    val itemName: String,
    val typeLabel: String,
    val dateRange: String,
    val guestLabel: String,
    val totalPrice: String,
    val bookedOn: String,
    val status: String
)
