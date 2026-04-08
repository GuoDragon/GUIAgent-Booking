package com.example.booking.presentation.flights.booking

import android.content.Context
import com.example.booking.presentation.flights.common.FlightFareOption
import com.example.booking.presentation.flights.common.FlightFlexibleTicketOption

interface FlightFareContract {
    interface View {
        fun showState(state: FlightFareUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applyFare(option: FlightFareOption)
    }
}

data class FlightFareUiState(
    val title: String = "",
    val totalPriceText: String = "",
    val options: List<FlightFareOptionUiModel> = emptyList(),
    val canContinue: Boolean = false
)

data class FlightFareOptionUiModel(
    val option: FlightFareOption,
    val title: String,
    val subtitle: String,
    val selected: Boolean
)

interface FlightLuggageContract {
    interface View {
        fun showState(state: FlightLuggageUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun applySelection(noAdditionalBaggage: Boolean, flexibleOption: FlightFlexibleTicketOption)
    }
}

data class FlightLuggageUiState(
    val includedBagText: String = "",
    val mealChoice: String = "No preference",
    val totalPriceText: String = "",
    val flexibleOptions: List<FlightFlexibleOptionUiModel> = emptyList(),
    val canContinue: Boolean = false
)

data class FlightFlexibleOptionUiModel(
    val option: FlightFlexibleTicketOption,
    val title: String,
    val priceText: String,
    val selected: Boolean
)

interface FlightMealChoiceContract {
    interface View {
        fun showState(state: FlightMealChoiceUiState)
    }

    interface Presenter {
        fun loadData()
        fun selectMeal(option: String)
    }
}

data class FlightMealChoiceUiState(
    val options: List<String> = emptyList(),
    val selectedMeal: String = "No preference"
)

interface FlightSeatContract {
    interface View {
        fun showState(state: FlightSeatUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class FlightSeatUiState(
    val routeTitle: String = "",
    val seatRows: List<String> = emptyList(),
    val totalPriceText: String = "",
    val canContinue: Boolean = false
)

interface FlightTravelerDetailsContract {
    interface View {
        fun showState(state: FlightTravelerDetailsUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun saveDetails(firstName: String, lastName: String, gender: String)
    }
}

data class FlightTravelerDetailsUiState(
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val totalPriceText: String = ""
)

interface FlightTravelerContactContract {
    interface View {
        fun showState(state: FlightTravelerContactUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
        fun completeBooking(
            context: Context,
            email: String,
            phoneCountryCode: String,
            phoneNumber: String
        ): String?
    }
}

data class FlightTravelerContactUiState(
    val email: String = "",
    val phoneCountryCode: String = "+1",
    val phoneNumber: String = "",
    val totalPriceText: String = "",
    val canComplete: Boolean = false
)

interface FlightBookingSuccessContract {
    interface View {
        fun showState(state: FlightBookingSuccessUiState)
    }

    interface Presenter {
        fun loadData(context: Context, orderId: String)
    }
}

data class FlightBookingSuccessUiState(
    val hasOrder: Boolean = false,
    val title: String = "",
    val orderId: String = "",
    val itemName: String = "",
    val dateLabel: String = "",
    val guestLabel: String = "",
    val totalPriceText: String = "",
    val note: String = ""
)
