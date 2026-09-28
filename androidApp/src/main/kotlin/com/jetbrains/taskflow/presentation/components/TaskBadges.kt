package com.jetbrains.taskflow.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jetbrains.taskflow.domain.model.TaskPriority
import com.jetbrains.taskflow.domain.model.TaskStatus

fun Enum<*>.prettyName(): String =
    name.split('_').joinToString(" ") { part ->
        part.lowercase().replaceFirstChar { it.uppercase() }
    }

@Composable
fun TaskBadge(
    label: String,
    container: Color,
    content: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = container,
        contentColor = content,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
        )
    }
}

@Composable
fun StatusBadge(status: TaskStatus, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (status.name) {
        "TODO" -> scheme.secondaryContainer to scheme.onSecondaryContainer
        "IN_PROGRESS" -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        "COMPLETED" -> scheme.primaryContainer to scheme.onPrimaryContainer
        else -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
    TaskBadge(status.prettyName(), container, content, modifier)
}

@Composable
fun PriorityBadge(priority: TaskPriority, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (priority.name) {
        "HIGH" -> scheme.errorContainer to scheme.onErrorContainer
        "MEDIUM" -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        else -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
    TaskBadge(priority.prettyName(), container, content, modifier)
}