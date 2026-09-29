package com.jetbrains.taskflow.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val textAndTrailing: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
            )
            if (trailingContent != null) {
                Spacer(Modifier.width(4.dp))
                trailingContent()
            }
        }
    }

    if (onClick != null) {
        Surface(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(50),
            color = container,
            contentColor = content,
        ) {
            textAndTrailing()
        }
    } else {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(50),
            color = container,
            contentColor = content,
        ) {
            textAndTrailing()
        }
    }
}

@Composable
fun StatusBadge(
    status: TaskStatus,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (status.name) {
        "TODO" -> scheme.secondaryContainer to scheme.onSecondaryContainer
        "IN_PROGRESS" -> scheme.primaryContainer to scheme.onPrimaryContainer
        "COMPLETED" -> scheme.tertiaryContainer to scheme.onTertiaryContainer
        else -> scheme.surfaceVariant to scheme.onSurfaceVariant
    }
    TaskBadge(
        label = status.prettyName(),
        container = container,
        content = content,
        modifier = modifier,
        onClick = onClick,
        trailingContent = if (onClick != null) {
            { Text("▾", style = MaterialTheme.typography.labelSmall) }
        } else {
            null
        },
    )
}

@Composable
fun PriorityBadge(
    priority: TaskPriority,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val scheme = MaterialTheme.colorScheme
    val (container, content) = when (priority.name) {
        "HIGH" -> scheme.error to scheme.onError
        "MEDIUM" -> scheme.surfaceContainerHighest to scheme.onSurface
        else -> scheme.surfaceContainerHigh to scheme.onSurfaceVariant
    }
    TaskBadge(
        label = priority.prettyName(),
        container,
        content,
        modifier,
        onClick = onClick,
        trailingContent = if (onClick != null) {
            { Text("▾", style = MaterialTheme.typography.labelSmall) }
        } else {
            null
        }
    )
}