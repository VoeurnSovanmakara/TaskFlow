package com.jetbrains.taskflow.presentation.task.list

import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.core.enum.TaskFilter
import com.jetbrains.taskflow.core.enum.TaskSort

data class TaskListUiState(
    val isLoading: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val error: String? = null,
    val selectedFilter: TaskFilter = TaskFilter.ALL,
    val selectedSort: TaskSort = TaskSort.CREATED_DATE
)