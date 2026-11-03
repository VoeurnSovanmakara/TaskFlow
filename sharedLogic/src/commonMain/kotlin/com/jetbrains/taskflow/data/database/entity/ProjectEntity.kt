package com.jetbrains.taskflow.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey
    val id: String,

    val name: String,

    val description: String?,

    val createdAt: Long,

    val updatedAt: Long
)