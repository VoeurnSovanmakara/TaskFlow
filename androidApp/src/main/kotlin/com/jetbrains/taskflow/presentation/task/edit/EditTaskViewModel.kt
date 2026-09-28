package com.jetbrains.taskflow.presentation.task.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus
import com.jetbrains.taskflow.domain.usecase.GetTaskUseCase
import com.jetbrains.taskflow.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class EditTaskViewModel(
    private val getTaskUseCase: GetTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditTaskUiState())

    val uiState: StateFlow<EditTaskUiState> =
        _uiState.asStateFlow()

    fun loadTask(taskId: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            try {
                val task = getTaskUseCase(taskId)

                if (task == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Task not found"
                        )
                    }
                    return@launch
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        title = task.title,
                        description = task.description.orEmpty(),
                        priority = task.priority,
                        status = task.status,
                        error = null
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load task"
                    )
                }
            }
        }
    }

    fun onTitleChanged(title: String) {
        _uiState.update {
            it.copy(title = title)
        }
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update {
            it.copy(description = description)
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

    fun updateTask(
        taskId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {

            _uiState.update {
                it.copy(
                    isSaving = true,
                    error = null
                )
            }

            try {
                val existingTask = getTaskUseCase(taskId)

                if (existingTask == null) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = "Task not found"
                        )
                    }
                    return@launch
                }

                val updatedTask = existingTask.copy(
                    title = _uiState.value.title,
                    description = _uiState.value.description
                        .ifBlank { null },
                    status = _uiState.value.status,
                    priority = _uiState.value.priority,
                    updatedAt = Clock.System.now()
                )

                updateTaskUseCase(updatedTask)

                _uiState.update {
                    it.copy(isSaving = false)
                }

                onSuccess()

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Failed to update task"
                    )
                }
            }
        }
    }
}