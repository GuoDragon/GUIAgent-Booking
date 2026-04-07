package com.example.booking.presentation.personalinfo

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository

class PersonalInfoPresenter(
    private val view: PersonalInfoContract.View
) : PersonalInfoContract.Presenter {

    override fun loadData(context: Context) {
        val user = DataRepository.loadUsers(context).firstOrNull()

        if (user == null) {
            view.showState(PersonalInfoUiState())
            return
        }

        view.showState(
            PersonalInfoUiState(
                initials = BookingFormatters.formatInitials(user.firstName, user.lastName),
                profileFields = listOf(
                    InfoFieldUiModel(
                        title = "Name",
                        value = BookingFormatters.formatFullName(user.firstName, user.lastName)
                    ),
                    InfoFieldUiModel(
                        title = "Gender",
                        value = "Select gender"
                    ),
                    InfoFieldUiModel(
                        title = "Date of birth",
                        value = "Add your birth date"
                    ),
                    InfoFieldUiModel(
                        title = "Passport details",
                        value = "Not provided"
                    )
                ),
                contactFields = listOf(
                    InfoFieldUiModel(
                        title = "Email address",
                        value = user.email ?: "Not provided",
                        badge = "Verified"
                    ),
                    InfoFieldUiModel(
                        title = "Phone number",
                        value = user.phone ?: "Add phone number"
                    ),
                    InfoFieldUiModel(
                        title = "Address",
                        value = "Add address"
                    )
                ),
                displayFields = listOf(
                    InfoFieldUiModel(
                        title = "Display name",
                        value = BookingFormatters.formatDisplayName(user.firstName, user.lastName)
                    ),
                    InfoFieldUiModel(
                        title = "Nationality",
                        value = BookingFormatters.formatCountry(user.nationality)
                    )
                )
            )
        )
    }
}
