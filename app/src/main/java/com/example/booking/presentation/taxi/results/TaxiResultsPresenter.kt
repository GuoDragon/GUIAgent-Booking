package com.example.booking.presentation.taxi.results

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.TaxiRoute
import com.example.booking.presentation.taxi.common.TaxiDraftStore
import com.example.booking.presentation.taxi.common.TaxiFlowMapper

class TaxiResultsPresenter(
    private val view: TaxiResultsContract.View
) : TaxiResultsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = TaxiDraftStore.snapshot()
        val allRoutes = DataRepository.loadTaxiRoutes(context)
        val exactRoutes = TaxiFlowMapper.sortRoutes(TaxiFlowMapper.filterRoutes(allRoutes, draft))
        val routes = if (exactRoutes.size >= 4) {
            exactRoutes
        } else {
            exactRoutes + allRoutes.filterNot { candidate ->
                exactRoutes.any { it.routeId == candidate.routeId }
            }.take(4 - exactRoutes.size)
        }

        val defaultRouteId = draft.selectedRouteId ?: routes.firstOrNull()?.routeId
        defaultRouteId?.let(TaxiDraftStore::selectRoute)

        val selectedRouteId = TaxiDraftStore.snapshot().selectedRouteId
        val selectedRoute = routes.firstOrNull { it.routeId == selectedRouteId }

        view.showState(
            TaxiResultsUiState(
                title = "${draft.tripType.label} options for ${draft.passengerCount} passenger" +
                    if (draft.passengerCount == 1) "" else "s",
                subtitle = "${draft.pickupLocation} -> ${draft.destination}",
                cards = routes.map { route ->
                    routeToCard(route = route, selected = route.routeId == selectedRouteId)
                },
                selectedRouteLabel = selectedRoute?.vehicleType ?: "",
                selectedPriceText = selectedRoute?.let {
                    BookingFormatters.formatCurrency(it.price, it.currency)
                }.orEmpty(),
                canContinue = selectedRoute != null
            )
        )
    }

    override fun selectRoute(context: Context, routeId: String) {
        TaxiDraftStore.selectRoute(routeId)
        loadData(context)
    }

    private fun routeToCard(
        route: TaxiRoute,
        selected: Boolean
    ): TaxiResultCardUiModel {
        return TaxiResultCardUiModel(
            cardId = route.routeId,
            routeId = route.routeId,
            title = route.vehicleType,
            seatText = "${route.maxPassengers} seats",
            bagText = "${TaxiFlowMapper.bagCount(route)} bags",
            driverText = "Driver waits in arrivals and tracks your landing time",
            cancelText = "Free cancellation up to 24 hours before pick-up",
            locationText = route.destination,
            durationText = "${route.estimatedDurationMinutes} min ride",
            priceText = BookingFormatters.formatCurrency(route.price, route.currency),
            selected = selected
        )
    }
}
