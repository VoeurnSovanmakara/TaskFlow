package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus
import com.jetbrains.taskflow.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.Instant

class TaskUpdater(
    private val updateTaskUseCase: UpdateTaskUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun update(
        task: Task,
        title: String,
        description: String?,
        priority: String,
        status: String,
        dueDate: String?,
        reminderAt: String?,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                val updatedTask = task.copy(
                    title = title.trim(),
                    description = description
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() },
                    priority = TaskPriority.valueOf(priority),
                    status = TaskStatus.valueOf(status),
                    dueDate = dueDate
                        ?.takeIf { it.isNotBlank() }
                        ?.let { LocalDate.parse(it) },
                    reminderAt = reminderAt?.let { Instant.parse(it) },
                    updatedAt = Clock.System.now()
                )

                updateTaskUseCase(updatedTask)
                onSuccess(updatedTask.id)
            } catch (e: Exception) {
                onError(e.message ?: "Failed to update task.")
            }
        }
    }
}