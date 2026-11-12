package com.jetbrains.taskflow.presentation.task.create

import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val dueDate: LocalDate? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val reminderAt: Instant? = null
)