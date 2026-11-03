package com.jetbrains.taskflow.domain.usecase

import com.jetbrains.taskflow.domain.model.Project
import com.jetbrains.taskflow.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow

class ObserveProjectsUseCase(
    private val projectRepository: ProjectRepository
) {
    operator fun invoke(): Flow<List<Project>> {
        return projectRepository.observeProjects()
    }
}