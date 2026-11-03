package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.usecase.ObserveTasksUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TaskListObserver (
    private val observeTasks: ObserveTasksUseCase
){
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var observationJob: Job? = null

    fun start(
        onTasks: (List<Task>) -> Unit,
        onError: (String) -> Unit
    ) {
        observationJob?.cancel()

        observationJob = scope.launch {
            try {
                observeTasks().collect { tasks ->
                    onTasks(tasks)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onError(e.message ?: "Failed to load tasks")
            }
        }
    }

    fun stop() {
        observationJob?.cancel()
        observationJob = null
    }
}