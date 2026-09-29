package com.jetbrains.taskflow.presentation.task.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.usecase.ObserveTasksUseCase
import com.jetbrains.taskflow.core.enum.TaskFilter
import com.jetbrains.taskflow.core.enum.TaskSort
import com.jetbrains.taskflow.core.extention.filterBy
import com.jetbrains.taskflow.core.extention.sortBy
import com.jetbrains.taskflow.domain.model.TaskStatus
import com.jetbrains.taskflow.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Clock

class TaskListViewModel(
    private val observeTasksUseCase: ObserveTasksUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(TaskListUiState())
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    val displayedTasks: StateFlow<List<Task>> =
        uiState
            .map { state ->
                state.tasks
                    .filterBy(state.selectedFilter)
                    .sortBy(state.selectedSort)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            observeTasksUseCase()
                .onStart {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                        )
                    }
                }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message
                                ?: "Failed to load tasks"
                        )
                    }
                }
                .collect { tasks ->
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

    fun setFilter(filter: TaskFilter) {
        _uiState.update {
            it.copy( selectedFilter = filter )
        }
    }

    fun setSort(sort: TaskSort) {
        _uiState.update {
            it.copy( selectedSort = sort )
        }
    }

    fun updateTaskStatus(
        task: Task,
        status: TaskStatus,
    ) {
        viewModelScope.launch {
            try {
                updateTaskUseCase(
                    task.copy(
                        status = status,
                        updatedAt = Clock.System.now()
                    )
                )
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message ?: "Failed to update task"
                    )
                }
            }
        }
    }

}