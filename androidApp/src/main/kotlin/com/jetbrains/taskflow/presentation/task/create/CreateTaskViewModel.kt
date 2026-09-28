package com.jetbrains.taskflow.presentation.task.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus
import com.jetbrains.taskflow.domain.usecase.CreateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.time.Clock
import kotlin.uuid.Uuid

class CreateTaskViewModel(
    private val createTaskUseCase: CreateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateTaskUiState())

    val uiState: StateFlow<CreateTaskUiState> = _uiState.asStateFlow()

    fun onTitleChanged(title: String) {
        _uiState.update {
            it.copy(
                title = title,
                error = null
            )
        }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update {
            it.copy(
                description = description,
                error = null
            )
        }
    }

    fun onPriorityChanged(priority: TaskPriority) {
        _uiState.update {
            it.copy(priority = priority)
        }
    }

    fun onStatusChanged(status: TaskStatus) {
        _uiState.update {
            it.copy(status = status)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun createTask(
        onSuccess: () -> Unit
    ) {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.update {
                it.copy(error = "Title is required")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
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

                _uiState.value = CreateTaskUiState()

                onSuccess()

            } catch (throwable: Throwable) {
                _uiState.update {
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