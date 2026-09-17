package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.data.database.TaskDatabase
import com.jetbrains.taskflow.data.database.createDatabase
import com.jetbrains.taskflow.data.repository.TaskRepositoryImpl
import com.jetbrains.taskflow.domain.repository.TaskRepository
import com.jetbrains.taskflow.domain.usecase.CreateTaskUseCase
import com.jetbrains.taskflow.domain.usecase.DeleteTaskUseCase
import com.jetbrains.taskflow.domain.usecase.GetTaskUseCase
import com.jetbrains.taskflow.domain.usecase.ObserveTasksUseCase
import com.jetbrains.taskflow.domain.usecase.UpdateTaskUseCase
import org.koin.dsl.module

val appModule = module{

    // Database
    single<TaskDatabase> { createDatabase( builder = get()) }

    // Repository
    single<TaskRepository> {
        TaskRepositoryImpl(
            taskDao = get<TaskDatabase>().taskDao()
        )
    }

    // Use Cases
    factory {
        CreateTaskUseCase( taskRepository = get() )
    }

    factory {
        UpdateTaskUseCase( taskRepository = get() )
    }

    factory {
        DeleteTaskUseCase( taskRepository = get() )
    }

    factory {
        GetTaskUseCase( taskRepository = get() )
    }

    factory {
        ObserveTasksUseCase( taskRepository = get() )
    }
}