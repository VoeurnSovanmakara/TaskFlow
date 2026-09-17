package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class ObserveTasksUseCase(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(): Flow<List<Task>> {
        return taskRepository.observeTasks()
    }
}