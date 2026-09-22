package com.jetbrains.taskflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jetbrains.taskflow.presentation.task.CreateTaskScreen
import com.jetbrains.taskflow.presentation.task.TaskListScreen
import com.jetbrains.taskflow.presentation.task.TaskViewModel

private object Routes  {
    const val TASK_LIST = "task_list"
    const val CREATE_TASK = "create_task"
}

@Composable
fun TaskFlowNavGraph(
    viewModel: TaskViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.TASK_LIST
    ) {
        composable(Routes.TASK_LIST) {
            TaskListScreen(
                viewModel = viewModel,
                onCreateTask = {
                    navController.navigate(Routes.CREATE_TASK)
                }
            )
        }

        composable(Routes.CREATE_TASK) {
            CreateTaskScreen(
                viewModel = viewModel,
                onTaskCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}