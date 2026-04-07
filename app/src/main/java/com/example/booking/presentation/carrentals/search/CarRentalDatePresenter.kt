package com.example.booking.presentation.carrentals.search

import com.example.booking.presentation.carrentals.common.CarRentalDraftStore
import java.time.LocalDateTime

class CarRentalDatePresenter(
    private val view: CarRentalDateContract.View
) : CarRentalDateContract.Presenter {

    override fun loadData() {
        val draft = CarRentalDraftStore.snapshot()
        view.showState(
            CarRentalDateUiState(
                pickupDateTime = draft.pickupDateTime,
                returnDateTime = draft.returnDateTime,
                calendarMonths = listOf(
                    draft.pickupDateTime.toLocalDate().withDayOfMonth(1),
                    draft.pickupDateTime.toLocalDate().plusMonths(1).withDayOfMonth(1)
                )
            )
        )
    }

    override fun applyDates(pickupDateTime: LocalDateTime, returnDateTime: LocalDateTime) {
        CarRentalDraftStore.update { draft ->
            draft.copy(
                pickupDateTime = pickupDateTime,
                returnDateTime = if (returnDateTime <= pickupDateTime) pickupDateTime.plusDays(2) else returnDateTime
            )
        }
    }
}
