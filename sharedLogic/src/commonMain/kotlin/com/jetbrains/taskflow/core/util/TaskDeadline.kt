package com.jetbrains.taskflow.core.util

import com.jetbrains.taskflow.core.enum.TaskDeadlineStatus
import com.jetbrains.taskflow.domain.model.Task
import kotlinx.datetime.LocalDate

fun Task.deadlineStatus(today: LocalDate): TaskDeadlineStatus {
    val dueDate = dueDate ?: return TaskDeadlineStatus.NO_DEADLINE

    return when {
        dueDate < today -> TaskDeadlineStatus.OVERDUE
        dueDate == today -> TaskDeadlineStatus.DUE_TODAY
        else -> TaskDeadlineStatus.UPCOMING
    }
}