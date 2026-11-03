package com.jetbrains.taskflow.domain.repository

import com.jetbrains.taskflow.domain.model.Project
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {

    fun observeProjects(): Flow<List<Project>>

    suspend fun getProject(id: String): Project?

    suspend fun createProject(project: Project)

    suspend fun updateProject(project: Project)

    suspend fun deleteProject(id: String)
}