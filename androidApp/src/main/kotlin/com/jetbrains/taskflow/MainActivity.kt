package com.jetbrains.taskflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jetbrains.taskflow.core.theme.TaskFlowTheme
import com.jetbrains.taskflow.navigation.TaskFlowNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        setContent {
            TaskFlowTheme {
                TaskFlowNavGraph()
            }
        }
    }
}
