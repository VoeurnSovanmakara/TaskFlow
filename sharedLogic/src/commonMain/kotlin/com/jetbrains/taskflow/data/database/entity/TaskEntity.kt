package com.jetbrains.taskflow.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,

    val title: String,

    val description: String?,

    val status: String,

    val priority: String,

    val dueDate: String?,

    val projectId: String?,

    val createdAt: Long,

    val updatedAt: Long
)