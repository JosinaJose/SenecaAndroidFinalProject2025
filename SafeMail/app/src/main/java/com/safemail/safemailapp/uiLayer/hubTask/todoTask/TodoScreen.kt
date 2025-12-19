package com.safemail.safemailapp.uiLayer.hubTask.todoTask

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TaskStatus
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoTask

@Composable
fun TodoScreen(
    viewModel: TodoViewModel = viewModel(),
    onBack: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsState()
    TodoContent(
        tasks = tasks,
        onAddTask = { viewModel.addTask(it) },
        onUpdateStatus = { id, status -> viewModel.updateTaskStatus(id, status) },
        onDeleteTask = { viewModel.deleteTask(it) },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoContent(
    tasks: List<TodoTask>,
    onAddTask: (String) -> Unit,
    onUpdateStatus: (Int, TaskStatus) -> Unit,
    onDeleteTask: (Int) -> Unit,
    onBack: () -> Unit
) {
    var textFieldState by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Task Workspace", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tasks yet. Tap + to start!", color = Color.Gray)
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(tasks, key = { it.id }) { task ->
                    TodoItem(
                        task = task,
                        onStatusChange = { status -> onUpdateStatus(task.id, status) },
                        onDelete = { onDeleteTask(task.id) }
                    )
                }
            }
        }

        // Modern Add Task Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("New Task") },
                text = {
                    OutlinedTextField(
                        value = textFieldState,
                        onValueChange = { textFieldState = it },
                        label = { Text("What needs to be done?") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        if (textFieldState.isNotBlank()) {
                            onAddTask(textFieldState)
                            textFieldState = ""
                            showAddDialog = false
                        }
                    }) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun TodoItem(
    task: TodoTask,
    onStatusChange: (TaskStatus) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                ModernStatusDropdown(currentStatus = task.status, onStatusChange = onStatusChange)
            }
            IconButton(
                onClick = onDelete,
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun ModernStatusDropdown(
    currentStatus: TaskStatus,
    onStatusChange: (TaskStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    // Visual colors based on status
    val badgeColor = when (currentStatus) {
        TaskStatus.COMPLETED -> Color(0xFF4CAF50)
        TaskStatus.IN_PROGRESS -> Color(0xFF2196F3)
        TaskStatus.BLOCKED -> Color(0xFFF44336)
        else -> Color.Gray
    }

    Box {
        Surface(
            onClick = { expanded = true },
            color = badgeColor.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
        ) {
            Text(
                text = currentStatus.name.replace("_", " "),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                color = badgeColor
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TaskStatus.values().forEach { status ->
                DropdownMenuItem(
                    text = { Text(status.name.replace("_", " ")) },
                    onClick = {
                        onStatusChange(status)
                        expanded = false
                    }
                )
            }
        }
    }
}

