package com.example.booking.presentation.carrentals.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.booking.common.format.BookingFormatters
import com.example.booking.presentation.stays.common.StayFooterBar
import com.example.booking.presentation.stays.common.StaySummaryInfoCard
import com.example.booking.ui.components.BookingFloatingBackButton
import com.example.booking.ui.theme.BookingBlue
import com.example.booking.ui.theme.BookingTextPrimary
import com.example.booking.ui.theme.BookingTextSecondary
import com.example.booking.ui.theme.BookingWhite
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters

@Composable
fun CarRentalDateScreen(
    onBackClick: () -> Unit,
    onApplyClick: () -> Unit
) {
    var uiState by remember {
        mutableStateOf(
            CarRentalDateUiState(
                pickupDateTime = LocalDateTime.now(),
                returnDateTime = LocalDateTime.now().plusDays(2),
                calendarMonths = emptyList()
            )
        )
    }
    var pickupDateText by rememberSaveable { mutableStateOf(LocalDate.now().plusDays(14).toString()) }
    var returnDateText by rememberSaveable { mutableStateOf(LocalDate.now().plusDays(16).toString()) }
    var selectingReturnDate by rememberSaveable { mutableStateOf(false) }

    val view = remember {
        object : CarRentalDateContract.View {
            override fun showState(state: CarRentalDateUiState) {
                uiState = state
                pickupDateText = state.pickupDateTime.toLocalDate().toString()
                returnDateText = state.returnDateTime.toLocalDate().toString()
                selectingReturnDate = false
            }
        }
    }
    val presenter = remember(view) { CarRentalDatePresenter(view) }

    LaunchedEffect(presenter) {
        presenter.loadData()
    }

    val pickupDate = remember(pickupDateText) { LocalDate.parse(pickupDateText) }
    val returnDate = remember(returnDateText) { LocalDate.parse(returnDateText) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BookingWhite)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(color = BookingWhite) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)) {
                    StaySummaryInfoCard(
                        title = "Select dates",
                        description = "${BookingFormatters.formatLongLocalDate(pickupDate)}\n${BookingFormatters.formatLongLocalDate(returnDate)}"
                    )
                }
            }
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                items(uiState.calendarMonths.size) { index ->
                    CarRentalCalendarMonth(
                        monthStart = uiState.calendarMonths[index],
                        selectedPickup = pickupDate,
                        selectedReturn = returnDate,
                        onDateSelected = { date ->
                            if (!selectingReturnDate) {
                                pickupDateText = date.toString()
                                returnDateText = date.toString()
                                selectingReturnDate = true
                            } else if (date <= pickupDate) {
                                pickupDateText = date.toString()
                                returnDateText = date.toString()
                                selectingReturnDate = true
                            } else {
                                returnDateText = date.toString()
                                selectingReturnDate = false
                            }
                        }
                    )
                }
            }
        }
        StayFooterBar(
            priceLine = "${BookingFormatters.formatShortLocalDate(pickupDate)} - ${BookingFormatters.formatShortLocalDate(returnDate)}",
            subLine = "Pickup at 10:00 | Return at 10:00",
            buttonText = "Select dates",
            modifier = Modifier.align(Alignment.BottomCenter),
            onClick = {
                presenter.applyDates(
                    pickupDate.atTime(uiState.pickupDateTime.toLocalTime()),
                    returnDate.atTime(uiState.returnDateTime.toLocalTime())
                )
                onApplyClick()
            }
        )
        BookingFloatingBackButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.TopStart),
            tint = BookingBlue
        )
    }
}

@Composable
private fun CarRentalCalendarMonth(
    monthStart: LocalDate,
    selectedPickup: LocalDate,
    selectedReturn: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val yearMonth = YearMonth.from(monthStart)
    val firstGridDate = monthStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    val dates = (0 until 35).map { firstGridDate.plusDays(it.toLong()) }

    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = BookingFormatters.formatMonthYear(monthStart),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = BookingTextPrimary
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                Text(
                    text = day,
                    color = BookingTextSecondary,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
        dates.chunked(7).forEach { week ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                week.forEach { date ->
                    val inMonth = date.month == yearMonth.month
                    val selected = date == selectedPickup || date == selectedReturn
                    val inRange = date > selectedPickup && date < selectedReturn
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                            .height(42.dp)
                            .clickable(enabled = inMonth) { onDateSelected(date) },
                        shape = RoundedCornerShape(12.dp),
                        color = when {
                            selected -> BookingBlue
                            inRange -> BookingBlue.copy(alpha = 0.12f)
                            else -> BookingWhite
                        }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = date.dayOfMonth.toString(),
                                color = when {
                                    !inMonth -> BookingTextSecondary.copy(alpha = 0.3f)
                                    selected -> BookingWhite
                                    else -> BookingTextPrimary
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
