package com.jetbrains.taskflow.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jetbrains.taskflow.presentation.task.create.CreateTaskScreen
import com.jetbrains.taskflow.presentation.task.detail.TaskDetailScreen
import com.jetbrains.taskflow.presentation.task.list.TaskListScreen
import com.jetbrains.taskflow.presentation.task.create.CreateTaskViewModel
import com.jetbrains.taskflow.presentation.task.detail.TaskDetailViewModel
import com.jetbrains.taskflow.presentation.task.edit.EditTaskScreen
import com.jetbrains.taskflow.presentation.task.edit.EditTaskViewModel
import com.jetbrains.taskflow.presentation.task.list.TaskListViewModel
import org.koin.androidx.compose.koinViewModel

private object Routes {
    const val TASK_LIST = "task_list"
    const val CREATE_TASK = "create_task"
    const val TASK_DETAIL = "task_detail/{taskId}"
    const val TASK_DETAIL_BASE = "task_detail"
    const val EDIT_TASK = "edit_task/{taskId}"
    const val EDIT_TASK_BASE = "edit_task"
}

@Composable
fun TaskFlowNavGraph() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.TASK_LIST
    ) {

        taskListDestination(navController)

        createTaskDestination(navController)

        taskDetailDestination(navController)

        editTaskDestination(navController)
    }
}

private fun NavGraphBuilder.taskListDestination(
    navController: NavHostController
) {
    composable(Routes.TASK_LIST) {

        val viewModel: TaskListViewModel = koinViewModel()

        TaskListScreen(
            viewModel = viewModel,
            onCreateTask = {
                navController.navigate(
                    Routes.CREATE_TASK
                )
            },
            onTaskClick = { taskId ->
                navController.navigate(
                    "${Routes.TASK_DETAIL_BASE}/$taskId"
                )
            }
        )
    }
}

private fun NavGraphBuilder.createTaskDestination(
    navController: NavHostController
) {
    composable(Routes.CREATE_TASK) {

        val viewModel: CreateTaskViewModel = koinViewModel()

        CreateTaskScreen(
            viewModel = viewModel,
            onBack = {
                navController.popBackStack()
            },
            onTaskCreated = {
                navController.popBackStack()
            }
        )
    }
}

private fun NavGraphBuilder.taskDetailDestination(
    navController: NavHostController
) {
    composable(
        route = Routes.TASK_DETAIL,
        arguments = listOf(
            navArgument("taskId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val taskId = backStackEntry.arguments
            ?.getString("taskId")
            ?: return@composable

        val viewModel: TaskDetailViewModel = koinViewModel()

        TaskDetailScreen(
            taskId = taskId,
            viewModel = viewModel,
            onBack = {
                navController.popBackStack()
            },
            onEdit = {
                navController.navigate(
                    "${Routes.EDIT_TASK_BASE}/$taskId"
                )
            },
            onDelete = {
                navController.popBackStack()
            }
        )
    }
}

private fun NavGraphBuilder.editTaskDestination(
    navController: NavHostController
) {
    composable(
        route = Routes.EDIT_TASK,
        arguments = listOf(
            navArgument("taskId") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val taskId = backStackEntry.arguments
            ?.getString("taskId")
            ?: return@composable

        val viewModel: EditTaskViewModel = koinViewModel()

        EditTaskScreen(
            taskId = taskId,
            viewModel = viewModel,
            onBack = {
                navController.popBackStack()
            },
            onTaskUpdated = {
                navController.popBackStack()
            }
        )
    }
}