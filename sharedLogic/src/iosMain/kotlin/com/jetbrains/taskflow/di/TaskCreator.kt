package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus
import com.jetbrains.taskflow.domain.usecase.CreateTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Clock
import kotlin.uuid.Uuid

class TaskCreator(
    private val createTaskUseCase: CreateTaskUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun create(
        title: String,
        description: String?,
        priority: String,
        status: String,
        dueDate: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                val now = Clock.System.now()

                val task = Task(
                    id = Uuid.random().toString(),
                    title = title.trim(),
                    description = description
                        ?.trim()
                        ?.takeIf { it.isNotEmpty() },
                    status = TaskStatus.valueOf(status),
                    priority = TaskPriority.valueOf(priority),
                    dueDate = dueDate?.let { LocalDate.parse(it) },
                    projectId = null,
                    createdAt = now,
                    updatedAt = now
                )

                createTaskUseCase(task)
                onSuccess()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                onError(e.message ?: "Failed to create task")
            }
        }
    }
}