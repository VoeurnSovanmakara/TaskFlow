package com.jetbrains.taskflow.presentation.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jetbrains.taskflow.core.enum.TaskDeadlineStatus
import com.jetbrains.taskflow.core.util.deadlineStatus
import com.jetbrains.taskflow.domain.model.Task
import kotlinx.datetime.LocalDate

@Composable
fun DeadlineBadge(
    task: Task,
    today: LocalDate,
    modifier: Modifier = Modifier,
) {
    val status = task.deadlineStatus(today)

    val text = when (status) {
        TaskDeadlineStatus.NO_DEADLINE -> "No deadline"
        TaskDeadlineStatus.OVERDUE -> "Overdue • ${task.dueDate}"
        TaskDeadlineStatus.DUE_TODAY -> "Due today"
        TaskDeadlineStatus.UPCOMING -> "Due ${task.dueDate}"
    }

    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (status) {
        TaskDeadlineStatus.NO_DEADLINE -> scheme.surfaceContainerHigh to scheme.onSurfaceVariant
        TaskDeadlineStatus.OVERDUE -> scheme.errorContainer to scheme.onErrorContainer
        TaskDeadlineStatus.DUE_TODAY -> scheme.primaryContainer to scheme.onPrimaryContainer
        TaskDeadlineStatus.UPCOMING -> scheme.secondaryContainer to scheme.onSecondaryContainer
    }

    TaskBadge(text, container, content, modifier)
}
