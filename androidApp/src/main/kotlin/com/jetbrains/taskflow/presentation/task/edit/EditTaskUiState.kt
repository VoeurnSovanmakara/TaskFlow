package com.jetbrains.taskflow.presentation.task.edit

import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus

data class EditTaskUiState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null
)