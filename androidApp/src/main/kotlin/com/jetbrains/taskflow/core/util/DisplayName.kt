package com.jetbrains.taskflow.core.util

import com.jetbrains.taskflow.core.enum.TaskFilter
import com.jetbrains.taskflow.core.enum.TaskSort

fun TaskFilter.displayName(): String {
    return when (this) {
        TaskFilter.ALL -> "All"
        TaskFilter.TODO -> "Todo"
        TaskFilter.IN_PROGRESS -> "In Progress"
        TaskFilter.COMPLETED -> "Completed"
    }
}

fun TaskSort.displayName(): String {
    return when (this) {
        TaskSort.CREATED_DATE -> "Created date"
        TaskSort.DUE_DATE -> "Due date"
        TaskSort.PRIORITY -> "Priority"
        TaskSort.TITLE -> "Title"
    }
}