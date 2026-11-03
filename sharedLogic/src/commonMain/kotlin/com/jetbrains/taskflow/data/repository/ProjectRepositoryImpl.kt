package com.jetbrains.taskflow.data.repository

import com.jetbrains.taskflow.data.database.dao.ProjectDao
import com.jetbrains.taskflow.data.database.mapper.toDomain
import com.jetbrains.taskflow.data.database.mapper.toEntity
import com.jetbrains.taskflow.domain.model.Project
import com.jetbrains.taskflow.domain.repository.ProjectRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProjectRepositoryImpl(
    private val projectDao: ProjectDao
) : ProjectRepository {

    override fun observeProjects(): Flow<List<Project>> {
        return projectDao.observeProjects()
            .map { projects -> projects.map { it.toDomain() } }
    }

    override suspend fun getProject(id: String): Project? {
        return projectDao.getProject(id)?.toDomain()
    }

    override suspend fun createProject(project: Project) {
        projectDao.insertProject(project.toEntity())
    }

    override suspend fun updateProject(project: Project) {
        projectDao.updateProject(project.toEntity())
    }

    override suspend fun deleteProject(id: String) {
        projectDao.deleteProject(id)
    }
}