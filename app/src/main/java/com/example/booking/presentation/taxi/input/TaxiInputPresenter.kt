package com.example.booking.presentation.taxi.input

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.presentation.taxi.common.TaxiDraftStore
import com.example.booking.presentation.taxi.common.TaxiTripType
import java.time.LocalDateTime

class TaxiLocationPresenter(
    private val view: TaxiLocationContract.View
) : TaxiLocationContract.Presenter {

    override fun loadPickupLocations(context: Context) {
        val draft = TaxiDraftStore.snapshot()
        val options = DataRepository.loadTaxiRoutes(context)
            .map { it.pickupLocation }
            .distinct()
            .sorted()
        view.showState(
            TaxiLocationUiState(
                title = "Pick-up location",
                subtitle = "Choose one of the local taxi demo pick-up points.",
                selectedValue = draft.pickupLocation,
                options = options
            )
        )
    }

    override fun loadDestinations(context: Context) {
        val draft = TaxiDraftStore.snapshot()
        val options = DataRepository.loadTaxiRoutes(context)
            .map { it.destination }
            .distinct()
            .sorted()
        view.showState(
            TaxiLocationUiState(
                title = "Destination",
                subtitle = "Keep it within the local taxi route data so the demo results can match.",
                selectedValue = draft.destination,
                options = options
            )
        )
    }

    override fun selectPickupLocation(value: String) {
        TaxiDraftStore.update { draft -> draft.copy(pickupLocation = value) }
    }

    override fun selectDestination(value: String) {
        TaxiDraftStore.update { draft -> draft.copy(destination = value) }
    }
}

class TaxiTimePresenter(
    private val view: TaxiTimeContract.View
) : TaxiTimeContract.Presenter {

    override fun loadTimeOptions() {
        val draft = TaxiDraftStore.snapshot()
        val baseDate = draft.pickupDateTime.toLocalDate()
        val pickupOptions = listOf(0L, 2L, 4L, 8L, 24L, 26L).map { hours ->
            draft.pickupDateTime.withHour(9).withMinute(0).plusHours(hours)
        }
        val returnOptions = listOf(1L, 4L, 8L, 24L, 28L, 32L).map { hours ->
            draft.pickupDateTime.withHour(13).withMinute(0).plusHours(hours)
        }
        view.showState(
            TaxiTimeUiState(
                tripLabel = if (draft.tripType == TaxiTripType.RoundTrip) "Round-trip taxi" else "One-way taxi",
                currentPickupLabel = BookingFormatters.formatLocalDateTime(draft.pickupDateTime),
                currentReturnLabel = BookingFormatters.formatLocalDateTime(draft.returnDateTime),
                pickupOptions = pickupOptions.map { option ->
                    option.withYear(baseDate.year)
                },
                returnOptions = returnOptions,
                roundTrip = draft.tripType == TaxiTripType.RoundTrip
            )
        )
    }

    override fun applySelection(pickupDateTime: LocalDateTime, returnDateTime: LocalDateTime) {
        TaxiDraftStore.update { draft ->
            draft.copy(
                pickupDateTime = pickupDateTime,
                returnDateTime = if (draft.tripType == TaxiTripType.RoundTrip) {
                    returnDateTime
                } else {
                    pickupDateTime.plusHours(6)
                },
                returnDateTimeConfirmed = draft.tripType == TaxiTripType.RoundTrip
            )
        }
    }
}

class TaxiPassengerPresenter(
    private val view: TaxiPassengerContract.View
) : TaxiPassengerContract.Presenter {

    override fun loadData() {
        val draft = TaxiDraftStore.snapshot()
        view.showState(
            TaxiPassengerUiState(
                tripLabel = if (draft.tripType == TaxiTripType.RoundTrip) "Round-trip taxi" else "One-way taxi",
                passengerCount = draft.passengerCount,
                helperText = "Routes only show cars that can fit every passenger."
            )
        )
    }

    override fun updatePassengerCount(value: Int) {
        TaxiDraftStore.update { draft ->
            draft.copy(passengerCount = value.coerceIn(1, 8))
        }
    }
}
