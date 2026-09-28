package com.jetbrains.taskflow.core.extention

import com.jetbrains.taskflow.core.enum.TaskFilter
import com.jetbrains.taskflow.core.enum.TaskSort
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus

fun List<Task>.filterBy(filter: TaskFilter): List<Task> {
    return when (filter) {
        TaskFilter.ALL -> this

        TaskFilter.TODO -> {
            filter { it.status == TaskStatus.TODO }
        }

        TaskFilter.IN_PROGRESS -> {
            filter { it.status == TaskStatus.IN_PROGRESS }
        }

        TaskFilter.COMPLETED -> {
            filter { it.status == TaskStatus.COMPLETED }
        }
    }
}


fun List<Task>.sortBy(sort: TaskSort): List<Task> {
    return when (sort) {

        TaskSort.CREATED_DATE -> {
            sortedByDescending { it.createdAt }
        }

        TaskSort.DUE_DATE -> {
            sortedBy { it.dueDate }
        }

        TaskSort.PRIORITY -> {
            sortedByDescending { priorityOrder(it.priority) }
        }

        TaskSort.TITLE -> {
            sortedBy { it.title.lowercase() }
        }
    }
}

private fun priorityOrder(priority: TaskPriority): Int {
    return when (priority) {
        TaskPriority.HIGH -> 3
        TaskPriority.MEDIUM -> 2
        TaskPriority.LOW -> 1
    }
}