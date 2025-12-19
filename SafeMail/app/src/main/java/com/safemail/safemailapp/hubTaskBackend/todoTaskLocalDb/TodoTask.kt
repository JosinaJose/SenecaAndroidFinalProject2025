package com.safemail.safemailapp.hubTaskBackend.todoTaskLocalDb

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    COMPLETED
}

@Entity(tableName = "todo_tasks")
data class TodoTask(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,               // Changed to 0 so Room auto-generates it
    val title: String,
    val status: TaskStatus = TaskStatus.TODO,
    val adminEmail: String,        // Added this!
    val createdAt: Long = System.currentTimeMillis() // Added this!
)