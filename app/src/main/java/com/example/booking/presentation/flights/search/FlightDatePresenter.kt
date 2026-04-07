package com.example.booking.presentation.flights.search

import com.example.booking.presentation.flights.common.FlightDraftStore
import java.time.LocalDate

class FlightDatePresenter(
    private val view: FlightDateContract.View
) : FlightDateContract.Presenter {

    override fun loadData() {
        val draft = FlightDraftStore.snapshot()
        view.showState(
            FlightDateUiState(
                departureDate = draft.departureDate,
                returnDate = draft.returnDate,
                calendarMonths = listOf(
                    draft.departureDate.withDayOfMonth(1),
                    draft.departureDate.plusMonths(1).withDayOfMonth(1)
                )
            )
        )
    }

    override fun applyDates(departureDate: LocalDate, returnDate: LocalDate) {
        FlightDraftStore.update { draft ->
            draft.copy(
                departureDate = departureDate,
                returnDate = if (returnDate <= departureDate) departureDate.plusDays(7) else returnDate
            )
        }
    }
}
