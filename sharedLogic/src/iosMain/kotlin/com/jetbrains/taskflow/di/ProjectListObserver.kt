package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.domain.model.Project
import com.jetbrains.taskflow.domain.usecase.ObserveProjectsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ProjectListObserver(
    private val observeProjects: ObserveProjectsUseCase
) {
    private val scope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private var observeJob: Job? = null

    fun start(
        onProjects: (List<Project>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        observeJob?.cancel()

        observeJob = scope.launch {
            observeProjects()
                .catch { error -> onError(error) }
                .collect { projects -> onProjects(projects) }
        }
    }

    fun stop() {
        observeJob?.cancel()
        observeJob = null
    }
}