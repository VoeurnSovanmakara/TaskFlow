package com.jetbrains.taskflow.presentation.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jetbrains.taskflow.domain.usecase.CreateTaskUseCase
import com.jetbrains.taskflow.domain.usecase.ObserveTasksUseCase
import org.koin.core.context.GlobalContext

class TaskViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            val koin = GlobalContext.get()

            return TaskViewModel(
                observeTasksUseCase = koin.get(),
                createTaskUseCase = koin.get(),
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}