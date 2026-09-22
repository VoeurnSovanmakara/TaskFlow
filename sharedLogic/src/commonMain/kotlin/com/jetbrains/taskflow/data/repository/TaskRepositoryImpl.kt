package com.jetbrains.taskflow.data.repository

import com.jetbrains.taskflow.data.database.dao.TaskDao
import com.jetbrains.taskflow.data.database.mapper.toDomain
import com.jetbrains.taskflow.data.database.mapper.toEntity
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> {
        return taskDao
            .observeTasks()
            .map { entities ->
                println("Room emitted ${entities.size} tasks")
                entities.map { it.toDomain() }
            }
    }

    override suspend fun getTask(id: String): Task? {
        return taskDao
            .getTask(id)
            ?.toDomain()
    }

    override suspend fun createTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun deleteTask(id: String) {
        taskDao.deleteTask(id)
    }
}