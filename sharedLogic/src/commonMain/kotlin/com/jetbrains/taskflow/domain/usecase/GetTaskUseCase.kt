package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.repository.TaskRepository

class GetTaskUseCase(
    private val taskRepository: TaskRepository
) {

    suspend operator fun invoke(id: String): Task? {
        return taskRepository.getTask(id)
    }
}