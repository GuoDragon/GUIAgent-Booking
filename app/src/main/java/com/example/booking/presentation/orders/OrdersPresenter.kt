package com.example.booking.presentation.orders

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.AccountActionSignal
import com.example.booking.model.AccountActionTypes
import com.example.booking.model.HotelReviewSignal
import com.example.booking.model.Order
import com.example.booking.presentation.stays.common.StayDraftStore
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlin.math.max

class OrdersPresenter(
    private val view: OrdersContract.View
) : OrdersContract.Presenter {

    companion object {
        private val SPENT_ORDER_STATUSES = setOf("COMPLETED", "ACTIVE")
    }

    override fun observeRuntimeVersion() = DataRepository.observeRuntimeDataVersion()

    override fun loadData(context: Context) {
        val orders = DataRepository.loadOrders(context).sortedByDescending { it.startDate }
        val hotelReviewMap = DataRepository.loadHotelReviewSignals(context).associateBy { it.orderId }

        view.showState(
            OrdersUiState(
                activeOrders = orders.filterByStatus(status = "ACTIVE", hotelReviewMap = hotelReviewMap),
                historyOrders = orders.filterByStatus(status = "COMPLETED", hotelReviewMap = hotelReviewMap),
                cancelledOrders = orders.filterByStatus(status = "CANCELLED", hotelReviewMap = hotelReviewMap),
                nextUpcomingTrip = orders.nextUpcomingTrip()
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

    override fun calculateSpentAmount(context: Context): String {
        val totalSpent = DataRepository.calculateSpentAmount(
            context = context,
            statuses = SPENT_ORDER_STATUSES
        )
        val userId = DataRepository.loadUsers(context).firstOrNull()?.userId ?: "user001"
        DataRepository.appendAccountActionSignal(
            context = context,
            signal = AccountActionSignal(
                signalId = "account_action_${UUID.randomUUID()}",
                userId = userId,
                actionType = AccountActionTypes.SPEND_CALCULATED,
                occurredAt = System.currentTimeMillis(),
                displayMessage = "Calculated spending across completed and active orders",
                amount = totalSpent,
                currency = "USD"
            )
        )
        loadData(context)
        return BookingFormatters.formatCurrency(totalSpent, "USD")
    }

    override fun cancelFutureActiveOrders(context: Context): Int {
        val cutoffMillis = nextMonthEndEpochMillis()
        val affectedOrderCount = DataRepository.cancelActiveOrdersAfter(
            context = context,
            cutoffMillis = cutoffMillis
        )
        val userId = DataRepository.loadUsers(context).firstOrNull()?.userId ?: "user001"
        DataRepository.appendAccountActionSignal(
            context = context,
            signal = AccountActionSignal(
                signalId = "account_action_${UUID.randomUUID()}",
                userId = userId,
                actionType = AccountActionTypes.FUTURE_ORDERS_CANCELLED,
                occurredAt = System.currentTimeMillis(),
                displayMessage = "Cancelled active orders scheduled after next month",
                affectedOrderCount = affectedOrderCount,
                extra = mapOf("cutoffMillis" to cutoffMillis.toString())
            )
        )
        loadData(context)
        return affectedOrderCount
    }

    override fun prepareStayBookAgain(context: Context, orderId: String): Boolean {
        val order = DataRepository.loadOrders(context).firstOrNull { it.orderId == orderId } ?: return false
        if (order.orderType != "STAY" || order.status != "COMPLETED") return false

        val hotel = DataRepository.loadHotels(context).firstOrNull { it.hotelId == order.itemId } ?: return false
        val rooms = DataRepository.loadHotelRooms(context).filter { it.hotelId == hotel.hotelId }
        val preferredRoomType = order.itemName.substringAfter(" - ", missingDelimiterValue = "").takeIf { it.isNotBlank() }
        val selectedRoom = rooms.firstOrNull {
            preferredRoomType != null && it.roomType.equals(preferredRoomType, ignoreCase = true)
        } ?: rooms.minByOrNull { it.pricePerNight }

        val previousNightCount = order.endDate?.let { endDateMillis ->
            val start = BookingFormatters.epochMillisToLocalDate(order.startDate)
            val end = BookingFormatters.epochMillisToLocalDate(endDateMillis)
            max(1, ChronoUnit.DAYS.between(start, end).toInt())
        } ?: 1

        val checkInDate = LocalDate.now().plusDays(1)
        val checkOutDate = checkInDate.plusDays(previousNightCount.toLong())

        StayDraftStore.update { draft ->
            draft.copy(
                destinationQuery = hotel.city,
                checkInDate = checkInDate,
                checkOutDate = checkOutDate,
                roomCount = 1,
                adultCount = max(1, order.guestCount),
                childCount = 0,
                selectedHotelId = hotel.hotelId,
                selectedRoomId = selectedRoom?.roomId,
                lastCreatedOrderId = null
            )
        }

        val userId = DataRepository.loadUsers(context).firstOrNull()?.userId ?: order.userId
        DataRepository.appendAccountActionSignal(
            context = context,
            signal = AccountActionSignal(
                signalId = "account_action_${UUID.randomUUID()}",
                userId = userId,
                actionType = AccountActionTypes.STAY_BOOK_AGAIN_PREPARED,
                occurredAt = System.currentTimeMillis(),
                displayMessage = "Prepared stay book-again flow for ${hotel.name}",
                extra = mapOf("sourceOrderId" to order.orderId)
            )
        )

        loadData(context)
        return true
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
                }.orEmpty(),
                showBookAgainAction = isHistoryStayOrder,
                bookAgainActionLabel = "Book again"
            )
        }
    }

    private fun List<Order>.nextUpcomingTrip(): UpcomingTripUiModel? {
        val now = System.currentTimeMillis()
        val activeOrders = filter { it.status == "ACTIVE" }
        if (activeOrders.isEmpty()) return null

        val futureOrder = activeOrders
            .filter { it.startDate >= now }
            .minByOrNull { it.startDate }
            ?: activeOrders.minByOrNull { it.startDate }

        return futureOrder?.let { order ->
            UpcomingTripUiModel(
                title = order.itemName,
                subtitle = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                supportingText = "${BookingFormatters.humanizeEnum(order.orderType)} ? ${BookingFormatters.formatCurrency(order.totalPrice, order.currency)}"
            )
        }
    }

    private fun nextMonthEndEpochMillis(): Long {
        val zoneId = ZoneId.systemDefault()
        val nextMonth = LocalDate.now(zoneId).plusMonths(1)
        val nextMonthEndDate = nextMonth.withDayOfMonth(nextMonth.lengthOfMonth())
        return nextMonthEndDate
            .plusDays(1)
            .atStartOfDay(zoneId)
            .toInstant()
            .toEpochMilli() - 1
    }
}
