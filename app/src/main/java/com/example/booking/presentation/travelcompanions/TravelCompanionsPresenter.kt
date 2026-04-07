package com.example.booking.presentation.travelcompanions

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository

class TravelCompanionsPresenter(
    private val view: TravelCompanionsContract.View
) : TravelCompanionsContract.Presenter {

    override fun loadData(context: Context) {
        val companions = DataRepository.loadTravelCompanions(context).map { companion ->
            TravelCompanionUiModel(
                companionId = companion.companionId,
                fullName = BookingFormatters.formatFullName(companion.firstName, companion.lastName),
                dateOfBirth = BookingFormatters.formatTravelerBirthDate(companion.dateOfBirth),
                gender = "Not provided"
            )
        }

        view.showState(TravelCompanionsUiState(companions = companions))
    }
}
