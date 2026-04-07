package com.example.booking.presentation.carrentals.booking

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.BookingSignal
import com.example.booking.model.Order
import com.example.booking.presentation.carrentals.common.CarRentalDraftStore
import com.example.booking.presentation.carrentals.common.CarRentalFlowMapper
import java.util.UUID

class CarRentalSummaryPresenter(
    private val view: CarRentalSummaryContract.View
) : CarRentalSummaryContract.Presenter {

    override fun loadData(context: Context) {
        val draft = CarRentalDraftStore.snapshot()
        val car = DataRepository.loadCarRentals(context).firstOrNull { it.carId == draft.selectedCarId }
        if (car == null) {
            view.showState(CarRentalSummaryUiState())
            return
        }
        val days = CarRentalFlowMapper.rentalDays(draft)
        val basePrice = car.pricePerDay * days
        val discount = basePrice * CarRentalFlowMapper.geniusDiscount(car)
        val subtotal = basePrice - discount
        view.showState(
            CarRentalSummaryUiState(
                title = car.carModel,
                subtitle = "or similar ${car.category.lowercase()}",
                pickupLine = "${BookingFormatters.formatLocalDateTime(draft.pickupDateTime)}\n${draft.pickupLocation}",
                dropOffLine = "${BookingFormatters.formatLocalDateTime(draft.returnDateTime)}\n${draft.pickupLocation}",
                companyName = car.companyName,
                includedLine = if (car.freeCancellation) "Free cancellation up to 48 hours before pick-up" else "Standard cancellation policy",
                priceLineItems = listOf(
                    "Car rental price" to BookingFormatters.formatCurrency(basePrice, car.currency),
                    "Discounts and savings" to "-${BookingFormatters.formatCurrency(discount, car.currency)}",
                    "Subtotal" to BookingFormatters.formatCurrency(subtotal, car.currency)
                ),
                totalPriceText = BookingFormatters.formatCurrency(subtotal, car.currency),
                totalLabel = "Total rental price",
                canContinue = true
            )
        )
    }

    override fun completeBooking(context: Context): String? {
        val draft = CarRentalDraftStore.snapshot()
        val user = DataRepository.loadUsers(context).firstOrNull()
        val car = DataRepository.loadCarRentals(context).firstOrNull { it.carId == draft.selectedCarId } ?: return null
        val days = CarRentalFlowMapper.rentalDays(draft)
        val totalPrice = car.pricePerDay * days * (1 - CarRentalFlowMapper.geniusDiscount(car))
        val now = System.currentTimeMillis()
        val orderId = "car_rental_${UUID.randomUUID()}"
        val userId = user?.userId ?: "user001"
        val itemName = "${car.companyName} - ${car.carModel}"
        val startDate = BookingFormatters.localDateTimeToEpochMillis(draft.pickupDateTime)
        val endDate = BookingFormatters.localDateTimeToEpochMillis(draft.returnDateTime)

        DataRepository.appendOrder(
            context = context,
            order = Order(
                orderId = orderId,
                userId = userId,
                orderType = "CAR_RENTAL",
                status = "ACTIVE",
                itemId = car.carId,
                itemName = itemName,
                bookingDate = now,
                startDate = startDate,
                endDate = endDate,
                totalPrice = totalPrice,
                currency = car.currency,
                guestCount = 1
            )
        )
        DataRepository.appendBookingSignal(
            context = context,
            signal = BookingSignal(
                signalId = "car_rental_booking_${UUID.randomUUID()}",
                userId = userId,
                orderType = "CAR_RENTAL",
                itemId = car.carId,
                itemName = itemName,
                totalPrice = totalPrice,
                currency = car.currency,
                guestCount = 1,
                startDate = startDate,
                endDate = endDate,
                createdAt = now
            )
        )
        CarRentalDraftStore.markBookingComplete(orderId)
        return orderId
    }
}

class CarRentalBookingSuccessPresenter(
    private val view: CarRentalBookingSuccessContract.View
) : CarRentalBookingSuccessContract.Presenter {

    override fun loadData(context: Context, orderId: String) {
        val order = DataRepository.loadOrderById(context, orderId)
        if (order == null) {
            view.showState(
                CarRentalBookingSuccessUiState(
                    title = "Booking not found",
                    note = "The car rental booking is missing from the local runtime order file."
                )
            )
            return
        }
        view.showState(
            CarRentalBookingSuccessUiState(
                hasOrder = true,
                title = "Your rental car is booked",
                orderId = order.orderId,
                itemName = order.itemName,
                dateLabel = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                guestLabel = "Driver included",
                totalPriceText = BookingFormatters.formatCurrency(order.totalPrice, order.currency),
                note = "The new car-rental order and booking signal were saved locally and are now available in Trips."
            )
        )
    }
}
