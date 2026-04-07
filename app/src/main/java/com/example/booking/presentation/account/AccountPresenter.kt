package com.example.booking.presentation.account

import android.content.Context
import com.example.booking.common.format.BookingFormatters
import com.example.booking.data.DataRepository

class AccountPresenter(
    private val view: AccountContract.View
) : AccountContract.Presenter {

    override fun loadData(context: Context) {
        val user = DataRepository.loadUsers(context).firstOrNull()

        if (user == null) {
            view.showState(AccountUiState())
            return
        }

        view.showState(
            AccountUiState(
                firstName = user.firstName.ifBlank { "Guest" },
                fullName = BookingFormatters.formatFullName(user.firstName, user.lastName),
                initials = BookingFormatters.formatInitials(user.firstName, user.lastName),
                email = user.email.orEmpty()
            )
        )
    }
}
