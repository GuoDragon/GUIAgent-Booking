package com.example.booking.presentation.attractions.common

import com.example.booking.model.Attraction
import com.example.booking.model.AttractionTicket

object AttractionFlowMapper {

    fun filterAttractions(
        attractions: List<Attraction>,
        draft: AttractionDraft
    ): List<Attraction> {
        val query = draft.destinationQuery.trim()
        if (query.isBlank()) return attractions
        return attractions.filter { attraction ->
            attraction.city.contains(query, ignoreCase = true) ||
                attraction.country.contains(query, ignoreCase = true) ||
                attraction.name.contains(query, ignoreCase = true)
        }.ifEmpty { attractions }
    }

    fun sortAttractions(attractions: List<Attraction>): List<Attraction> {
        return attractions.sortedWith(compareByDescending<Attraction> { it.rating }.thenBy { it.fromPrice })
    }

    fun ticketsForAttraction(
        tickets: List<AttractionTicket>,
        attractionId: String?
    ): List<AttractionTicket> {
        return tickets.filter { it.attractionId == attractionId }.sortedBy { it.price }
    }
}
