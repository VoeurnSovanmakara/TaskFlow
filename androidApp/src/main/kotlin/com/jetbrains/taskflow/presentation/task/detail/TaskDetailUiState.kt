package com.jetbrains.taskflow.presentation.task.detail

import com.jetbrains.taskflow.domain.model.Task

data class TaskDetailUiState(
    val isLoading: Boolean = false,
    val task: Task? = null,
    val error: String? = null
)