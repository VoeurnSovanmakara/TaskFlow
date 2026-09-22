@file:OptIn(ExperimentalMaterial3Api::class)

package com.jetbrains.taskflow.presentation.task

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onCreateTask: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Tasks")
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateTask
            ) {
                Text("+")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TaskListContent(
                uiState = uiState,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
    }
}

@Composable
fun TaskListContent(
    uiState: TaskListUiState,
    modifier: Modifier = Modifier,
) {
    when {
        uiState.isLoading -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Text(
                text = uiState.error,
                modifier = modifier
            )
        }

        uiState.tasks.isEmpty() -> {
            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                Text("No tasks yet")
            }
        }

        else -> {
            LazyColumn(
                modifier = modifier,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = uiState.tasks,
                    key = { task -> task.id }
                ) { task ->

                    Column {
                        Text(
                            text = task.title,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        task.description?.let { description ->
                            Text(
                                text = description,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        Text(
                            text = "${task.status} • ${task.priority}"
                        )
                    }
                }
            }
        }
    }
}