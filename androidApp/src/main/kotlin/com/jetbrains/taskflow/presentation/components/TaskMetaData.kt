package com.jetbrains.taskflow.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskStatus

@Composable
fun TaskMetaData(
    task: Task,
    onStatusSelected: (TaskStatus) -> Unit,
) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatusMenu(
            status = task.status,
            onStatusSelected = onStatusSelected,
        )
        PriorityBadge(task.priority)
    }
}