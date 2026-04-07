package com.example.booking.presentation.saved

import android.content.Context
import com.example.booking.common.resolver.WishlistResolver
import com.example.booking.data.DataRepository

class SavedPresenter(
    private val view: SavedContract.View
) : SavedContract.Presenter {

    override fun loadData(context: Context) {
        val groups = WishlistResolver.buildGroups(
            wishlistItems = DataRepository.loadWishlistItems(context),
            hotels = DataRepository.loadHotels(context),
            flights = DataRepository.loadFlights(context),
            airports = DataRepository.loadAirports(context),
            attractions = DataRepository.loadAttractions(context),
            carRentals = DataRepository.loadCarRentals(context),
            cruises = DataRepository.loadCruises(context)
        ).map {
            SavedGroupUiModel(
                title = it.title,
                savedItemCount = it.count
            )
        }

        view.showState(SavedUiState(groups = groups))
    }
}
