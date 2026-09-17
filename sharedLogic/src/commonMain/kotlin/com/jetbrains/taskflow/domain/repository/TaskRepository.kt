package com.jetbrains.taskflow.domain.repository

import com.jetbrains.taskflow.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>

    suspend fun getTask(id: String): Task?

    suspend fun createTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(id: String)
}