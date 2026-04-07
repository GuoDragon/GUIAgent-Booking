package com.example.booking.presentation.attractions.details

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.presentation.attractions.common.AttractionDraftStore
import com.example.booking.presentation.attractions.common.AttractionFlowMapper

class AttractionPreviewPresenter(
    private val view: AttractionPreviewContract.View
) : AttractionPreviewContract.Presenter {

    override fun loadData(context: Context) {
        val draft = AttractionDraftStore.snapshot()
        val attraction = DataRepository.loadAttractions(context).firstOrNull { it.attractionId == draft.selectedAttractionId }
        if (attraction == null) {
            view.showState(AttractionPreviewUiState())
            return
        }
        view.showState(
            AttractionPreviewUiState(
                hasAttraction = true,
                title = attraction.name,
                ratingText = String.format("%.1f", attraction.rating),
                reviewText = "${attraction.reviewCount} reviews",
                durationText = listOf("45 minutes", "1 hour", "2 hours").first(),
                cancelText = "Free cancellation available",
                dateLabels = List(6) { index ->
                    BookingFormatters.formatShortLocalDate(draft.selectedDate.plusDays(index.toLong()))
                },
                priceText = "From ${BookingFormatters.formatCurrency(attraction.fromPrice, attraction.currency)}"
            )
        )
    }
}

class AttractionDetailsPresenter(
    private val view: AttractionDetailsContract.View
) : AttractionDetailsContract.Presenter {

    override fun loadData(context: Context) {
        val attraction = DataRepository.loadAttractions(context)
            .firstOrNull { it.attractionId == AttractionDraftStore.snapshot().selectedAttractionId }
        if (attraction == null) {
            view.showState(AttractionDetailsUiState())
            return
        }
        view.showState(
            AttractionDetailsUiState(
                hasAttraction = true,
                title = attraction.name,
                locationText = "${attraction.city}, ${attraction.country}",
                categoryText = attraction.category,
                description = attraction.description,
                highlights = listOf(
                    "Instant confirmation",
                    "Mobile ticket",
                    "Top local pick",
                    "Flexible cancellation"
                ),
                priceText = "From ${BookingFormatters.formatCurrency(attraction.fromPrice, attraction.currency)}"
            )
        )
    }
}

class AttractionTicketsPresenter(
    private val view: AttractionTicketsContract.View
) : AttractionTicketsContract.Presenter {

    override fun loadData(context: Context) {
        val draft = AttractionDraftStore.snapshot()
        val attraction = DataRepository.loadAttractions(context).firstOrNull { it.attractionId == draft.selectedAttractionId }
        if (attraction == null) {
            view.showState(AttractionTicketsUiState())
            return
        }
        val tickets = AttractionFlowMapper.ticketsForAttraction(
            tickets = DataRepository.loadAttractionTickets(context),
            attractionId = attraction.attractionId
        ).map { ticket ->
            AttractionTicketCardUiModel(
                ticketId = ticket.ticketId,
                title = ticket.ticketType,
                description = ticket.description,
                validityText = "${ticket.validDays} day validity",
                cancelText = if (ticket.cancellable) "Free cancellation available" else "Non-refundable",
                priceText = BookingFormatters.formatCurrency(ticket.price, ticket.currency)
            )
        }
        view.showState(
            AttractionTicketsUiState(
                hasAttraction = true,
                title = attraction.name,
                subtitle = "${tickets.size} ticket options",
                tickets = tickets
            )
        )
    }
}

class AttractionTicketDetailPresenter(
    private val view: AttractionTicketDetailContract.View
) : AttractionTicketDetailContract.Presenter {

    override fun loadData(context: Context) {
        val draft = AttractionDraftStore.snapshot()
        val attraction = DataRepository.loadAttractions(context).firstOrNull { it.attractionId == draft.selectedAttractionId }
        val ticket = DataRepository.loadAttractionTickets(context).firstOrNull { it.ticketId == draft.selectedTicketId }
        if (attraction == null || ticket == null) {
            view.showState(AttractionTicketDetailUiState())
            return
        }
        view.showState(
            AttractionTicketDetailUiState(
                hasTicket = true,
                title = ticket.ticketType,
                attractionTitle = attraction.name,
                description = ticket.description,
                validityText = "${ticket.validDays} day validity",
                cancelText = if (ticket.cancellable) "Free cancellation available" else "This ticket is non-refundable",
                priceText = BookingFormatters.formatCurrency(ticket.price, ticket.currency)
            )
        )
    }
}
