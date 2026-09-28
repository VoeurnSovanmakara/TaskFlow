package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.domain.usecase.CreateTaskUseCase
import com.jetbrains.taskflow.domain.usecase.DeleteTaskUseCase
import com.jetbrains.taskflow.domain.usecase.GetTaskUseCase
import com.jetbrains.taskflow.domain.usecase.ObserveTasksUseCase
import com.jetbrains.taskflow.domain.usecase.UpdateTaskUseCase
import com.jetbrains.taskflow.presentation.task.create.CreateTaskViewModel
import com.jetbrains.taskflow.presentation.task.detail.TaskDetailViewModel
import com.jetbrains.taskflow.presentation.task.edit.EditTaskViewModel
import com.jetbrains.taskflow.presentation.task.list.TaskListViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        TaskListViewModel(
            observeTasksUseCase = get<ObserveTasksUseCase>()
        )
    }
    viewModel {
        CreateTaskViewModel(
            createTaskUseCase = get<CreateTaskUseCase>()
        )
    }

    viewModel {
        TaskDetailViewModel(
            getTaskUseCase = get<GetTaskUseCase>(),
            deleteTaskUseCase = get<DeleteTaskUseCase>()
        )
    }

    viewModel {
        EditTaskViewModel(
            getTaskUseCase = get<GetTaskUseCase>(),
            updateTaskUseCase = get<UpdateTaskUseCase>()
        )
    }
}