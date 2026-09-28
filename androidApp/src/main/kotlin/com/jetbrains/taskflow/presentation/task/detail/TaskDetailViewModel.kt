package com.jetbrains.taskflow.presentation.task.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.usecase.DeleteTaskUseCase
import com.jetbrains.taskflow.domain.usecase.GetTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class TaskDetailViewModel(
    private val getTaskUseCase: GetTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskDetailUiState())

    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

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

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        task = task
                    )
                }

            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = throwable.message
                            ?: "Failed to load task"
                    )
                }
            }
        }
    }

    fun deleteTask(
        taskId: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                deleteTaskUseCase(taskId)
                onSuccess()
            } catch (throwable: Throwable) {
                _uiState.update {
                    it.copy(
                        error = throwable.message
                            ?: "Failed to delete task"
                    )
                }
            }
        }
    }
}
