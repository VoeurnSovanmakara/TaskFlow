package com.jetbrains.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.jetbrains.taskflow.navigation.TaskFlowNavGraph
import com.jetbrains.taskflow.presentation.task.TaskViewModel
import com.jetbrains.taskflow.presentation.task.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: TaskViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            TaskViewModelFactory()
        )[TaskViewModel::class.java]

        setContent {
            TaskFlowNavGraph(
                viewModel = viewModel,
            )
        }
    }
}
