package com.example.booking.presentation.stays.input

import android.content.Context
import com.example.booking.data.DataRepository
import com.example.booking.presentation.stays.common.StayDraftStore
import java.time.LocalDate
import java.util.Locale

class StayDestinationPresenter(
    private val view: StayDestinationContract.View
) : StayDestinationContract.Presenter {

    override fun loadData(context: Context, query: String) {
        val draft = StayDraftStore.snapshot()
        val hotels = DataRepository.loadHotels(context)
        val normalizedQuery = query.trim()
        val loweredQuery = normalizedQuery.lowercase(Locale.ENGLISH)

        val citySuggestions = hotels
            .map { it.city to it.country }
            .distinct()
            .filter { (city, country) ->
                loweredQuery.isBlank() ||
                    city.lowercase(Locale.ENGLISH).contains(loweredQuery) ||
                    country.lowercase(Locale.ENGLISH).contains(loweredQuery)
            }
            .take(6)
            .map { (city, country) ->
                StayDestinationSuggestionUiModel(
                    title = city,
                    subtitle = country,
                    type = if (city.equals(draft.destinationQuery, ignoreCase = true)) {
                        StayDestinationSuggestionType.Recent
                    } else {
                        StayDestinationSuggestionType.City
                    }
                )
            }

        val propertySuggestions = hotels
            .filter { hotel ->
                loweredQuery.isBlank() ||
                    hotel.name.lowercase(Locale.ENGLISH).contains(loweredQuery)
            }
            .take(4)
            .map { hotel ->
                StayDestinationSuggestionUiModel(
                    title = hotel.name,
                    subtitle = "${hotel.city}, ${hotel.country}",
                    type = StayDestinationSuggestionType.Property
                )
            }

        val recentSuggestions = buildList {
            if (draft.destinationQuery.isNotBlank()) {
                add(
                    StayDestinationSuggestionUiModel(
                        title = draft.destinationQuery,
                        subtitle = "From your latest Stays draft",
                        type = StayDestinationSuggestionType.Recent
                    )
                )
            }
            addAll(citySuggestions.take(2))
        }.distinctBy { it.title }

        view.showState(
            StayDestinationUiState(
                query = normalizedQuery,
                recentSuggestions = recentSuggestions,
                propertySuggestions = propertySuggestions.ifEmpty { citySuggestions }
            )
        )
    }

    override fun applyDestination(query: String) {
        StayDraftStore.update { draft ->
            draft.copy(destinationQuery = query.trim())
        }
    }
}

class StayDatePresenter(
    private val view: StayDateContract.View
) : StayDateContract.Presenter {

    override fun loadData() {
        val draft = StayDraftStore.snapshot()
        view.showState(
            StayDateUiState(
                checkInDate = draft.checkInDate,
                checkOutDate = draft.checkOutDate,
                calendarMonths = listOf(
                    draft.checkInDate.withDayOfMonth(1),
                    draft.checkInDate.plusMonths(1).withDayOfMonth(1)
                )
            )
        )
    }

    override fun applyDates(checkInDate: LocalDate, checkOutDate: LocalDate) {
        StayDraftStore.update { draft ->
            draft.copy(
                checkInDate = checkInDate,
                checkOutDate = if (checkOutDate <= checkInDate) checkInDate.plusDays(1) else checkOutDate
            )
        }
    }
}

class StayGuestsPresenter(
    private val view: StayGuestsContract.View
) : StayGuestsContract.Presenter {

    override fun loadData() {
        val draft = StayDraftStore.snapshot()
        view.showState(
            StayGuestsUiState(
                roomCount = draft.roomCount,
                adultCount = draft.adultCount,
                childCount = draft.childCount,
                travelingWithPets = draft.travelingWithPets
            )
        )
    }

    override fun applySelection(
        roomCount: Int,
        adultCount: Int,
        childCount: Int,
        travelingWithPets: Boolean
    ) {
        StayDraftStore.update { draft ->
            draft.copy(
                roomCount = roomCount,
                adultCount = adultCount,
                childCount = childCount,
                travelingWithPets = travelingWithPets
            )
        }
    }
}
