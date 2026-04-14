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
        fun calculateSpentAmount(context: Context): String
        fun cancelFutureActiveOrders(context: Context): Int
        fun prepareStayBookAgain(context: Context, orderId: String): Boolean
    }
}

data class OrdersUiState(
    val activeOrders: List<OrderCardUiModel> = emptyList(),
    val historyOrders: List<OrderCardUiModel> = emptyList(),
    val cancelledOrders: List<OrderCardUiModel> = emptyList(),
    val nextUpcomingTrip: UpcomingTripUiModel? = null
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
    val reviewUpdatedOn: String = "",
    val showBookAgainAction: Boolean = false,
    val bookAgainActionLabel: String = "Book again"
)

data class UpcomingTripUiModel(
    val title: String,
    val subtitle: String,
    val supportingText: String
)
