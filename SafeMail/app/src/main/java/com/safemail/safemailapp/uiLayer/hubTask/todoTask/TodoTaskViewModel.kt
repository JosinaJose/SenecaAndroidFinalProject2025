package com.safemail.safemailapp.uiLayer.hubTask.todoTask

import androidx.lifecycle.ViewModel
import com.safemail.safemailapp.dataModels.TaskStatus
import com.safemail.safemailapp.dataModels.TodoTask
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class TodoViewModel : ViewModel() {

    // Backing state (mutable) with correct TaskStatus initialization
    private val _tasks = MutableStateFlow(
        listOf(
            TodoTask(1, "Clean the kitchen", TaskStatus.TODO),
            TodoTask(2, "Update Android Studio", TaskStatus.IN_PROGRESS),
            TodoTask(3, "Go for a run", TaskStatus.COMPLETED)
        )
    )

    // Exposed state (immutable)
    val tasks: StateFlow<List<TodoTask>> = _tasks

    // Add a new task (default status is TODO)
    fun addTask(title: String) {
        if (title.isBlank()) return

        val newTask = TodoTask(
            id = (_tasks.value.maxOfOrNull { it.id } ?: 0) + 1,
            title = title,
            status = TaskStatus.TODO
        )

        _tasks.value = _tasks.value + newTask
    }

    // Update the status of a task
    fun updateTaskStatus(taskId: Int, newStatus: TaskStatus) {
        _tasks.value = _tasks.value.map { task ->
            if (task.id == taskId) {
                task.copy(status = newStatus)
            } else {
                task
            }
        }
    }

    // Delete a task by ID
    fun deleteTask(taskId: Int) {
        _tasks.value = _tasks.value.filterNot { it.id == taskId }
    }
}
