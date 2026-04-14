package com.example.booking.presentation.personalinfo

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository
import com.example.booking.model.AccountActionSignal
import com.example.booking.model.AccountActionTypes
import java.util.UUID

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
                        key = PersonalInfoFieldKey.Name,
                        title = "Name",
                        value = BookingFormatters.formatFullName(user.firstName, user.lastName),
                        editable = true
                    ),
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.Gender,
                        title = "Gender",
                        value = "Select gender"
                    ),
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.DateOfBirth,
                        title = "Date of birth",
                        value = "Add your birth date"
                    ),
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.PassportDetails,
                        title = "Passport details",
                        value = "Not provided"
                    )
                ),
                contactFields = listOf(
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.EmailAddress,
                        title = "Email address",
                        value = user.email ?: "Not provided",
                        badge = "Verified"
                    ),
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.PhoneNumber,
                        title = "Phone number",
                        value = user.phone ?: "Add phone number",
                        editable = true
                    ),
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.Address,
                        title = "Address",
                        value = "Add address"
                    )
                ),
                displayFields = listOf(
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.DisplayName,
                        title = "Display name",
                        value = BookingFormatters.formatDisplayName(user.firstName, user.lastName)
                    ),
                    InfoFieldUiModel(
                        key = PersonalInfoFieldKey.Nationality,
                        title = "Nationality",
                        value = BookingFormatters.formatCountry(user.nationality)
                    )
                )
            )
        )
    }

    override fun updateName(context: Context, firstName: String, lastName: String): Boolean {
        val existing = DataRepository.loadUsers(context).firstOrNull() ?: return false
        val normalizedFirstName = firstName.trim()
        val normalizedLastName = lastName.trim()
        if (normalizedFirstName.isBlank() || normalizedLastName.isBlank()) {
            return false
        }

        val updated = DataRepository.updatePrimaryUserProfile(
            context = context,
            firstName = normalizedFirstName,
            lastName = normalizedLastName,
            phone = existing.phone.orEmpty()
        ) ?: return false

        DataRepository.appendAccountActionSignal(
            context = context,
            signal = AccountActionSignal(
                signalId = "account_action_${UUID.randomUUID()}",
                userId = updated.userId,
                actionType = AccountActionTypes.PROFILE_UPDATED,
                occurredAt = System.currentTimeMillis(),
                displayMessage = "Updated profile name to ${BookingFormatters.formatFullName(updated.firstName, updated.lastName)}",
                extra = mapOf("field" to "name")
            )
        )
        loadData(context)
        return true
    }

    override fun updatePhone(
        context: Context,
        phoneCountryCode: String,
        phoneNumber: String
    ): Boolean {
        val existing = DataRepository.loadUsers(context).firstOrNull() ?: return false
        val normalizedPhone = normalizePhone(phoneCountryCode = phoneCountryCode, phoneNumber = phoneNumber)
        if (normalizedPhone.isBlank()) {
            return false
        }

        val updated = DataRepository.updatePrimaryUserProfile(
            context = context,
            firstName = existing.firstName,
            lastName = existing.lastName,
            phone = normalizedPhone
        ) ?: return false

        DataRepository.appendAccountActionSignal(
            context = context,
            signal = AccountActionSignal(
                signalId = "account_action_${UUID.randomUUID()}",
                userId = updated.userId,
                actionType = AccountActionTypes.PROFILE_UPDATED,
                occurredAt = System.currentTimeMillis(),
                displayMessage = "Updated profile phone to $normalizedPhone",
                extra = mapOf("field" to "phone")
            )
        )
        loadData(context)
        return true
    }

    private fun normalizePhone(
        phoneCountryCode: String,
        phoneNumber: String
    ): String {
        val normalizedCountryCode = phoneCountryCode.trim().ifBlank { "+1" }
        val normalizedPhoneNumber = phoneNumber.trim()
        if (normalizedPhoneNumber.isBlank()) {
            return ""
        }
        return "$normalizedCountryCode-$normalizedPhoneNumber"
    }
}
