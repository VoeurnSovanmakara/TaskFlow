@file:OptIn(ExperimentalMaterial3Api::class)
package com.jetbrains.taskflow.presentation.task.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.taskflow.core.enum.TaskFilter
import com.jetbrains.taskflow.core.enum.TaskSort
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.presentation.components.PriorityBadge
import com.jetbrains.taskflow.presentation.components.StatusBadge

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onCreateTask: () -> Unit,
    onTaskClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val displayedTasks by viewModel.displayedTasks.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tasks",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                    )
                },
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateTask
            ) {
                Text("+ New task")
            }
        }
    ) { paddingValues ->

        TaskListContent(
            uiState = uiState,
            displayedTasks = displayedTasks,
            onFilterSelected = viewModel::setFilter,
            onSortSelected = viewModel::setSort,
            onCreateTask = onCreateTask,
            onTaskClick = onTaskClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
fun TaskListContent(
    uiState: TaskListUiState,
    displayedTasks: List<Task>,
    onFilterSelected: (TaskFilter) -> Unit,
    onSortSelected: (TaskSort) -> Unit,
    onCreateTask: () -> Unit,
    onTaskClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (!uiState.isLoading && uiState.error == null && uiState.tasks.isNotEmpty()) {
            TaskFilterRow(
                selectedFilter = uiState.selectedFilter,
                onFilterSelected = onFilterSelected
            )
            TaskSortRow(
                selectedSort = uiState.selectedSort,
                onSortSelected = onSortSelected
            )
        }

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                }
            }

            uiState.tasks.isEmpty() -> {
                EmptyTaskState(
                    onCreateTask = onCreateTask,
                    modifier = Modifier.fillMaxSize()
                )
            }

            displayedTasks.isEmpty() -> {
                EmptyFilteredTaskState(
                    filter = uiState.selectedFilter,
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    // extra bottom padding so the FAB never covers the last card
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 96.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = displayedTasks,
                        key = { task -> task.id }
                    ) { task ->
                        TaskCard(
                            task = task,
                            onClick = {
                                onTaskClick(task.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: Task,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )

            task.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            TaskMetaData(task = task)
        }
    }
}

@Composable
private fun TaskMetaData(task: Task) {
    Row(
        modifier = Modifier.padding(top = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatusBadge(task.status)
        PriorityBadge(task.priority)
    }
}

@Composable
private fun EmptyTaskState(
    onCreateTask: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ) {
                Text(
                    text = "✓",
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp)
                )
            }

            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Create your first task to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.width(4.dp))

            Button(
                onClick = onCreateTask,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Create task")
            }
        }
    }
}

@Composable
private fun EmptyFilteredTaskState(
    filter: TaskFilter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No ${filter.displayName().lowercase()} tasks",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TaskFilterRow(
    selectedFilter: TaskFilter,
    onFilterSelected: (TaskFilter) -> Unit,
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(TaskFilter.entries) { filter ->
            val selected = selectedFilter == filter
            FilterChip(
                selected = selected,
                onClick = {
                    onFilterSelected(filter)
                },
                label = {
                    Text(
                        text = filter.displayName()
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun TaskSortRow(
    selectedSort: TaskSort,
    onSortSelected: (TaskSort) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Box {
            AssistChip(
                onClick = {
                    expanded = true
                },
                shape = RoundedCornerShape(12.dp),
                label = {
                    Text("Sort: ${selectedSort.displayName()}")
                },
                trailingIcon = {
                    Text(
                        text = "▾",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                shape = RoundedCornerShape(16.dp)
            ) {
                TaskSort.entries.forEach { sort ->
                    val selected = sort == selectedSort

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = sort.displayName(),
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        },
                        onClick = {
                            onSortSelected(sort)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

private fun TaskFilter.displayName(): String {
    return when (this) {
        TaskFilter.ALL -> "All"
        TaskFilter.TODO -> "Todo"
        TaskFilter.IN_PROGRESS -> "In Progress"
        TaskFilter.COMPLETED -> "Completed"
    }
}

private fun TaskSort.displayName(): String {
    return when (this) {
        TaskSort.CREATED_DATE -> "Created date"
        TaskSort.DUE_DATE -> "Due date"
        TaskSort.PRIORITY -> "Priority"
        TaskSort.TITLE -> "Title"
    }
}