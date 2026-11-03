package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.model.Project
import com.jetbrains.taskflow.domain.repository.ProjectRepository

class GetProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(id: String): Project? {
        return projectRepository.getProject(id)
    }
}