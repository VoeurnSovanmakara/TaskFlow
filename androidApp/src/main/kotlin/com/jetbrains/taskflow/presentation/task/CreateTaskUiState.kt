package com.jetbrains.taskflow.presentation.task

import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus

data class CreateTaskUiState(
    val title: String = "",
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isSaving: Boolean = false,
    val error: String? = null
)