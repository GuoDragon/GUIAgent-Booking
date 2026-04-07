package com.example.booking.presentation.addcompanion

class AddTravelCompanionPresenter(
    private val view: AddTravelCompanionContract.View
) : AddTravelCompanionContract.Presenter {

    override fun loadData() {
        view.showState(
            AddTravelCompanionUiState(
                genderOptions = listOf("Female", "Male", "Prefer not to say")
            )
        )
    }
}
