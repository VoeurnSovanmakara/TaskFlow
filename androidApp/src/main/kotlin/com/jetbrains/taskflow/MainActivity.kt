package com.jetbrains.taskflow

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jetbrains.taskflow.core.theme.TaskFlowTheme
import com.jetbrains.taskflow.navigation.TaskFlowNavGraph
import com.jetbrains.taskflow.notification.TaskReminderReceiver

class MainActivity : ComponentActivity() {
    private var pendingTaskId by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        pendingTaskId = intent?.getStringExtra(
            TaskReminderReceiver.EXTRA_TASK_ID
        )

        setContent {
            TaskFlowTheme {
                TaskFlowNavGraph(
                    initialTaskId = pendingTaskId,
                    onTaskNavigationHandled = {
                        pendingTaskId = null
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        pendingTaskId = intent.getStringExtra(
            TaskReminderReceiver.EXTRA_TASK_ID
        )
    }
}
