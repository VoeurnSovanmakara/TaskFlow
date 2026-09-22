package com.jetbrains.taskflow.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus
import com.jetbrains.taskflow.domain.usecase.CreateTaskUseCase
import com.jetbrains.taskflow.domain.usecase.ObserveTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import kotlin.time.Clock
import kotlin.time.Instant

class TaskViewModel(
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()
    private val _createTaskUiState = MutableStateFlow(CreateTaskUiState())
    val createTaskUiState: StateFlow<CreateTaskUiState> = _createTaskUiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            observeTasksUseCase()
                .onStart {
                    _uiState.update {
                        it.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Failed to load tasks"
                        )
                    }
                }
                .collect { tasks ->
                    println("ViewModel received ${tasks.size} tasks")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            tasks = tasks,
                            error = null
                        )
                    }
                }
        }
    }

    fun onTitleChanged(title: String) {
        _createTaskUiState.update {
            it.copy(
                title = title,
                error = null
            )
        }
    }

    fun onDescriptionChanged(description: String) {
        _createTaskUiState.update {
            it.copy(
                description = description,
                error = null
            )
        }
    }

    fun onPriorityChanged(priority: TaskPriority) {
        _createTaskUiState.update {
            it.copy(priority = priority)
        }
    }

    fun onStatusChanged(status: TaskStatus) {
        _createTaskUiState.update {
            it.copy(status = status)
        }
    }

    fun createTask(
        onSuccess: () -> Unit
    ) {
        val state = _createTaskUiState.value

        if (state.title.isBlank()) {
            _createTaskUiState.update {
                it.copy(error = "Title is required")
            }
            return
        }

        viewModelScope.launch {
            _createTaskUiState.update {
                it.copy(
                    isSaving = true,
                    error = null
                )
            }

            try {
                val now = Clock.System.now()

                val task = Task(
                    id = Uuid.random().toString(),
                    title = state.title.trim(),
                    description = state.description
                        .trim()
                        .ifBlank { null },
                    status = state.status,
                    priority = state.priority,
                    dueDate = null,
                    dueTime = null,
                    projectId = null,
                    createdAt = now,
                    updatedAt = now
                )

                createTaskUseCase(task)

                _createTaskUiState.value = CreateTaskUiState()

                onSuccess()

            } catch (throwable: Throwable) {
                _createTaskUiState.update {
                    it.copy(
                        isSaving = false,
                        error = throwable.message
                            ?: "Failed to create task"
                    )
                }
            }
        }
    }
}