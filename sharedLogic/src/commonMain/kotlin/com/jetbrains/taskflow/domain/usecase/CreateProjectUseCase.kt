package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.model.Project
import com.jetbrains.taskflow.domain.repository.ProjectRepository

class CreateProjectUseCase(
    private val projectRepository: ProjectRepository
) {
    suspend operator fun invoke(project: Project) {
        projectRepository.createProject(project)
    }
}