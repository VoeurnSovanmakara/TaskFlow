package com.jetbrains.taskflow.domain.model

import kotlin.time.Instant

data class Project(
    val id: String,
    val name: String,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)