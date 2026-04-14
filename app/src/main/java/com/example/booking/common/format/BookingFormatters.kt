package com.example.booking.common.format

import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
import java.util.Currency
import java.util.Locale

object BookingFormatters {

    private val appZoneId: ZoneId = ZoneId.of("Asia/Shanghai")
    private val shortDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
    private val fullDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.ENGLISH)
    private val birthDateInputFormatter = DateTimeFormatter.ISO_LOCAL_DATE
    private val birthDateOutputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH)
    private val localDateFormatter = DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH)
    private val localDateLongFormatter = DateTimeFormatter.ofPattern("EEE, MMM d, yyyy", Locale.ENGLISH)
    private val localDateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d · HH:mm", Locale.ENGLISH)
    private val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH)
    private val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    private val weekdayFormatter = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH)

    fun formatCurrency(amount: Double, currencyCode: String): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)
        formatter.currency = Currency.getInstance(currencyCode)
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }

    fun formatShortDate(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(appZoneId)
            .toLocalDate()
            .format(shortDateFormatter)
    }

    fun formatFullDate(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(appZoneId)
            .toLocalDate()
            .format(fullDateFormatter)
    }

    fun formatDateRange(startMillis: Long, endMillis: Long?): String {
        return if (endMillis != null) {
            "${formatShortDate(startMillis)} - ${formatShortDate(endMillis)}"
        } else {
            formatFullDate(startMillis)
        }
    }

    fun formatTravelerBirthDate(rawDate: String?): String {
        if (rawDate.isNullOrBlank()) {
            return "Not provided"
        }
        return runCatching {
            LocalDate.parse(rawDate, birthDateInputFormatter).format(birthDateOutputFormatter)
        }.getOrDefault(rawDate)
    }

    fun formatFullName(firstName: String, lastName: String): String {
        return listOf(firstName, lastName)
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { "Guest" }
    }

    fun formatDisplayName(firstName: String, lastName: String): String {
        val lastInitial = lastName.firstOrNull()?.uppercaseChar()?.toString().orEmpty()
        return if (firstName.isBlank() && lastInitial.isBlank()) {
            "Guest"
        } else {
            listOfNotNull(
                firstName.takeIf { it.isNotBlank() },
                lastInitial.takeIf { it.isNotBlank() }?.plus(".")
            ).joinToString(" ")
        }
    }

    fun formatInitials(firstName: String, lastName: String): String {
        val letters = buildString {
            firstName.firstOrNull()?.let { append(it.uppercaseChar()) }
            lastName.firstOrNull()?.let { append(it.uppercaseChar()) }
        }
        return letters.ifBlank { "BK" }
    }

    fun formatCountry(value: String?): String {
        if (value.isNullOrBlank()) {
            return "Not provided"
        }
        return if (value.length == 2) {
            Locale("", value).getDisplayCountry(Locale.ENGLISH).ifBlank { value }
        } else {
            value
        }
    }

    fun humanizeEnum(value: String): String {
        return value.lowercase(Locale.ENGLISH)
            .split("_")
            .joinToString(" ") { token ->
                token.replaceFirstChar { char -> char.titlecase(Locale.ENGLISH) }
            }
    }

    fun formatLocalDate(date: LocalDate): String = date.format(localDateFormatter)

    fun formatLongLocalDate(date: LocalDate): String = date.format(localDateLongFormatter)

    fun formatShortLocalDate(date: LocalDate): String = date.format(shortDateFormatter)

    fun formatMonthYear(date: LocalDate): String = date.format(monthYearFormatter)

    fun formatWeekday(date: LocalDate): String = date.format(weekdayFormatter)

    fun formatTime(epochMillis: Long): String {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(appZoneId)
            .toLocalTime()
            .format(localTimeFormatter)
    }

    fun formatTime(dateTime: LocalDateTime): String = dateTime.format(localTimeFormatter)

    fun formatLocalDateTime(dateTime: LocalDateTime): String = dateTime.format(localDateTimeFormatter)

    fun formatStayDateRange(checkIn: LocalDate, checkOut: LocalDate): String {
        return "${formatShortLocalDate(checkIn)} - ${formatShortLocalDate(checkOut)}"
    }

    fun formatSearchDateSummary(checkIn: LocalDate, checkOut: LocalDate): String {
        return "${formatLocalDate(checkIn)} - ${formatLocalDate(checkOut)}"
    }

    fun formatGuestSummary(rooms: Int, adults: Int, children: Int): String {
        val roomLabel = "$rooms room" + if (rooms == 1) "" else "s"
        val adultLabel = "$adults adult" + if (adults == 1) "" else "s"
        val childLabel = if (children == 1) "$children child" else "$children children"
        return "$roomLabel | $adultLabel | $childLabel"
    }

    fun formatNightCount(checkIn: LocalDate, checkOut: LocalDate): String {
        val nights = ChronoUnit.DAYS.between(checkIn, checkOut).coerceAtLeast(1)
        return "$nights night" + if (nights == 1L) "" else "s"
    }

    fun formatDurationMinutes(minutes: Int): String {
        val hours = minutes / 60
        val remainder = minutes % 60
        return if (remainder == 0) {
            "${hours}h"
        } else {
            "${hours}h ${remainder}m"
        }
    }

    fun localDateToEpochMillis(date: LocalDate): Long {
        return date.atStartOfDay(appZoneId).toInstant().toEpochMilli()
    }

    fun localDateTimeToEpochMillis(dateTime: LocalDateTime): Long {
        return dateTime.atZone(appZoneId).toInstant().toEpochMilli()
    }

    fun epochMillisToLocalDate(epochMillis: Long): LocalDate {
        return Instant.ofEpochMilli(epochMillis)
            .atZone(appZoneId)
            .toLocalDate()
    }

    fun parsePhoneParts(phone: String?): Pair<String, String> {
        if (phone.isNullOrBlank()) {
            return "+1" to ""
        }

        val trimmed = phone.trim()
        val parts = trimmed.split("-", limit = 2)
        return if (parts.size == 2 && parts.first().startsWith("+")) {
            parts.first() to parts.last()
        } else if (trimmed.startsWith("+")) {
            val prefix = trimmed.takeWhile { it == '+' || it.isDigit() }
            prefix to trimmed.removePrefix(prefix).trimStart('-', ' ')
        } else {
            "+1" to trimmed
        }
    }
}
