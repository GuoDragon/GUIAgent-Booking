package com.example.booking.presentation.flights.booking

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.BookingSignal
import com.example.booking.model.Order
import com.example.booking.presentation.flights.common.FlightDraftStore
import com.example.booking.presentation.flights.common.FlightFareOption
import com.example.booking.presentation.flights.common.FlightFlexibleTicketOption
import com.example.booking.presentation.flights.common.FlightFlowMapper
import java.util.UUID

class FlightFarePresenter(
    private val view: FlightFareContract.View
) : FlightFareContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val itinerary = selectedItinerary(context) ?: run {
            view.showState(FlightFareUiState())
            return
        }
        view.showState(
            FlightFareUiState(
                title = FlightFlowMapper.itineraryRouteLabel(itinerary),
                totalPriceText = formatTotalPrice(itinerary.totalPrice, itinerary.currency, draft.selectedFareOption, draft.flexibleTicketOption),
                options = FlightFareOption.entries.map { option ->
                    FlightFareOptionUiModel(
                        option = option,
                        title = option.title,
                        subtitle = "${option.baggageLabel} | ${option.changePolicy}",
                        selected = option == draft.selectedFareOption
                    )
                },
                canContinue = true
            )
        )
    }

    override fun applyFare(option: FlightFareOption) {
        FlightDraftStore.update { draft -> draft.copy(selectedFareOption = option) }
    }

    private fun selectedItinerary(context: Context) = FlightFlowMapper.findItinerary(
        flights = DataRepository.loadFlights(context),
        airlines = DataRepository.loadAirlines(context),
        airports = DataRepository.loadAirports(context),
        draft = FlightDraftStore.snapshot()
    )
}

class FlightLuggagePresenter(
    private val view: FlightLuggageContract.View
) : FlightLuggageContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val itinerary = selectedItinerary(context) ?: run {
            view.showState(FlightLuggageUiState())
            return
        }
        view.showState(
            FlightLuggageUiState(
                includedBagText = draft.selectedFareOption.baggageLabel,
                mealChoice = draft.selectedMeal,
                totalPriceText = formatTotalPrice(itinerary.totalPrice, itinerary.currency, draft.selectedFareOption, draft.flexibleTicketOption),
                flexibleOptions = FlightFlexibleTicketOption.entries.map { option ->
                    FlightFlexibleOptionUiModel(
                        option = option,
                        title = option.title,
                        priceText = BookingFormatters.formatCurrency(option.priceOffset, itinerary.currency),
                        selected = option == draft.flexibleTicketOption
                    )
                },
                canContinue = true
            )
        )
    }

    override fun applySelection(noAdditionalBaggage: Boolean, flexibleOption: FlightFlexibleTicketOption) {
        FlightDraftStore.update { draft ->
            draft.copy(
                noAdditionalBaggage = noAdditionalBaggage,
                flexibleTicketOption = flexibleOption
            )
        }
    }

    private fun selectedItinerary(context: Context) = FlightFlowMapper.findItinerary(
        flights = DataRepository.loadFlights(context),
        airlines = DataRepository.loadAirlines(context),
        airports = DataRepository.loadAirports(context),
        draft = FlightDraftStore.snapshot()
    )
}

class FlightMealChoicePresenter(
    private val view: FlightMealChoiceContract.View
) : FlightMealChoiceContract.Presenter {

    private val options = listOf(
        "No preference",
        "Vegetarian - free",
        "Vegan - free",
        "Lactose-free - free",
        "Gluten-free - free",
        "Kosher - free",
        "Halal - free"
    )

    override fun loadData() {
        val draft = FlightDraftStore.snapshot()
        view.showState(
            FlightMealChoiceUiState(
                options = options,
                selectedMeal = draft.selectedMeal
            )
        )
    }

    override fun selectMeal(option: String) {
        FlightDraftStore.update { draft -> draft.copy(selectedMeal = option) }
        loadData()
    }
}

class FlightSeatPresenter(
    private val view: FlightSeatContract.View
) : FlightSeatContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val itinerary = selectedItinerary(context) ?: run {
            view.showState(FlightSeatUiState())
            return
        }
        view.showState(
            FlightSeatUiState(
                routeTitle = FlightFlowMapper.itineraryRouteLabel(itinerary),
                seatRows = FlightFlowMapper.seatRows(itinerary),
                totalPriceText = formatTotalPrice(itinerary.totalPrice, itinerary.currency, draft.selectedFareOption, draft.flexibleTicketOption),
                canContinue = true
            )
        )
    }

    private fun selectedItinerary(context: Context) = FlightFlowMapper.findItinerary(
        flights = DataRepository.loadFlights(context),
        airlines = DataRepository.loadAirlines(context),
        airports = DataRepository.loadAirports(context),
        draft = FlightDraftStore.snapshot()
    )
}

class FlightTravelerDetailsPresenter(
    private val view: FlightTravelerDetailsContract.View
) : FlightTravelerDetailsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val user = DataRepository.loadUsers(context).firstOrNull()
        val itinerary = selectedItinerary(context)
        view.showState(
            FlightTravelerDetailsUiState(
                firstName = draft.travelerFirstName.ifBlank { user?.firstName.orEmpty() },
                lastName = draft.travelerLastName.ifBlank { user?.lastName.orEmpty() },
                gender = draft.travelerGender,
                totalPriceText = itinerary?.let {
                    formatTotalPrice(it.totalPrice, it.currency, draft.selectedFareOption, draft.flexibleTicketOption)
                }.orEmpty()
            )
        )
    }

    override fun saveDetails(firstName: String, lastName: String, gender: String) {
        FlightDraftStore.update { draft ->
            draft.copy(
                travelerFirstName = firstName.trim(),
                travelerLastName = lastName.trim(),
                travelerGender = gender.trim()
            )
        }
    }

    private fun selectedItinerary(context: Context) = FlightFlowMapper.findItinerary(
        flights = DataRepository.loadFlights(context),
        airlines = DataRepository.loadAirlines(context),
        airports = DataRepository.loadAirports(context),
        draft = FlightDraftStore.snapshot()
    )
}

class FlightTravelerContactPresenter(
    private val view: FlightTravelerContactContract.View
) : FlightTravelerContactContract.Presenter {

    override fun loadData(context: Context) {
        val draft = FlightDraftStore.snapshot()
        val user = DataRepository.loadUsers(context).firstOrNull()
        val phoneParts = BookingFormatters.parsePhoneParts(user?.phone)
        val itinerary = selectedItinerary(context)
        view.showState(
            FlightTravelerContactUiState(
                email = draft.contactEmail.ifBlank { user?.email.orEmpty() },
                phoneCountryCode = draft.contactPhoneCountryCode.ifBlank { phoneParts.first },
                phoneNumber = draft.contactPhoneNumber.ifBlank { phoneParts.second },
                totalPriceText = itinerary?.let {
                    formatTotalPrice(it.totalPrice, it.currency, draft.selectedFareOption, draft.flexibleTicketOption)
                }.orEmpty(),
                canComplete = itinerary != null
            )
        )
    }

    override fun completeBooking(
        context: Context,
        email: String,
        phoneCountryCode: String,
        phoneNumber: String
    ): String? {
        val draft = FlightDraftStore.snapshot()
        val user = DataRepository.loadUsers(context).firstOrNull()
        val itinerary = selectedItinerary(context) ?: return null
        val orderId = "flight_${UUID.randomUUID()}"
        val totalPrice = itinerary.totalPrice + draft.selectedFareOption.priceOffset + draft.flexibleTicketOption.priceOffset
        val itemName = "${itinerary.outbound.departureAirportCode} to ${itinerary.outbound.arrivalAirportCode} - ${itinerary.outbound.airlineName}"
        val now = System.currentTimeMillis()
        val userId = user?.userId ?: "user001"

        FlightDraftStore.update { currentDraft ->
            currentDraft.copy(
                contactEmail = email.trim(),
                contactPhoneCountryCode = phoneCountryCode.trim(),
                contactPhoneNumber = phoneNumber.trim()
            )
        }

        DataRepository.appendOrder(
            context = context,
            order = Order(
                orderId = orderId,
                userId = userId,
                orderType = "FLIGHT",
                status = "ACTIVE",
                itemId = itinerary.outbound.flightId ?: itinerary.itineraryId,
                itemName = itemName,
                bookingDate = now,
                startDate = itinerary.outbound.departureTime,
                endDate = itinerary.returnLeg?.arrivalTime,
                totalPrice = totalPrice,
                currency = itinerary.currency,
                guestCount = FlightDraftStore.snapshot().adultCount
            )
        )
        DataRepository.appendBookingSignal(
            context = context,
            signal = BookingSignal(
                signalId = "flight_booking_${UUID.randomUUID()}",
                userId = userId,
                orderType = "FLIGHT",
                itemId = itinerary.outbound.flightId ?: itinerary.itineraryId,
                itemName = itemName,
                totalPrice = totalPrice,
                currency = itinerary.currency,
                guestCount = FlightDraftStore.snapshot().adultCount,
                startDate = itinerary.outbound.departureTime,
                endDate = itinerary.returnLeg?.arrivalTime,
                createdAt = now
            )
        )
        FlightDraftStore.markBookingComplete(orderId)
        return orderId
    }

    private fun selectedItinerary(context: Context) = FlightFlowMapper.findItinerary(
        flights = DataRepository.loadFlights(context),
        airlines = DataRepository.loadAirlines(context),
        airports = DataRepository.loadAirports(context),
        draft = FlightDraftStore.snapshot()
    )
}

class FlightBookingSuccessPresenter(
    private val view: FlightBookingSuccessContract.View
) : FlightBookingSuccessContract.Presenter {

    override fun loadData(context: Context, orderId: String) {
        val order = DataRepository.loadOrderById(context, orderId)
        if (order == null) {
            view.showState(
                FlightBookingSuccessUiState(
                    title = "Booking not found",
                    note = "The booking was not found in the local runtime order file."
                )
            )
            return
        }
        view.showState(
            FlightBookingSuccessUiState(
                hasOrder = true,
                title = "Your flight is booked",
                orderId = order.orderId,
                itemName = order.itemName,
                dateLabel = BookingFormatters.formatDateRange(order.startDate, order.endDate),
                guestLabel = "${order.guestCount} traveler" + if (order.guestCount == 1) "" else "s",
                totalPriceText = BookingFormatters.formatCurrency(order.totalPrice, order.currency),
                note = "The new flight order and booking signal were saved locally and are now available in Trips."
            )
        )
    }
}

private fun formatTotalPrice(
    basePrice: Double,
    currency: String,
    fareOption: FlightFareOption,
    flexibleTicketOption: FlightFlexibleTicketOption
): String {
    return BookingFormatters.formatCurrency(basePrice + fareOption.priceOffset + flexibleTicketOption.priceOffset, currency)
}
