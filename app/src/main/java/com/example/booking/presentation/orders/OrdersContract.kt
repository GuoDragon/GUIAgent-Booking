package com.example.booking.presentation.orders

import android.content.Context
import kotlinx.coroutines.flow.StateFlow

interface OrdersContract {
    interface View {
        fun showState(state: OrdersUiState)
    }

    interface Presenter {
        fun observeRuntimeVersion(): StateFlow<Int>
        fun loadData(context: Context)
        fun saveHotelReview(
            context: Context,
            orderId: String,
            rating: Int,
            comment: String
        )
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
    val status: String,
    val showReviewAction: Boolean = false,
    val reviewActionLabel: String = "",
    val reviewRating: Int? = null,
    val reviewComment: String = "",
    val reviewUpdatedOn: String = ""
)
