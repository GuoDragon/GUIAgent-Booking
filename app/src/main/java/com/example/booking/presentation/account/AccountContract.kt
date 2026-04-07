package com.example.booking.presentation.account

import android.content.Context

interface AccountContract {
    interface View {
        fun showState(state: AccountUiState)
    }

    interface Presenter {
        fun loadData(context: Context)
    }
}

data class AccountUiState(
    val firstName: String = "Guest",
    val fullName: String = "Guest",
    val initials: String = "BK",
    val geniusLevelLabel: String = "Genius Level 1",
    val email: String = ""
)
