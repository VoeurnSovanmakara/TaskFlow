package com.jetbrains.taskflow.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.Instant

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val status: TaskStatus,
    val priority: TaskPriority,
    val dueDate: LocalDate?,
    val dueTime: LocalTime?,
    val projectId: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)