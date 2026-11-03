package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.domain.model.Project
import com.jetbrains.taskflow.domain.usecase.CreateProjectUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.uuid.Uuid

class ProjectCreator(
    private val createProject: CreateProjectUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun create(
        name: String,
        description: String?,
        onSuccess: () -> Unit,
        onError: (Throwable) -> Unit
    ) {
        scope.launch {
            try {
                val now = Clock.System.now()

                val project = Project(
                    id = Uuid.random().toString(),
                    name = name,
                    description = description?.trim()?.takeIf { it.isNotEmpty() },
                    createdAt = now,
                    updatedAt = now
                )
                createProject(project)
                onSuccess()
            } catch (error: Throwable) {
                onError(error)
            }
        }
    }
}