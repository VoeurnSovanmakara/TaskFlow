package com.jetbrains.taskflow.data.database.mapper

import com.jetbrains.taskflow.data.database.entity.ProjectEntity
import com.jetbrains.taskflow.domain.model.Project
import kotlin.time.Instant

fun ProjectEntity.toDomain(): Project = Project(
    id = id,
    name = name,
    description = description,
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    updatedAt = Instant.fromEpochMilliseconds(updatedAt)
)

fun Project.toEntity(): ProjectEntity = ProjectEntity(
    id = id,
    name = name,
    description = description,
    createdAt = createdAt.toEpochMilliseconds(),
    updatedAt = updatedAt.toEpochMilliseconds()
)