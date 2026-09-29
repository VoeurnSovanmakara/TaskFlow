@file:OptIn(ExperimentalMaterial3Api::class)
package com.jetbrains.taskflow.presentation.task.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jetbrains.taskflow.core.enum.TaskFilter
import com.jetbrains.taskflow.core.enum.TaskSort
import com.jetbrains.taskflow.core.theme.selectableChipColors
import com.jetbrains.taskflow.core.util.displayName
import com.jetbrains.taskflow.domain.model.Task
import com.jetbrains.taskflow.domain.model.TaskStatus
import com.jetbrains.taskflow.presentation.components.EmptyFilteredTaskState
import com.jetbrains.taskflow.presentation.components.EmptyTaskState
import com.jetbrains.taskflow.presentation.components.TaskCard
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    onCreateTask: () -> Unit,
    onTaskClick: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val displayedTasks by viewModel.displayedTasks.collectAsStateWithLifecycle()
    val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

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
            onStatusSelected = { task, status ->
                viewModel.updateTaskStatus(
                    task = task,
                    status = status
                )
            },
            today = today,
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
    today: LocalDate,
    onStatusSelected: (Task, TaskStatus) -> Unit,
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
                            today = today,
                            onClick = {
                                onTaskClick(task.id)
                            },
                            onStatusSelected = { status ->
                                onStatusSelected(task, status)
                            }
                        )
                    }
                }
            }
        }
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
                shape = RoundedCornerShape(12.dp),
                colors = selectableChipColors()
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