package com.example.booking.presentation.attractions.input

import android.content.Context
import com.example.booking.data.DataRepository
import com.example.booking.presentation.attractions.common.AttractionDraftStore
import java.time.LocalDate

class AttractionDestinationPresenter(
    private val view: AttractionDestinationContract.View
) : AttractionDestinationContract.Presenter {

    override fun loadData(context: Context) {
        val draft = AttractionDraftStore.snapshot()
        val options = DataRepository.loadAttractions(context)
            .map { "${it.city}, ${it.country}" }
            .distinct()
            .sorted()
        view.showState(
            AttractionDestinationUiState(
                selectedValue = draft.destinationQuery,
                options = options
            )
        )
    }

    override fun selectDestination(value: String) {
        AttractionDraftStore.update { draft -> draft.copy(destinationQuery = value) }
    }
}

class AttractionDatePresenter(
    private val view: AttractionDateContract.View
) : AttractionDateContract.Presenter {

    override fun loadData() {
        val draft = AttractionDraftStore.snapshot()
        view.showState(
            AttractionDateUiState(
                selectedDate = draft.selectedDate,
                options = List(10) { index -> LocalDate.now().plusDays(index.toLong() + 1) }
            )
        )
    }

    override fun selectDate(date: LocalDate) {
        AttractionDraftStore.update { draft -> draft.copy(selectedDate = date) }
    }
}
