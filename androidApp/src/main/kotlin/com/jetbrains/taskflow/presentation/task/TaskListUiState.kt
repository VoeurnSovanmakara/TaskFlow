package com.jetbrains.taskflow.presentation.task

import com.jetbrains.taskflow.domain.model.Task

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null,
)