package com.jetbrains.taskflow.di

import com.jetbrains.taskflow.data.database.getDatabaseBuilder
import com.jetbrains.taskflow.domain.usecase.CreateProjectUseCase
import com.jetbrains.taskflow.domain.usecase.CreateTaskUseCase
import com.jetbrains.taskflow.domain.usecase.DeleteTaskUseCase
import com.jetbrains.taskflow.domain.usecase.ObserveProjectsUseCase
import com.jetbrains.taskflow.domain.usecase.ObserveTasksUseCase
import com.jetbrains.taskflow.domain.usecase.UpdateTaskUseCase
import org.koin.mp.KoinPlatform

fun initKoinIos() {
    initKoin(
        databaseBuilder = getDatabaseBuilder()
    )
}

// Task
fun createTaskListObserver(): TaskListObserver {
    val observeTasks = KoinPlatform.getKoin()
        .get<ObserveTasksUseCase>()

    return TaskListObserver(observeTasks)
}

fun createTaskCreator(): TaskCreator {
    val useCase = KoinPlatform.getKoin().get<CreateTaskUseCase>()
    return TaskCreator(useCase)
}

fun createTaskDeleter(): TaskDeleter {
    val deleteTaskUseCase =
        KoinPlatform.getKoin().get<DeleteTaskUseCase>()

    return TaskDeleter(deleteTaskUseCase)
}

fun createTaskUpdater(): TaskUpdater {
    val updateTaskUseCase =
        KoinPlatform.getKoin().get<UpdateTaskUseCase>()

    return TaskUpdater(updateTaskUseCase)
}

// Project
fun createProjectListObserver(): ProjectListObserver {
    return ProjectListObserver(
        observeProjects = KoinPlatform.getKoin()
            .get<ObserveProjectsUseCase>()
    )
}

fun createProjectCreator(): ProjectCreator {
    return ProjectCreator(
        createProject = KoinPlatform.getKoin()
            .get<CreateProjectUseCase>()
    )
}