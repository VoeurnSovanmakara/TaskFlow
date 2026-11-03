package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.domain.usecase.DeleteTaskUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class TaskDeleter(
    private val deleteTaskUseCase: DeleteTaskUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun delete(
        id: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        scope.launch {
            try {
                deleteTaskUseCase(id)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete task.")
            }
        }
    }
}