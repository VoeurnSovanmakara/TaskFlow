package com.jetbrains.taskflow.core.util

import kotlinx.datetime.LocalDate


fun LocalDate.toDisplayString(): String {
    return "${month.name.lowercase().replaceFirstChar { it.uppercase() }} $day, $year"
}