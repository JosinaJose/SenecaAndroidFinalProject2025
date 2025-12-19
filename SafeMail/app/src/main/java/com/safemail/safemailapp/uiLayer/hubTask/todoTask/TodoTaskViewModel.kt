package com.safemail.safemailapp.uiLayer.hubTask.todoTask

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TaskStatus
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoDao
import com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb.TodoTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoDao: TodoDao,
    private val adminEmail: String
) : ViewModel() {

    // 1. Observe tasks with a safety catch
    // The .catch block prevents database mismatches from crashing the UI thread
    val tasks: StateFlow<List<TodoTask>> = todoDao.getTasksForAdmin(adminEmail)
        .catch { e ->
            Log.e("TodoViewModel", "Error fetching tasks for $adminEmail: ${e.message}")
            emit(emptyList()) // Provide an empty list instead of crashing
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 2. Add a task to Room
    fun addTask(title: String) {
        if (title.isBlank()) return

        viewModelScope.launch {
            try {
                val newTask = TodoTask(
                    title = title,
                    status = TaskStatus.TODO,
                    adminEmail = adminEmail, // Links task to current admin
                    createdAt = System.currentTimeMillis() //
                )
                todoDao.insertTask(newTask)
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Failed to insert task: ${e.message}")
            }
        }
    }

    // 3. Update status in Room
    fun updateTaskStatus(taskId: Int, newStatus: TaskStatus) {
        viewModelScope.launch {
            try {
                tasks.value.find { it.id == taskId }?.let { task ->
                    todoDao.updateTask(task.copy(status = newStatus))
                }
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Failed to update task: ${e.message}")
            }
        }
    }

    // 4. Delete from Room
    fun deleteTask(taskId: Int) {
        viewModelScope.launch {
            try {
                todoDao.deleteTaskById(taskId)
            } catch (e: Exception) {
                Log.e("TodoViewModel", "Failed to delete task: ${e.message}")
            }
        }
    }
}