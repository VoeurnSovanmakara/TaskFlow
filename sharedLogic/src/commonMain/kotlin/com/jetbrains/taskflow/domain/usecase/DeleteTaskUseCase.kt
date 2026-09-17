package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.repository.TaskRepository

class DeleteTaskUseCase(
    private val taskRepository: TaskRepository
) {

    suspend operator fun invoke(id: String) {
        taskRepository.deleteTask(id)
    }
}