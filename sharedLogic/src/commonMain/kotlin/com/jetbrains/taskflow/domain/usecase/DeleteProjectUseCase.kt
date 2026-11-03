package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.repository.ProjectRepository

class DeleteProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(id: String) {
        projectRepository.deleteProject(id)
    }
}