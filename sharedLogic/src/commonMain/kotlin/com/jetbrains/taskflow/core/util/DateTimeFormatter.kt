package com.jetbrains.taskflow.core.util

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant


fun LocalDate.toDisplayString(): String {
    return "${month.name.lowercase().replaceFirstChar { it.uppercase() }} $day, $year"
}

fun dateTimeFormatter(instant: Instant): String {
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val month = localDateTime.month.name
        .lowercase()
        .replaceFirstChar { it.uppercase() }

    val day = localDateTime.day
    val year = localDateTime.year

    val hour = localDateTime.hour
    val minute = localDateTime.minute

    val amPm = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    return "$month $day, $year, ${displayHour}:${minute.toString().padStart(2, '0')} $amPm"
}

fun formatTimeAgo(
    instant: Instant,
    now: Instant = Clock.System.now()
): String {
    val duration = now - instant

    return when {
        duration < 1.minutes -> "Just now"

        duration < 1.hours -> {
            val minutes = duration.inWholeMinutes
            "${minutes}m ago"
        }

        duration < 24.hours -> {
            val hours = duration.inWholeHours
            "${hours}h ago"
        }

        duration < 7.days -> {
            val days = duration.inWholeDays
            "${days}d ago"
        }

        duration < 30.days -> {
            val weeks = duration.inWholeDays / 7
            "${weeks}w ago"
        }

        duration < 365.days -> {
            val months = duration.inWholeDays / 30
            "${months}mo ago"
        }

        else -> {
            val years = duration.inWholeDays / 365
            "${years}y ago"
        }
    }
}