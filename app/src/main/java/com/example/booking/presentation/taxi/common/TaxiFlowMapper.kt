package com.example.booking.presentation.taxi.common

import com.example.booking.model.TaxiRoute

object TaxiFlowMapper {

    fun filterRoutes(
        routes: List<TaxiRoute>,
        draft: TaxiDraft
    ): List<TaxiRoute> {
        return routes.filter { route ->
            route.pickupLocation.contains(draft.pickupLocation, ignoreCase = true) &&
                route.destination.contains(draft.destination, ignoreCase = true) &&
                route.maxPassengers >= draft.passengerCount
        }.ifEmpty {
            routes.filter { route -> route.maxPassengers >= draft.passengerCount }
        }
    }

    fun sortRoutes(routes: List<TaxiRoute>): List<TaxiRoute> {
        return routes.sortedWith(compareBy<TaxiRoute> { it.price }.thenBy { it.estimatedDurationMinutes })
    }

    fun bagCount(route: TaxiRoute): Int {
        return when {
            route.maxPassengers >= 6 -> 4
            route.maxPassengers >= 4 -> 3
            else -> 2
        }
    }
}
