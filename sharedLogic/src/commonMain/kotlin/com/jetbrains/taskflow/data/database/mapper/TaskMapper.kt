package com.jetbrains.taskflow.data.database.mapper

import com.jetbrains.taskflow.data.database.entity.TaskEntity
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        status = TaskStatus.valueOf(status),
        priority = TaskPriority.valueOf(priority),
        dueDate = dueDate?.let(LocalDate::parse),
        dueTime = dueTime?.let(LocalTime::parse),
        projectId = projectId,
        createdAt = Instant.fromEpochMilliseconds(createdAt),
        updatedAt = Instant.fromEpochMilliseconds(updatedAt)
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        status = status.name,
        priority = priority.name,
        dueDate = dueDate?.toString(),
        dueTime = dueTime?.toString(),
        projectId = projectId,
        createdAt = createdAt.toEpochMilliseconds(),
        updatedAt = updatedAt.toEpochMilliseconds()
    )
}