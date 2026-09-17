package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.repository.TaskRepository

class CreateTaskUseCase(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        taskRepository.createTask(task)
    }
}